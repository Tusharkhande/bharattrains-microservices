package com.train.bookingservice.dto;

import lombok.Data;

@Data
public class NotificationRequest {

    private Long bookingId;
    private String message;
    private String type; // EMAIL / SMS
    private String email; // ✅ NEW
}
