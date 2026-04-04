package com.train.bookingservice.client;

import com.train.bookingservice.dto.NotificationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(Long bookingId, String message, String email, String type) {

        NotificationRequest request = new NotificationRequest();
        request.setBookingId(bookingId);
        request.setMessage(message);
        request.setEmail(email);
        request.setType(type);

        restTemplate.postForObject(
                "http://localhost:8085/notifications/send",
                request,
                String.class
        );
    }
}