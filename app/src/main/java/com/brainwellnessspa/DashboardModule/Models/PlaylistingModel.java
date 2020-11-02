package com.brainwellnessspa.DashboardModule.Models;

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

        public String getIscreate() {
            return Iscreate;
        }

        public void setIscreate(String iscreate) {
            Iscreate = iscreate;
        }
    }
}
