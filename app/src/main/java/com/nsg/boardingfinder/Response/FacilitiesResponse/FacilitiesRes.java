package com.nsg.boardingfinder.Response.FacilitiesResponse;

import java.util.List;

public class FacilitiesRes {
    List<Facility> data;

    public FacilitiesRes() {
    }

    public FacilitiesRes(List<Facility> data) {
        this.data = data;
    }

    public List<Facility> getData() {
        return data;
    }

    public void setData(List<Facility> data) {
        this.data = data;
    }
}
