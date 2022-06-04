package com.nsg.boardingfinder.Utils;

import java.util.List;

public class GetBoardingsReqModel {

    List<Integer> facilities;
    List<String> genders;

    public GetBoardingsReqModel() {
    }

    public GetBoardingsReqModel(List<Integer> facilities, List<String> genders) {
        this.facilities = facilities;
        this.genders = genders;
    }

    public List<Integer> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Integer> facilities) {
        this.facilities = facilities;
    }

    public List<String> getGenders() {
        return genders;
    }

    public void setGenders(List<String> genders) {
        this.genders = genders;
    }
}
