package com.cognizant.poc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsArticle {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("imageHref")
    @Expose
    private String urlToImage;


    public String getTitle() {
        if(title == null) {
            return "";
        } else {
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if(description == null) {
            return "";
        } else {
            return description;
        }

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToImage() {
        if(urlToImage == null) {
            return "";
        } else {
            return urlToImage;
        }
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }
}
