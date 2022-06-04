package com.nsg.boardingfinder.Response.BoardingByIdRes;

import java.util.List;

public class Boarding {
    private String id;
    private String title;
    private String price;
    private String location;
    private String longitude;
    private String latitude;
    private String category;
    private String gender;
    private String description;
    private String status;
    private String image;
    private String postedAt;
    private String timeElapsed;
    private List<String> facilities;

    public Boarding() {
    }

    public Boarding(String id, String title, String price, String location, String longitude, String latitude, String category, String gender, String description, String status, String image, String postedAt, String timeElapsed, List<String> facilities) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.category = category;
        this.gender = gender;
        this.description = description;
        this.status = status;
        this.image = image;
        this.postedAt = postedAt;
        this.timeElapsed = timeElapsed;
        this.facilities = facilities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public String getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }
}
