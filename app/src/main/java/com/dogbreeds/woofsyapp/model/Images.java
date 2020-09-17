package com.dogbreeds.woofsyapp.model;

import com.google.gson.annotations.SerializedName;

public class Images {

    @SerializedName("original")
    private Original original;
    public Original getOriginal() {
        return original;
    }

    public void setOriginal(Original original) {
        this.original = original;
    }

}
