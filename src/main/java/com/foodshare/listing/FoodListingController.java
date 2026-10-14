package com.foodshare.listing;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/food-listings")
public class FoodListingController {
    private final FoodListingRepository listings;

    public FoodListingController(FoodListingRepository listings) {
        this.listings = listings;
    }

    private String signedInEmail(HttpSession session) {
        Object email = session.getAttribute("userEmail");
        return email == null ? null : email.toString();
    }

    private boolean isDonor(HttpSession session) {
        return "DONOR".equals(session.getAttribute("userRole"));
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        String email = signedInEmail(session);
        if (email == null) return "redirect:/login";
        model.addAttribute("listings", listings.findAll());
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userEmail", email);
        return "food-listings";
    }

    @GetMapping("/new")
    public String newListing(HttpSession session, Model model) {
        if (signedInEmail(session) == null) return "redirect:/login";
        if (!isDonor(session)) return "redirect:/food-listings?error=donorOnly";
        model.addAttribute("listing", new FoodListing());
        model.addAttribute("today", LocalDate.now());
        return "food-listing-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute FoodListing listing, HttpSession session, Model model) {
        String email = signedInEmail(session);
        if (email == null) return "redirect:/login";
        if (!isDonor(session)) return "redirect:/food-listings?error=donorOnly";

        String foodName = listing.getFoodName() == null ? "" : listing.getFoodName().trim();
        String location = listing.getPickupLocation() == null ? "" : listing.getPickupLocation().trim();

        if (foodName.isBlank() || location.isBlank() || listing.getQuantity() == null
                || listing.getExpiryDate() == null) {
            model.addAttribute("error", "Food name, quantity, pickup location and expiry date are required.");
            model.addAttribute("listing", listing);
            model.addAttribute("today", LocalDate.now());
            return "food-listing-form";
        }
        if (listing.getQuantity() < 1 || listing.getQuantity() > 500) {
            model.addAttribute("error", "Quantity must be between 1 and 500 units.");
            model.addAttribute("listing", listing);
            model.addAttribute("today", LocalDate.now());
            return "food-listing-form";
        }
        if (listing.getExpiryDate().isBefore(LocalDate.now())) {
            model.addAttribute("error", "Expiry date cannot be in the past.");
            model.addAttribute("listing", listing);
            model.addAttribute("today", LocalDate.now());
            return "food-listing-form";
        }

        listing.setFoodName(foodName);
        listing.setPickupLocation(location);
        listing.setDonorEmail(email);
        listing.setStatus(ListingStatus.AVAILABLE);
        listings.save(listing);
        return "redirect:/food-listings?saved";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        String email = signedInEmail(session);
        if (email == null) return "redirect:/login";
        if (!isDonor(session)) return "redirect:/food-listings?error=donorOnly";

        listings.findById(id).filter(item -> item.getDonorEmail().equals(email))
                .ifPresent(listings::delete);
        return "redirect:/food-listings?deleted";
    }
}
