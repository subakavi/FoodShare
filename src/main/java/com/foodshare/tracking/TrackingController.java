package com.foodshare.tracking;

import com.foodshare.request.DonationRequest;
import com.foodshare.request.DonationRequestRepository;
import com.foodshare.request.RequestStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tracking")
public class TrackingController {
    private final DonationRequestRepository requests;
    private final CollectionRepository records;
    public TrackingController(DonationRequestRepository requests, CollectionRepository records) { this.requests=requests; this.records=records; }
    @GetMapping
    public String tracking(HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/login";
        model.addAttribute("requests", requests.findAllByOrderByCreatedAtDesc());
        model.addAttribute("records", records.findAllByOrderByUpdatedAtDesc());
        model.addAttribute("userRole", session.getAttribute("userRole"));
        return "collection-tracking";
    }
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        Object emailObj=session.getAttribute("userEmail");
        if (emailObj == null) return "redirect:/login";
        if (!"DONOR".equals(session.getAttribute("userRole"))) return "redirect:/tracking?error=donorOnly";
        DonationRequest request=requests.findById(id).orElse(null);
        if (request == null) return "redirect:/tracking?error=notFound";
        RequestStatus next;
        try { next=RequestStatus.valueOf(status); } catch (IllegalArgumentException ex) { return "redirect:/tracking?error=invalidStatus"; }
        boolean valid = (request.getStatus()==RequestStatus.APPROVED && next==RequestStatus.COLLECTED)
                || (request.getStatus()==RequestStatus.COLLECTED && next==RequestStatus.DISTRIBUTED);
        if (!valid) return "redirect:/tracking?error=invalidTransition";
        request.setStatus(next); requests.save(request);
        CollectionStatus cs = next==RequestStatus.COLLECTED ? CollectionStatus.COLLECTED : CollectionStatus.DISTRIBUTED;
        CollectionRecord record=records.findByDonationRequestId(id).orElse(new CollectionRecord(id,emailObj.toString(),cs));
        record.setUpdatedBy(emailObj.toString()); record.setStatus(cs); record.setUpdatedAt(java.time.LocalDateTime.now()); records.save(record);
        return "redirect:/tracking?updated";
    }
}
