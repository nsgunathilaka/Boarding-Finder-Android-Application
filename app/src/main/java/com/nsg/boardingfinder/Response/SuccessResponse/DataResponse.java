package com.nsg.boardingfinder.Response.SuccessResponse;

import com.google.gson.annotations.SerializedName;

public class DataResponse {
    @SerializedName("data")
    public String data;

    public DataResponse() {
    }
    public DataResponse(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataResponse{" +
                "data='" + data + '\'' +
                '}';
    }
}
