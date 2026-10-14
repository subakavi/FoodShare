package com.foodshare.request;

import com.foodshare.listing.FoodListing;
import com.foodshare.listing.FoodListingRepository;
import com.foodshare.listing.ListingStatus;
import com.foodshare.matching.MatchingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/requests")
public class DonationRequestController {
    private final DonationRequestRepository requests;
    private final FoodListingRepository listings;
    private final MatchingService matchingService;

    public DonationRequestController(DonationRequestRepository requests, FoodListingRepository listings, MatchingService matchingService) {
        this.requests = requests; this.listings = listings; this.matchingService = matchingService;
    }
    private String email(HttpSession session) { Object v = session.getAttribute("userEmail"); return v == null ? null : v.toString(); }
    private String role(HttpSession session) { Object v = session.getAttribute("userRole"); return v == null ? null : v.toString(); }

    @GetMapping
    public String list(HttpSession session, Model model) {
        String email = email(session); if (email == null) return "redirect:/login";
        boolean donor = "DONOR".equals(role(session));
        model.addAttribute("requests", donor ? requests.findAllByOrderByCreatedAtDesc() : requests.findByRequesterEmailOrderByCreatedAtDesc(email));
        model.addAttribute("isDonor", donor);
        model.addAttribute("userEmail", email);
        return "donation-requests";
    }

    @PostMapping("/create/{listingId}")
    public String create(@PathVariable Long listingId, @RequestParam Integer requestedQuantity, HttpSession session) {
        String email = email(session); if (email == null) return "redirect:/login";
        if (!"RECIPIENT".equals(role(session))) return "redirect:/requests?error=recipientOnly";
        FoodListing listing = listings.findById(listingId).orElse(null);
        if (listing == null || listing.getStatus() != ListingStatus.AVAILABLE || listing.getExpiryDate().isBefore(LocalDate.now())) return "redirect:/matching?error=unavailable";
        if (requestedQuantity == null || requestedQuantity < 1 || requestedQuantity > listing.getQuantity()) return "redirect:/matching?error=quantity";
        boolean duplicate = requests.existsByFoodListingIdAndRequesterEmailAndStatusIn(listingId, email, List.of(RequestStatus.REQUESTED, RequestStatus.APPROVED, RequestStatus.COLLECTED));
        if (duplicate) return "redirect:/requests?error=duplicate";
        requests.save(new DonationRequest(listingId, email, requestedQuantity));
        return "redirect:/requests?created";
    }

    @PostMapping("/{id}/decision")
    public String decision(@PathVariable Long id, @RequestParam String action, HttpSession session) {
        if (email(session) == null) return "redirect:/login";
        if (!"DONOR".equals(role(session))) return "redirect:/requests?error=donorOnly";
        DonationRequest request = requests.findById(id).orElse(null);
        if (request == null || request.getStatus() != RequestStatus.REQUESTED) return "redirect:/requests?error=invalidStatus";
        if ("approve".equals(action)) request.setStatus(RequestStatus.APPROVED);
        else if ("reject".equals(action)) request.setStatus(RequestStatus.REJECTED);
        else return "redirect:/requests?error=invalidAction";
        requests.save(request);
        return "redirect:/requests?updated";
    }
}
