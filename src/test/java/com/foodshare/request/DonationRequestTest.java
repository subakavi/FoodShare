package com.foodshare.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DonationRequestTest {
    @Test
    void newRequestStartsAsRequested() {
        DonationRequest request = new DonationRequest(12L, "recipient@example.com", 3);
        assertEquals(12L, request.getFoodListingId());
        assertEquals("recipient@example.com", request.getRequesterEmail());
        assertEquals(3, request.getRequestedQuantity());
        assertEquals(RequestStatus.REQUESTED, request.getStatus());
        assertNotNull(request.getCreatedAt());
    }
}
