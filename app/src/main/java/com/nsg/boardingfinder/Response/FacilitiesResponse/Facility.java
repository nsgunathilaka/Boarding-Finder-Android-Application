package com.nsg.boardingfinder.Response.FacilitiesResponse;

public class Facility {
   private int id;
   private String facility;

    public Facility() {
    }

    public Facility(int id, String facility) {
        this.id = id;
        this.facility = facility;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
                "id=" + id +
                ", facility='" + facility + '\'' +
                '}';
    }
}
