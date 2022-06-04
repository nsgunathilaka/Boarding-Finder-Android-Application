package com.nsg.boardingfinder.model;

public class PostData {

    String title;
    String price;
    String status;
    String boardingId;
    String ownerId;
    String imageUrl;

    public PostData(){
    }

    public PostData(String title, String price, String status, String boardingId, String ownerId, String imageUrl) {
        this.title = title;
        this.price = price;
        this.status = status;
        this.boardingId = boardingId;
        this.ownerId = ownerId;
        this.imageUrl = imageUrl;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBoardingId() {
        return boardingId;
    }

    public void setBoardingId(String boardingId) {
        this.boardingId = boardingId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
