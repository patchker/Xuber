package com.example.xuber.service;

import com.example.xuber.controller.AcceptRideRequest;
import com.example.xuber.controller.RideStatusChangeRequest;
import com.example.xuber.exceptions.*;
import com.example.xuber.model.Ride;
import com.example.xuber.model.RideStatus;
import com.example.xuber.model.User;
import com.example.xuber.repository.RideRepository;
import com.example.xuber.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Autowired
    public RideService(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    public List<Ride> getAllRidesForUser(int userId) {
        return rideRepository.findAllByUser_Id(userId);
    }

    public List<Ride> getAllSearchingRides() {
        return rideRepository.findAllByRideStatus(RideStatus.SEARCHING);
    }

    public Ride startRide(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("Driver not found"));

        List<Ride> activeRides = rideRepository.findAllByUser_IdAndRideStatusIn(
                existingUser.getId(),
                Arrays.asList(RideStatus.SEARCHING, RideStatus.WAITING, RideStatus.RIDING)
        );

        if (!activeRides.isEmpty()) {
            throw new ActiveRideExistsException("User already has an active ride");
        }

        Ride newRide = createNewRide(existingUser, user);
        return rideRepository.save(newRide);
    }

    public Ride acceptRide(AcceptRideRequest request) {
        User driver = userRepository.findById(request.getDriverId())
                .orElseThrow(() -> new UserNotFoundException("Driver not found"));

        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (!ride.getRideStatus().equals(RideStatus.SEARCHING)) {
            throw new InvalidRideStatusException("Ride is not in SEARCHING status");
        }

        List<Ride> activeDriverRides = rideRepository.findAllByDriver_IdAndRideStatusIn(
                driver.getId(),
                Arrays.asList(RideStatus.WAITING, RideStatus.RIDING)
        );

        if (!activeDriverRides.isEmpty()) {
            throw new ActiveRideExistsException("Driver already has an active ride");
        }

        ride.setDriver(driver);
        ride.setRideStatus(RideStatus.WAITING);
        ride.setUpdatedAt(LocalDateTime.now());

        return rideRepository.save(ride);
    }

    public Ride pickupRide(RideStatusChangeRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (!ride.getRideStatus().equals(RideStatus.WAITING)) {
            throw new InvalidRideStatusException("Ride is not in WAITING status");
        }

        if (ride.getDriver().getId() != request.getDriverId()) {
            throw new UnauthorizedDriverException("You are not the driver of this ride");
        }

        ride.setRideStatus(RideStatus.RIDING);
        ride.setUpdatedAt(LocalDateTime.now());

        return rideRepository.save(ride);
    }

    public Ride completeRide(RideStatusChangeRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (!ride.getRideStatus().equals(RideStatus.RIDING)) {
            throw new InvalidRideStatusException("Ride is not in RIDING status");
        }

        if (ride.getDriver().getId() != request.getDriverId()) {
            throw new UnauthorizedDriverException("You are not the driver of this ride");
        }

        ride.setRideStatus(RideStatus.COMPLETED);
        ride.setUpdatedAt(LocalDateTime.now());

        return rideRepository.save(ride);
    }

    private Ride createNewRide(User existingUser, User userDetails) {
        Ride newRide = new Ride();
        newRide.setUser(existingUser);
        newRide.setRideStatus(RideStatus.SEARCHING);
        newRide.setAddress(userDetails.getAddress());
        newRide.setCity(userDetails.getCity());
        newRide.setState(userDetails.getState());
        newRide.setZip(userDetails.getZip());
        newRide.setCountry(userDetails.getCountry());
        newRide.setCreatedAt(LocalDateTime.now());
        return newRide;
    }
}