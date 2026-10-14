package com.foodshare.matching;

import com.foodshare.listing.FoodListing;
import com.foodshare.listing.FoodListingRepository;
import com.foodshare.listing.ListingStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MatchingService {
    private final FoodListingRepository listings;
    public MatchingService(FoodListingRepository listings) { this.listings = listings; }

    public List<FoodListing> findEligibleListings() {
        return listings.findAll().stream()
                .filter(item -> item.getStatus() == ListingStatus.AVAILABLE)
                .filter(item -> item.getExpiryDate() != null && !item.getExpiryDate().isBefore(LocalDate.now()))
                .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
                .toList();
    }
}
