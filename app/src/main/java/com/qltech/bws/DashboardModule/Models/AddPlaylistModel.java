package com.qltech.bws.DashboardModule.Models;

public class AddPlaylistModel {
    String title;

    public AddPlaylistModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
