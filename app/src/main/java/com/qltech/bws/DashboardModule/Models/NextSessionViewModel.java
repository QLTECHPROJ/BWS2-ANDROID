package com.qltech.bws.DashboardModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NextSessionViewModel {
    @SerializedName("ResponseData")
    @Expose
    private ResponseData responseData;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("ResponseStatus")
    @Expose
    private String responseStatus;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
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
        @SerializedName("Id")
        @Expose
        private String id;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Date")
        @Expose
        private String date;
        @SerializedName("Duration")
        @Expose
        private String duration;
        @SerializedName("Response")
        @Expose
        private String Response;
        @SerializedName("Time")
        @Expose
        private String time;
        @SerializedName("Task")
        @Expose
        private Task task;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getResponse() {
            return Response;
        }

        public void setResponse(String response) {
            Response = response;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

    }

    public class Task {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("AudioTask")
        @Expose
        private String audioTask;
        @SerializedName("BookletTask")
        @Expose
        private String bookletTask;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAudioTask() {
            return audioTask;
        }

        public void setAudioTask(String audioTask) {
            this.audioTask = audioTask;
        }

        public String getBookletTask() {
            return bookletTask;
        }

        public void setBookletTask(String bookletTask) {
            this.bookletTask = bookletTask;
        }
    }
}
