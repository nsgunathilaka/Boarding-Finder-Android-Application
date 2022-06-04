package com.nsg.boardingfinder.Response.OwnerPostListResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Facility {

    private String facility;

    public Facility() {
    }

    public Facility(String facility) {
        this.facility = facility;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "facility='" + facility + '\'' +
                '}';
    }
}
