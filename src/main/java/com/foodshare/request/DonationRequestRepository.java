package com.foodshare.request;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {
    List<DonationRequest> findByRequesterEmailOrderByCreatedAtDesc(String requesterEmail);
    List<DonationRequest> findAllByOrderByCreatedAtDesc();
    boolean existsByFoodListingIdAndRequesterEmailAndStatusIn(Long foodListingId, String requesterEmail, List<RequestStatus> statuses);
    long countByStatus(RequestStatus status);
}
