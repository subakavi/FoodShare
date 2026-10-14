package com.foodshare.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserAccountRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("roles", UserRole.values());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam UserRole role,
                           Model model) {
        fullName = fullName == null ? "" : fullName.trim();
        email = email == null ? "" : email.trim().toLowerCase();

        if (fullName.isBlank() || email.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "All fields are required.");
            model.addAttribute("roles", UserRole.values());
            return "register";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            model.addAttribute("error", "Enter a valid email address.");
            model.addAttribute("roles", UserRole.values());
            return "register";
        }
        if (password.length() < 8) {
            model.addAttribute("error", "Password must contain at least 8 characters.");
            model.addAttribute("roles", UserRole.values());
            return "register";
        }
        if (users.existsByEmailIgnoreCase(email)) {
            model.addAttribute("error", "An account with this email already exists.");
            model.addAttribute("roles", UserRole.values());
            return "register";
        }

        UserAccount user = new UserAccount(fullName, email, passwordEncoder.encode(password), role);
        users.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        UserAccount user = users.findByEmailIgnoreCase(email == null ? "" : email.trim())
                .orElse(null);

        if (user == null || password == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }

        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("userRole", user.getRole().name());
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/login";
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userRole", session.getAttribute("userRole"));
        return "dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
