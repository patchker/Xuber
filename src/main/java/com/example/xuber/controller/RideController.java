package com.example.xuber.controller;

import com.example.xuber.exceptions.*;
import com.example.xuber.model.Ride;
import com.example.xuber.model.RideStatus;
import com.example.xuber.model.User;
import com.example.xuber.repository.RideRepository;
import com.example.xuber.repository.UserRepository;
import com.example.xuber.service.RideService;
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
    private final RideService rideService;

    @Autowired
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Ride>> all(@RequestParam int userId) {
        return ResponseEntity.ok(rideService.getAllRidesForUser(userId));
    }

    @GetMapping("/driver/all")
    public ResponseEntity<List<Ride>> driverAll() {
        return ResponseEntity.ok(rideService.getAllSearchingRides());
    }

    @PostMapping("/start")
    public ResponseEntity<?> startRide(@RequestBody User user) {
        try {
            Ride savedRide = rideService.startRide(user);
            return ResponseEntity.ok(savedRide);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ActiveRideExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptRide(@RequestBody AcceptRideRequest request) {
        try {
            Ride updatedRide = rideService.acceptRide(request);
            return ResponseEntity.ok(updatedRide);
        } catch (UserNotFoundException | RideNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidRideStatusException | ActiveRideExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/pickup")
    public ResponseEntity<?> pickupRide(@RequestBody RideStatusChangeRequest request) {
        try {
            Ride updatedRide = rideService.pickupRide(request);
            return ResponseEntity.ok(updatedRide);
        } catch (RideNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidRideStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UnauthorizedDriverException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeRide(@RequestBody RideStatusChangeRequest request) {
        try {
            Ride updatedRide = rideService.completeRide(request);
            return ResponseEntity.ok(updatedRide);
        } catch (RideNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidRideStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UnauthorizedDriverException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
