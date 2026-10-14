package com.foodshare.tracking;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "collection_records")
public class CollectionRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private Long donationRequestId;
    @Column(nullable = false) private String updatedBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CollectionStatus status;
    @Column(nullable = false) private LocalDateTime updatedAt;
    public CollectionRecord() {}
    public CollectionRecord(Long requestId, String updatedBy, CollectionStatus status) { this.donationRequestId=requestId; this.updatedBy=updatedBy; this.status=status; this.updatedAt=LocalDateTime.now(); }
    public Long getId(){return id;} public Long getDonationRequestId(){return donationRequestId;} public void setDonationRequestId(Long v){donationRequestId=v;}
    public String getUpdatedBy(){return updatedBy;} public void setUpdatedBy(String v){updatedBy=v;} public CollectionStatus getStatus(){return status;} public void setStatus(CollectionStatus v){status=v;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}
