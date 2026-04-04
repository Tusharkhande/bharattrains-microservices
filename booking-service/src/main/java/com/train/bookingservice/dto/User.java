package com.train.bookingservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class User {

    private Long userId;

    private String name;

    private String email;

    private String phone;
}