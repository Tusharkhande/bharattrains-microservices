package com.train.trainservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainSearchResponse {

    private Long trainId;
    private String trainName;

    private String source;
    private String destination;

    private int fromOrder;
    private int toOrder;

    private LocalDate journeyDate;

    // future fields
    private Integer availableSeats;

    private String arrivalTime;

    private String departureTime;

    private Long durationHours;
}