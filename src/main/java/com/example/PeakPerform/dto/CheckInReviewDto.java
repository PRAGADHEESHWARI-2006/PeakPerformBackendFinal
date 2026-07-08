package com.example.PeakPerform.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInReviewDto {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;

    // ---------------- GETTER ----------------

    public String getRejectionReason() {
        return rejectionReason;
    }

    // ---------------- SETTER ----------------

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
