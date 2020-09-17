package com.dogbreeds.woofsyapp.model;

import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("url")
    private String url;
    @SerializedName("slug")
    private String slug;
    @SerializedName("bitly_gif_url")
    private String bitlyGifUrl;
    @SerializedName("images")
    private Images images;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBitlyGifUrl() {
        return bitlyGifUrl;
    }

    public void setBitlyGifUrl(String bitlyGifUrl) {
        this.bitlyGifUrl = bitlyGifUrl;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

}
