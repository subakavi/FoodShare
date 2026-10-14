package com.foodshare.matching;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchingController {
    private final MatchingService matchingService;
    public MatchingController(MatchingService matchingService) { this.matchingService = matchingService; }
    @GetMapping("/matching")
    public String matching(HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/login";
        model.addAttribute("listings", matchingService.findEligibleListings());
        model.addAttribute("userRole", session.getAttribute("userRole"));
        return "matching";
    }
}
