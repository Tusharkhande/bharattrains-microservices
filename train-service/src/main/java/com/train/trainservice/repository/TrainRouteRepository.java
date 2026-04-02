package com.train.trainservice.repository;

import com.train.trainservice.entity.TrainRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainRouteRepository extends JpaRepository<TrainRoute, Long> {

    List<TrainRoute> findByTrainIdOrderByStopOrderAsc(Long trainId);

    TrainRoute findByTrainIdAndStationId(Long trainId, Long stationId);

    @Query("""
    SELECT tr1.trainId
    FROM TrainRoute tr1
    JOIN TrainRoute tr2
      ON tr1.trainId = tr2.trainId
    WHERE tr1.stationId = :sourceId
      AND tr2.stationId = :destId
      AND tr1.stopOrder < tr2.stopOrder
""")
    List<Long> findTrainsBetweenStations(Long sourceId, Long destId);

}