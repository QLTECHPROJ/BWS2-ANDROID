package com.qltech.bws.MembershipModule.Models;

public class SubscriptionModel {
    String title;

    public SubscriptionModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
