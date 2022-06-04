package com.nsg.boardingfinder.Response.BoardingByIdRes;

public class BoardingByIdResponse {
    private  Boarding boarding;
    private  Owner owner;

    public BoardingByIdResponse() {
    }

    public BoardingByIdResponse(Boarding boarding, Owner owner) {
        this.boarding = boarding;
        this.owner = owner;
    }

    public Boarding getBoarding() {
        return boarding;
    }

    public void setBoarding(Boarding boarding) {
        this.boarding = boarding;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
