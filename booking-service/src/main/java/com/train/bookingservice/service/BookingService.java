package com.train.bookingservice.service;

import com.train.bookingservice.client.NotificationClient;
import com.train.bookingservice.client.SeatInventoryClient;
import com.train.bookingservice.client.UserClient;
import com.train.bookingservice.dto.BookingDetailsResponse;
import com.train.bookingservice.dto.BookingRequestDTO;
import com.train.bookingservice.dto.PassengerDTO;
import com.train.bookingservice.dto.PassengerDetails;
import com.train.bookingservice.entity.Booking;
import com.train.bookingservice.entity.Passenger;
import com.train.bookingservice.repository.BookingRepository;
import com.train.bookingservice.repository.PassengerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final SeatInventoryClient seatClient;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    public BookingService(
            BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            SeatInventoryClient seatClient,
            NotificationClient notificationClient,
            UserClient userClient
    ) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.seatClient = seatClient;
        this.notificationClient = notificationClient;
        this.userClient = userClient;
    }

    public Booking createBooking(BookingRequestDTO request) {

        // Fetch email from User Service
        String email = userClient.getUserEmail(request.getUserId());

        // Create booking
        Booking booking = new Booking();
        booking.setTrainId(request.getTrainId());
        booking.setJourneyDate(request.getJourneyDate());
        booking.setFromStation(request.getFromStation());
        booking.setToStation(request.getToStation());
        booking.setPnr(generatePNR());
        booking.setStatus("CREATED");

        bookingRepository.save(booking);

        boolean allConfirmed = true;

        for (PassengerDTO p : request.getPassengers()) {

            Passenger passenger = new Passenger();
            passenger.setBookingId(booking.getBookingId());
            passenger.setName(p.getName());
            passenger.setAge(p.getAge());
            passenger.setGender(p.getGender());

            passengerRepository.save(passenger);

            String status = seatClient.bookSeat(
                    request.getTrainId(),
                    request.getJourneyDate(),
                    request.getFromStation(),
                    request.getToStation(),
                    booking.getBookingId(),
                    passenger.getPassengerId()
            );

            passenger.setStatus(status);

            if (!"CONFIRMED".equals(status)) {
                allConfirmed = false;
            }

            passengerRepository.save(passenger);
        }

        // Update booking status
        booking.setStatus(allConfirmed ? "CONFIRMED" : "PARTIAL");
        bookingRepository.save(booking);

        // 🔔 SEND NOTIFICATION
        notificationClient.sendNotification(
                booking.getBookingId(),
                "Booking created successfully. PNR: " + booking.getPnr(),
                email,
                "BOOKING_CREATED"
        );

        return booking;
    }

    private String generatePNR() {
        return "PNR" + System.currentTimeMillis();
    }

    public BookingDetailsResponse getBookingByPNR(String pnr) {

        Booking booking = bookingRepository.findByPnr(pnr);

        if (booking == null) {
            throw new RuntimeException("PNR not found");
        }

        List<Passenger> passengers =
                passengerRepository.findByBookingId(booking.getBookingId());

        List<PassengerDetails> passengerDetailsList =
                passengers.stream()
                        .map(p -> new PassengerDetails(
                                p.getName(),
                                p.getAge(),
                                p.getGender(),
                                p.getStatus()
                        ))
                        .toList();

        return new BookingDetailsResponse(
                booking.getPnr(),
                booking.getTrainId(),
                booking.getJourneyDate(),
                booking.getFromStation(),
                booking.getToStation(),
                booking.getStatus(),
                passengerDetailsList
        );
    }

    @Transactional
    public String cancelBooking(String pnr) {

        Booking booking = bookingRepository.findByPnr(pnr);

        if (booking == null) {
            throw new RuntimeException("PNR not found");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            return "Booking already cancelled";
        }

        // Call seat service
        seatClient.cancelBooking(
                booking.getBookingId(),
                booking.getTrainId(),
                booking.getJourneyDate()
        );

        List<Passenger> passengers =
                passengerRepository.findByBookingId(booking.getBookingId());

        for (Passenger p : passengers) {
            p.setStatus("CANCELLED");
            passengerRepository.save(p);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // Fetch email from User Service
        String email = userClient.getUserEmailByBookingId(booking.getBookingId());

        // 🔔 SEND NOTIFICATION
        notificationClient.sendNotification(
                booking.getBookingId(),
                "Your booking with PNR " + booking.getPnr() + " has been cancelled.",
                email,
                "BOOKING_CANCELLED"
        );

        return "Booking cancelled successfully";
    }

    @Transactional
    public String cancelPassenger(Long passengerId) {

        Passenger passenger =
                passengerRepository.findById(passengerId)
                        .orElseThrow(() -> new RuntimeException("Passenger not found"));

        if ("CANCELLED".equals(passenger.getStatus())) {
            return "Passenger already cancelled";
        }

        Booking booking =
                bookingRepository.findById(passenger.getBookingId())
                        .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Call seat service
        seatClient.cancelPassenger(
                passengerId,
                booking.getTrainId(),
                booking.getJourneyDate()
        );

        passenger.setStatus("CANCELLED");
        passengerRepository.save(passenger);

        updateBookingStatus(booking.getBookingId());

        // Fetch email
        String email = userClient.getUserEmailByBookingId(booking.getBookingId());

        // 🔔 SEND NOTIFICATION
        notificationClient.sendNotification(
                booking.getBookingId(),
                "Passenger " + passenger.getName() + " has been cancelled from booking PNR: " + booking.getPnr(),
                email,
                "PASSENGER_CANCELLED"
        );

        return "Passenger cancelled successfully";
    }

    private void updateBookingStatus(Long bookingId) {

        List<Passenger> passengers =
                passengerRepository.findByBookingId(bookingId);

        boolean allCancelled =
                passengers.stream().allMatch(p -> p.getStatus().equals("CANCELLED"));

        boolean allConfirmed =
                passengers.stream().allMatch(p -> p.getStatus().equals("CONFIRMED"));

        Booking booking = bookingRepository.findById(bookingId).get();

        if (allCancelled) {
            booking.setStatus("CANCELLED");
        } else if (allConfirmed) {
            booking.setStatus("CONFIRMED");
        } else {
            booking.setStatus("PARTIAL");
        }

        bookingRepository.save(booking);
    }
}