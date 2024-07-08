package com.example.xuber.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
public class User {


    @Id
    @SequenceGenerator(name = "user_sequence",sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private int id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String gender;
    private int birthday;
    private int rideNum;
    private int status;


    public User(int status, int rideNum, int birthday, String gender, String zip, String country, String state, String city, String address, String phone, String email, String password, String username) {
        this.status = status;
        this.rideNum = rideNum;
        this.birthday = birthday;
        this.gender = gender;
        this.zip = zip;
        this.country = country;
        this.state = state;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public int getRideNum() {
        return rideNum;
    }

    public void setRideNum(int rideNum) {
        this.rideNum = rideNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
