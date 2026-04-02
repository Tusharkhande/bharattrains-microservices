package com.train.trainservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityRequest {

    private Long trainId;
    private LocalDate journeyDate;
    private int from;
    private int to;
}