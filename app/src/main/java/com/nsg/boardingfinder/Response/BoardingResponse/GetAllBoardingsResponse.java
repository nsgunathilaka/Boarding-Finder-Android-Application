package com.nsg.boardingfinder.Response.BoardingResponse;

import com.nsg.boardingfinder.model.Boarding;

import java.util.List;

public class GetAllBoardingsResponse {
    private List<Boarding> data;

    public GetAllBoardingsResponse() {
    }

    public GetAllBoardingsResponse(List<Boarding> data) {
        this.data = data;
    }

    public List<Boarding> getData() {
        return data;
    }

    public void setData(List<Boarding> data) {
        this.data = data;
    }
}
