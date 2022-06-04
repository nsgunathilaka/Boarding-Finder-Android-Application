package com.nsg.boardingfinder.model;

import java.util.ArrayList;
import java.util.Date;

public class Accommodater {

    public int id;
    public String fullName;
    public String address;
    public String phone;
    public String occupation;
    public Date createdAt;
    public Date updatedAt;
    public int loginId;
    public ArrayList<Boarding> bordings;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public ArrayList<Boarding> getBordings() {
        return bordings;
    }

    public void setBordings(ArrayList<Boarding> bordings) {
        this.bordings = bordings;
    }
}
