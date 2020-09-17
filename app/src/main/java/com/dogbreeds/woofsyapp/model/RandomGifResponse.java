package com.dogbreeds.woofsyapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RandomGifResponse {
    @SerializedName("data")
    private ArrayList<Datum> data;

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }
}