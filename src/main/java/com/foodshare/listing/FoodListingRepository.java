package com.foodshare.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodListingRepository extends JpaRepository<FoodListing, Long> {
    List<FoodListing> findByDonorEmailOrderByIdDesc(String donorEmail);
}
