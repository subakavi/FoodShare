package com.foodshare.user;

import com.foodshare.FoodShareApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FoodShareApplication.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository users;

    @BeforeEach
    void setUp() {
        users.deleteAll();
    }

    @Test
    void registrationPageShouldLoad() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void validUserShouldRegisterSuccessfully() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Test Donor")
                        .param("email", "donor@example.com")
                        .param("password", "password123")
                        .param("role", "DONOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        org.junit.jupiter.api.Assertions.assertTrue(
                users.existsByEmailIgnoreCase("donor@example.com"));
    }

    @Test
    void invalidEmailShouldBeRejected() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Test Donor")
                        .param("email", "invalid-email")
                        .param("password", "password123")
                        .param("role", "DONOR"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));

        org.junit.jupiter.api.Assertions.assertEquals(0, users.count());
    }

    @Test
    void shortPasswordShouldBeRejected() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "Test Donor")
                        .param("email", "donor@example.com")
                        .param("password", "abc123")
                        .param("role", "DONOR"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));

        org.junit.jupiter.api.Assertions.assertEquals(0, users.count());
    }

    @Test
    void duplicateEmailShouldBeRejected() throws Exception {
        mockMvc.perform(post("/register")
                .param("fullName", "First Donor")
                .param("email", "donor@example.com")
                .param("password", "password123")
                .param("role", "DONOR"));

        mockMvc.perform(post("/register")
                        .param("fullName", "Second Donor")
                        .param("email", "donor@example.com")
                        .param("password", "password456")
                        .param("role", "DONOR"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));

        org.junit.jupiter.api.Assertions.assertEquals(1, users.count());
    }

    @Test
    void dashboardShouldRedirectWhenUserIsNotLoggedIn() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}