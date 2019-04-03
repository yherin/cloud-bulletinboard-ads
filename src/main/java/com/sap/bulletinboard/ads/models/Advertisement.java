package com.sap.bulletinboard.ads.models;

import org.springframework.stereotype.Component;

@Component
public class Advertisement {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
