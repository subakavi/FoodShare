package com.foodshare.request;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation_requests")
public class DonationRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long foodListingId;
    @Column(nullable = false) private String requesterEmail;
    @Column(nullable = false) private Integer requestedQuantity;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RequestStatus status = RequestStatus.REQUESTED;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public DonationRequest() {}
    public DonationRequest(Long foodListingId, String requesterEmail, Integer requestedQuantity) {
        this.foodListingId = foodListingId; this.requesterEmail = requesterEmail;
        this.requestedQuantity = requestedQuantity; this.status = RequestStatus.REQUESTED;
        this.createdAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Long getFoodListingId() { return foodListingId; }
    public void setFoodListingId(Long foodListingId) { this.foodListingId = foodListingId; }
    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
    public Integer getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
