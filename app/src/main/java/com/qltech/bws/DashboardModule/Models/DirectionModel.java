package com.qltech.bws.DashboardModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionModel {
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

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Like")
        @Expose
        private String like;
        @SerializedName("Download")
        @Expose
        private String download;
        @SerializedName("AudioFile")
        @Expose
        private String audioFile;
        @SerializedName("ImageFile")
        @Expose
        private String imageFile;
        @SerializedName("AudioDuration")
        @Expose
        private String audioDuration;
        @SerializedName("AudioDescription")
        @Expose
        private String audioDescription;
        @SerializedName("AudioDirection")
        @Expose
        private String audioDirection;
        @SerializedName("Audiomastercat")
        @Expose
        private String audiomastercat;
        @SerializedName("AudioSubCategory")
        @Expose
        private String audioSubCategory;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getAudioFile() {
            return audioFile;
        }

        public void setAudioFile(String audioFile) {
            this.audioFile = audioFile;
        }

        public String getImageFile() {
            return imageFile;
        }

        public void setImageFile(String imageFile) {
            this.imageFile = imageFile;
        }

        public String getiD() {
            return iD;
        }

        public void setiD(String iD) {
            this.iD = iD;
        }

        public String getAudioDirection() {
            return audioDirection;
        }

        public void setAudioDirection(String audioDirection) {
            this.audioDirection = audioDirection;
        }

        public String getAudioDuration() {
            return audioDuration;
        }

        public void setAudioDuration(String audioDuration) {
            this.audioDuration = audioDuration;
        }

        public String getAudioDescription() {
            return audioDescription;
        }

        public void setAudioDescription(String audioDescription) {
            this.audioDescription = audioDescription;
        }

        public String getAudiomastercat() {
            return audiomastercat;
        }

        public void setAudiomastercat(String audiomastercat) {
            this.audiomastercat = audiomastercat;
        }

        public String getAudioSubCategory() {
            return audioSubCategory;
        }

        public void setAudioSubCategory(String audioSubCategory) {
            this.audioSubCategory = audioSubCategory;
        }

    }
}
