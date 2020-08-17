package com.qltech.bws.DashboardModule.Models;

public class SessionListModel {
    String title,suTitle;

    public SessionListModel(String title, String suTitle) {
        this.title = title;
        this.suTitle = suTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuTitle() {
        return suTitle;
    }

    public void setSuTitle(String suTitle) {
        this.suTitle = suTitle;
    }
}
