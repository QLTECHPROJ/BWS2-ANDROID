package com.qltech.bws.ReminderModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RemiderDetailsModel {
    @SerializedName("ResponseData")
    @Expose
    private List<ResponseData> responseData = null;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("ResponseStatus")
    @Expose
    private String responseStatus;

    public List<ResponseData> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<ResponseData> responseData) {
        this.responseData = responseData;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public class ResponseData {
        @SerializedName("PlaylistId")
        @Expose
        private String playlistId;
        @SerializedName("PlaylistName")
        @Expose
        private String playlistName;
        @SerializedName("ReminderDay")
        @Expose
        private String reminderDay;
        @SerializedName("ReminderTime")
        @Expose
        private String reminderTime;
        @SerializedName("IsLock")
        @Expose
        private String IsLock;
        @SerializedName("IsActive")
        @Expose
        private String IsActive;
        @SerializedName("RDay")
        @Expose
        private String RDay;
        @SerializedName("IsCheck")
        @Expose
        private String isCheck;
        @SerializedName("ReminderId")
        @Expose
        private String reminderId;

        public String getPlaylistId() {
            return playlistId;
        }

        public void setPlaylistId(String playlistId) {
            this.playlistId = playlistId;
        }

        public String getPlaylistName() {
            return playlistName;
        }

        public void setPlaylistName(String playlistName) {
            this.playlistName = playlistName;
        }

        public String getReminderDay() {
            return reminderDay;
        }

        public void setReminderDay(String reminderDay) {
            this.reminderDay = reminderDay;
        }

        public String getReminderTime() {
            return reminderTime;
        }

        public void setReminderTime(String reminderTime) {
            this.reminderTime = reminderTime;
        }

        public String getIsCheck() {
            return isCheck;
        }

        public void setIsCheck(String isCheck) {
            this.isCheck = isCheck;
        }

        public String getRDay() {
            return RDay;
        }

        public void setRDay(String RDay) {
            this.RDay = RDay;
        }

        public String getIsLock() {
            return IsLock;
        }

        public void setIsLock(String isLock) {
            IsLock = isLock;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }

        public String getReminderId() {
            return reminderId;
        }

        public void setReminderId(String reminderId) {
            this.reminderId = reminderId;
        }
    }
}
