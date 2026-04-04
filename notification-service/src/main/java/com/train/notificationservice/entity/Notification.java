package com.train.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;

    private String message;

    private String type; // EMAIL / SMS

    private String status; // SENT / FAILED

    private LocalDateTime createdAt;
}
