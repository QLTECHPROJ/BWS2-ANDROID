package com.brainwellnessspa.DashboardOldModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistingModel {
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
        private String ID;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Created")
        @Expose
        private String created;
        @SerializedName("TotalAudio")
        @Expose
        private String totalAudio;
        @SerializedName("TotalDuration")
        @Expose
        private String totalDuration;
        @SerializedName("Totalhour")
        @Expose
        private String totalhour;
        @SerializedName("Totalminute")
        @Expose
        private String totalminute;
        @SerializedName("Image")
        @Expose
        private String image;
        @SerializedName("Iscreate")
        @Expose
        private String Iscreate;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getTotalAudio() {
            return totalAudio;
        }

        public void setTotalAudio(String totalAudio) {
            this.totalAudio = totalAudio;
        }

        public String getTotalDuration() {
            return totalDuration;
        }

        public void setTotalDuration(String totalDuration) {
            this.totalDuration = totalDuration;
        }

        public String getTotalhour() {
            return totalhour;
        }

        public void setTotalhour(String totalhour) {
            this.totalhour = totalhour;
        }

        public String getTotalminute() {
            return totalminute;
        }

        public void setTotalminute(String totalminute) {
            this.totalminute = totalminute;
        }

        public String getIscreate() {
            return Iscreate;
        }

        public void setIscreate(String iscreate) {
            Iscreate = iscreate;
        }
    }
}
