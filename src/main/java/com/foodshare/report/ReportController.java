package com.foodshare.report;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {
    private final ReportService reports;
    public ReportController(ReportService reports) { this.reports=reports; }
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/login";
        model.addAttribute("summary", reports.summary());
        return "reports";
    }
}
