package com.foodshare.tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<CollectionRecord, Long> {
    Optional<CollectionRecord> findByDonationRequestId(Long donationRequestId);
    List<CollectionRecord> findAllByOrderByUpdatedAtDesc();
    long countByStatus(CollectionStatus status);
}
