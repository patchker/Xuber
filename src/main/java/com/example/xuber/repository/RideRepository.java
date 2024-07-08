package com.example.xuber.repository;

import com.example.xuber.model.Ride;
import com.example.xuber.model.RideStatus;
import com.example.xuber.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Integer> {
    List<Ride> findAllByUser_Id(int userId);
    List<Ride> findAllByRideStatus(RideStatus rideStatus);
    List<Ride> findAllByUser_IdAndRideStatusIn(int user_id, Collection<RideStatus> rideStatus);
    List<Ride> findAllByDriver_IdAndRideStatusIn(int user_id, Collection<RideStatus> rideStatus);


}
