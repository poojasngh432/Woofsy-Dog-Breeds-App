package com.example.woofsyapp.model;

import com.google.gson.annotations.SerializedName;

public class Original {

    @SerializedName("url")
    private String url;
    @SerializedName("webp")
    private String webp;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebp() {
        return webp;
    }

    public void setWebp(String webp) {
        this.webp = webp;
    }

}
