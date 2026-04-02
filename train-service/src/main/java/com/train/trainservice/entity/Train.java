package com.train.trainservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.Set;

@Entity
@Table(name = "train")
@Data
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainId;

    private String trainName;

    private Set<DayOfWeek> runningDays;
}