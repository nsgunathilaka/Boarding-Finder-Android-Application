package com.nsg.boardingfinder.Response.OwnerPostListResponse;

import java.util.List;

public class OwnerPostListResponse {
    private String id;
    private String fullName;
    private String address;
    private String phone;
    private String occupation;
    private String gender;
    private String loginId;
    private List<Boadings>  bordings;

    public OwnerPostListResponse() {
    }

    public OwnerPostListResponse(String id, String fullName, String address, String phone, String occupation, String gender, String loginId, List<Boadings> bordings) {
        this.id = id;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.occupation = occupation;
        this.gender = gender;
        this.loginId = loginId;
        this.bordings = bordings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public List<Boadings> getBordings() {
        return bordings;
    }

    public void setBordings(List<Boadings> bordings) {
        this.bordings = bordings;
    }

    @Override
    public String toString() {
        return "OwnerPostListResponse{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", occupation='" + occupation + '\'' +
                ", gender='" + gender + '\'' +
                ", loginId='" + loginId + '\'' +
                ", bordings=" + bordings +
                '}';
    }
}
