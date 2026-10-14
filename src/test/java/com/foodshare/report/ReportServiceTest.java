package com.foodshare.report;

import com.foodshare.listing.FoodListingRepository;
import com.foodshare.request.DonationRequestRepository;
import com.foodshare.request.RequestStatus;
import com.foodshare.tracking.CollectionRepository;
import com.foodshare.tracking.CollectionStatus;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportServiceTest {
    @Test void summaryContainsCurrentRepositoryCounts() {
        FoodListingRepository listings = mock(FoodListingRepository.class);
        DonationRequestRepository requests = mock(DonationRequestRepository.class);
        CollectionRepository records = mock(CollectionRepository.class);
        when(listings.count()).thenReturn(8L);
        when(requests.count()).thenReturn(5L);
        when(requests.countByStatus(RequestStatus.REQUESTED)).thenReturn(2L);
        when(requests.countByStatus(RequestStatus.APPROVED)).thenReturn(1L);
        when(records.countByStatus(CollectionStatus.DISTRIBUTED)).thenReturn(3L);
        Map<String, Long> result = new ReportService(listings, requests, records).summary();
        assertEquals(8L, result.get("totalListings"));
        assertEquals(5L, result.get("totalRequests"));
        assertEquals(2L, result.get("pendingRequests"));
        assertEquals(1L, result.get("approvedRequests"));
        assertEquals(3L, result.get("completedDistributions"));
    }
}
