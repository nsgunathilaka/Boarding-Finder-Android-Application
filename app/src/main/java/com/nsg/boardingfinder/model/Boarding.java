package com.nsg.boardingfinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Boarding {

    private int id;
    private String title;
    private String price;
    private String location;
    private String longitude;
    private String latitude;
    private String category;
    private String gender;
    private String image;
    private String accommodaterId;
    private String description;
    private Date postedAt;
    private String timeElapsed;

    public Boarding() {
    }

    public Boarding(int id, String title, String price, String location, String category, String gender, String image, String accommodaterId, String description, Date postedAt, String timeElapsed) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.category = category;
        this.gender = gender;
        this.image = image;
        this.accommodaterId = accommodaterId;
        this.description = description;
        this.postedAt = postedAt;
        this.timeElapsed = timeElapsed;
    }

    public Boarding(int id, String title, String price, String location, String longitude, String latitude, String category, String gender, String image, String accommodaterId, String description, Date postedAt, String timeElapsed) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.category = category;
        this.gender = gender;
        this.image = image;
        this.accommodaterId = accommodaterId;
        this.description = description;
        this.postedAt = postedAt;
        this.timeElapsed = timeElapsed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAccommodaterId() {
        return accommodaterId;
    }

    public void setAccommodaterId(String accommodaterId) {
        this.accommodaterId = accommodaterId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public String getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
