package com.train.trainservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "station")
@Data
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationId;

    private String name;

    private String code; // PUNE, MUM, DEL
}