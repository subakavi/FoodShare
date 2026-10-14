package com.foodshare.listing;

import com.foodshare.FoodShareApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FoodShareApplication.class)
@AutoConfigureMockMvc
class FoodListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FoodListingRepository listings;

    @BeforeEach
    void setUp() {
        listings.deleteAll();
    }

    @Test
    void foodListingsShouldRedirectWhenUserIsNotLoggedIn()
            throws Exception {

        mockMvc.perform(get("/food-listings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void donorShouldOpenFoodListingPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userEmail", "donor@example.com");
        session.setAttribute("userRole", "DONOR");

        mockMvc.perform(get("/food-listings").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("food-listings"));
    }

    @Test
    void donorShouldCreateValidFoodListing() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userEmail", "donor@example.com");
        session.setAttribute("userRole", "DONOR");

        mockMvc.perform(post("/food-listings/save")
                        .session(session)
                        .param("foodName", "Vegetable Rice")
                        .param("description", "Fresh food")
                        .param("quantity", "10")
                        .param("pickupLocation", "Vellore")
                        .param("expiryDate",
                                LocalDate.now().plusDays(2).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/food-listings?saved"));

        assertEquals(1, listings.count());

        FoodListing saved = listings.findAll().get(0);
        assertEquals("Vegetable Rice", saved.getFoodName());
        assertEquals(10, saved.getQuantity());
        assertEquals("donor@example.com", saved.getDonorEmail());
        assertEquals(ListingStatus.AVAILABLE, saved.getStatus());
    }

    @Test
    void quantityBelowMinimumShouldBeRejected() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userEmail", "donor@example.com");
        session.setAttribute("userRole", "DONOR");

        mockMvc.perform(post("/food-listings/save")
                        .session(session)
                        .param("foodName", "Rice")
                        .param("quantity", "0")
                        .param("pickupLocation", "Vellore")
                        .param("expiryDate",
                                LocalDate.now().plusDays(2).toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("food-listing-form"))
                .andExpect(model().attributeExists("error"));

        assertEquals(0, listings.count());
    }

    @Test
    void quantityAboveMaximumShouldBeRejected() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userEmail", "donor@example.com");
        session.setAttribute("userRole", "DONOR");

        mockMvc.perform(post("/food-listings/save")
                        .session(session)
                        .param("foodName", "Rice")
                        .param("quantity", "501")
                        .param("pickupLocation", "Vellore")
                        .param("expiryDate",
                                LocalDate.now().plusDays(2).toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("food-listing-form"))
                .andExpect(model().attributeExists("error"));

        assertEquals(0, listings.count());
    }

    @Test
    void nonDonorShouldNotCreateFoodListing() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userEmail", "recipient@example.com");
        session.setAttribute("userRole", "RECIPIENT");

        mockMvc.perform(post("/food-listings/save")
                        .session(session)
                        .param("foodName", "Rice")
                        .param("quantity", "10")
                        .param("pickupLocation", "Vellore")
                        .param("expiryDate",
                                LocalDate.now().plusDays(2).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/food-listings?error=donorOnly"));

        assertEquals(0, listings.count());
    }
}