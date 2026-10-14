package com.foodshare.tracking;

import com.foodshare.FoodShareApplication;
import com.foodshare.request.DonationRequest;
import com.foodshare.request.DonationRequestRepository;
import com.foodshare.request.RequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FoodShareApplication.class)
@AutoConfigureMockMvc
class TrackingControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired DonationRequestRepository requests;
    @Autowired CollectionRepository records;

    @BeforeEach void clean() { records.deleteAll(); requests.deleteAll(); }

    @Test void unauthenticatedUserIsRedirected() throws Exception {
        mockMvc.perform(get("/tracking")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
    }

    @Test void donorCanMoveApprovedRequestToCollected() throws Exception {
        DonationRequest request = requests.save(new DonationRequest(1L, "recipient@example.com", 2));
        request.setStatus(RequestStatus.APPROVED); requests.save(request);
        MockHttpSession session = new MockHttpSession(); session.setAttribute("userEmail", "donor@example.com"); session.setAttribute("userRole", "DONOR");
        mockMvc.perform(post("/tracking/" + request.getId() + "/update").session(session).param("status", "COLLECTED"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/tracking?updated"));
        assertEquals(RequestStatus.COLLECTED, requests.findById(request.getId()).orElseThrow().getStatus());
        assertEquals(1, records.count());
    }

    @Test void invalidTransitionIsRejected() throws Exception {
        DonationRequest request = requests.save(new DonationRequest(1L, "recipient@example.com", 2));
        MockHttpSession session = new MockHttpSession(); session.setAttribute("userEmail", "donor@example.com"); session.setAttribute("userRole", "DONOR");
        mockMvc.perform(post("/tracking/" + request.getId() + "/update").session(session).param("status", "DISTRIBUTED"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/tracking?error=invalidTransition"));
        assertEquals(RequestStatus.REQUESTED, requests.findById(request.getId()).orElseThrow().getStatus());
    }
}
