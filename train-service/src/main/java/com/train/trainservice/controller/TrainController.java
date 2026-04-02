package com.train.trainservice.controller;

import com.train.trainservice.dto.TrainSearchResponse;
import com.train.trainservice.entity.Station;
import com.train.trainservice.entity.Train;
import com.train.trainservice.entity.TrainRoute;
import com.train.trainservice.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class TrainController {

    @Autowired
    TrainService trainService;

    @PostMapping("/train")
    public Train createTrain(@RequestParam String name) {
        return trainService.createTrain(name);
    }

    @PostMapping("/station")
    public Station createStation(
            @RequestParam String name,
            @RequestParam String code) {

        return trainService.createStation(name, code);
    }

    @PostMapping("/route")
    public String addRoute(
            @RequestParam Long trainId,
            @RequestParam Long stationId,
            @RequestParam int order,
            @RequestParam(required = false) String arrival,
            @RequestParam(required = false) String departure) {

        trainService.addRoute(trainId, stationId, order, arrival, departure);

        return "Route added";
    }

    @GetMapping("/route/{trainId}")
    public List<TrainRoute> getRoute(@PathVariable Long trainId) {
        return trainService.getRoute(trainId);
    }

    @GetMapping("/order")
    public int getOrder(
            @RequestParam Long trainId,
            @RequestParam String stationCode) {

        return trainService.getStationOrder(trainId, stationCode);
    }

    @GetMapping("/search")
    public List<TrainSearchResponse> searchTrains(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam String date) {

        return trainService.searchTrains(
                source,
                destination,
                LocalDate.parse(date)
        );
    }

}
