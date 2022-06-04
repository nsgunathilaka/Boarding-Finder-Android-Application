package com.nsg.boardingfinder.Response.BoardingByIdRes;

public class Owner {
    private String ownerUserProfileId;
    private String fullName;
    private String address;
    private String phone;
    private String occupation;
    private String gender;

    public Owner() {
    }

    public Owner(String ownerUserProfileId, String fullName, String address, String phone, String occupation, String gender) {
        this.ownerUserProfileId = ownerUserProfileId;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.occupation = occupation;
        this.gender = gender;
    }

    public String getOwnerUserProfileId() {
        return ownerUserProfileId;
    }

    public void setOwnerUserProfileId(String ownerUserProfileId) {
        this.ownerUserProfileId = ownerUserProfileId;
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

    @Override
    public String toString() {
        return "Owner{" +
                "ownerUserProfileId='" + ownerUserProfileId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", occupation='" + occupation + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
