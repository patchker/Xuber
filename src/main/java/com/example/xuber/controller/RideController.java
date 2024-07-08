package com.example.xuber.controller;

import com.example.xuber.model.Ride;
import com.example.xuber.model.RideStatus;
import com.example.xuber.model.User;
import com.example.xuber.repository.RideRepository;
import com.example.xuber.repository.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@RestController
@RequestMapping("/api/ride")
public class RideController {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Autowired
    public RideController(RideRepository rideRepository , UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<Ride> all(@RequestParam int userId) {
        return rideRepository.findAllByUser_Id(userId);
    }

    @GetMapping("/driver/all")
    public List<Ride> driverAll() {
        return rideRepository.findAllByRideStatus(RideStatus.SEARCHING);
    }

    @PostMapping("/start")
    public ResponseEntity startRide(@RequestBody User user) {

        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));


        List<Ride> activeRides = rideRepository.findAllByUser_IdAndRideStatusIn(
                existingUser.getId(),
                Arrays.asList(RideStatus.SEARCHING, RideStatus.WAITING, RideStatus.RIDING)
        );

        if (!activeRides.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("User already has an active ride");
        }
        // Stwórz nowy przejazd
        Ride newRide = new Ride();
        newRide.setUser(existingUser);
        newRide.setRideStatus(RideStatus.SEARCHING); // Ustaw status przejazdu na "STARTED"
        newRide.setAddress(user.getAddress());
        newRide.setCity(user.getCity());
        newRide.setState(user.getState());
        newRide.setZip(user.getZip());
        newRide.setCountry(user.getCountry());
        newRide.setCreatedAt(LocalDateTime.now()); // Ustaw aktualną datę i czas

        Ride savedRide = rideRepository.save(newRide);
        return ResponseEntity.ok(savedRide);
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptRide(@RequestBody AcceptRideRequest request) {
        User driver = userRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        if (!ride.getRideStatus().equals(RideStatus.SEARCHING)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ride is not in SEARCHING status");
        }

        // Sprawdź, czy kierowca nie ma już aktywnego przejazdu
        List<Ride> activeDriverRides = rideRepository.findAllByDriver_IdAndRideStatusIn(
                driver.getId(),
                Arrays.asList(RideStatus.WAITING, RideStatus.RIDING)
        );

        if (!activeDriverRides.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Driver already has an active ride");
        }

        // Aktualizuj przejazd
        ride.setDriver(driver);
        ride.setRideStatus(RideStatus.WAITING);
        ride.setUpdatedAt(LocalDateTime.now());

        Ride updatedRide = rideRepository.save(ride);
        return ResponseEntity.ok(updatedRide);
    }

    @PostMapping("/pickup")
    public ResponseEntity<?> pickupRide(@RequestBody RideStatusChangeRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        if (!ride.getRideStatus().equals(RideStatus.WAITING)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ride is not in WAITING status");
        }

        if (ride.getDriver().getId() != request.getDriverId()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not the driver of this ride");
        }

        ride.setRideStatus(RideStatus.RIDING);
        ride.setUpdatedAt(LocalDateTime.now());

        Ride updatedRide = rideRepository.save(ride);
        return ResponseEntity.ok(updatedRide);
    }



    @PostMapping("/complete")
    public ResponseEntity<?> completeRide(@RequestBody RideStatusChangeRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        if (!ride.getRideStatus().equals(RideStatus.RIDING)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ride is not in RIDING status");
        }

        if (ride.getDriver().getId() != request.getDriverId()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are not the driver of this ride");
        }

        ride.setRideStatus(RideStatus.COMPLETED);
        ride.setUpdatedAt(LocalDateTime.now());

        Ride updatedRide = rideRepository.save(ride);
        return ResponseEntity.ok(updatedRide);
    }
    /*@GetMapping("/driver/all")
    public List<Ride> driverAll(@RequestParam RideStatus rideStatus) {
        return rideRepository.findAllByRideStatus(rideStatus);
    }

     */
}
