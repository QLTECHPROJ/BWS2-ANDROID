package com.qltech.bws.DashboardModule.Models;

public class SuggestionAudiosModel {
    String title;
    int id;

    public SuggestionAudiosModel(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
