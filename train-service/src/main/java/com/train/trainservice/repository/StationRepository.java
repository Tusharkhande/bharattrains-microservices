package com.train.trainservice.repository;

import com.train.trainservice.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {

    Station findByCode(String code);
}