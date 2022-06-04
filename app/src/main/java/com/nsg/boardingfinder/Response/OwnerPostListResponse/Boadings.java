package com.nsg.boardingfinder.Response.OwnerPostListResponse;

import java.util.List;

public class Boadings {
    private String id;
    private String title;
    private String price;
    private String location;
    private String category;
    private String gender;
    private String image;
    private String description;
    private String status;
    private String createdAt;
    private String accommodaterId;
    private List<Facility> facilities;

    public Boadings() {
    }

    public Boadings(String id, String title, String price, String location, String category, String gender, String image, String description, String status, String createdAt, String accommodaterId, List<Facility> facilities) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.category = category;
        this.gender = gender;
        this.image = image;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.accommodaterId = accommodaterId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAccommodaterId() {
        return accommodaterId;
    }

    public void setAccommodaterId(String accommodaterId) {
        this.accommodaterId = accommodaterId;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    @Override
    public String toString() {
        return "Boadings{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", gender='" + gender + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", accommodaterId='" + accommodaterId + '\'' +
                ", facilities=" + facilities +
                '}';
    }
}
