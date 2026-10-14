package com.foodshare.matching;

import com.foodshare.listing.FoodListing;
import com.foodshare.listing.FoodListingRepository;
import com.foodshare.listing.ListingStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchingServiceTest {
    @Test
    void returnsOnlyAvailableNonExpiredListingsWithPositiveQuantity() {
        FoodListingRepository repository = mock(FoodListingRepository.class);
        FoodListing valid = listing("Rice", 10, LocalDate.now().plusDays(1), ListingStatus.AVAILABLE);
        FoodListing expired = listing("Expired food", 10, LocalDate.now().minusDays(1), ListingStatus.AVAILABLE);
        FoodListing unavailable = listing("Unavailable", 10, LocalDate.now().plusDays(1), ListingStatus.UNAVAILABLE);
        FoodListing zeroQuantity = listing("Empty", 0, LocalDate.now().plusDays(1), ListingStatus.AVAILABLE);
        when(repository.findAll()).thenReturn(List.of(valid, expired, unavailable, zeroQuantity));
        MatchingService service = new MatchingService(repository);
        List<FoodListing> result = service.findEligibleListings();
        assertEquals(1, result.size());
        assertEquals("Rice", result.get(0).getFoodName());
    }

    private FoodListing listing(String name, int quantity, LocalDate expiry, ListingStatus status) {
        FoodListing item = new FoodListing(); item.setFoodName(name); item.setQuantity(quantity);
        item.setExpiryDate(expiry); item.setPickupLocation("Vellore"); item.setDonorEmail("donor@example.com"); item.setStatus(status); return item;
    }
}
