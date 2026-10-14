package com.foodshare.report;

import com.foodshare.listing.FoodListingRepository;
import com.foodshare.request.DonationRequestRepository;
import com.foodshare.request.RequestStatus;
import com.foodshare.tracking.CollectionRepository;
import com.foodshare.tracking.CollectionStatus;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ReportService {
    private final FoodListingRepository listings; private final DonationRequestRepository requests; private final CollectionRepository records;
    public ReportService(FoodListingRepository listings, DonationRequestRepository requests, CollectionRepository records) { this.listings=listings; this.requests=requests; this.records=records; }
    public Map<String, Long> summary() {
        Map<String, Long> result=new LinkedHashMap<>();
        result.put("totalListings", listings.count()); result.put("totalRequests", requests.count());
        result.put("pendingRequests", requests.countByStatus(RequestStatus.REQUESTED));
        result.put("approvedRequests", requests.countByStatus(RequestStatus.APPROVED));
        result.put("completedDistributions", records.countByStatus(CollectionStatus.DISTRIBUTED));
        return result;
    }
}
