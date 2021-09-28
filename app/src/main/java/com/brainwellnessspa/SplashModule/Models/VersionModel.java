package com.brainwellnessspa.SplashModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionModel {
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
        @SerializedName("IsForce")
        @Expose
        private String isForce;

        @SerializedName("displayRegister")
        @Expose
        private String displayRegister;
        @SerializedName("segmentKey")
        @Expose
        private String segmentKey;
        @SerializedName("supportText")
        @Expose
        private String supportText;

        @SerializedName("supportEmail")
        @Expose
        private String supportEmail;

        public String getIsForce() {
            return isForce;
        }

        public void setIsForce(String isForce) {
            this.isForce = isForce;
        }

        public String getDisplayRegister() {
            return displayRegister;
        }

        public void setDisplayRegister(String displayRegister) {
            this.displayRegister = displayRegister;
        }

        public String getSegmentKey() {
            return segmentKey;
        }

        public void setSegmentKey(String segmentKey) {
            this.segmentKey = segmentKey;
        }

        public String getSupportText() {
            return supportText;
        }

        public void setSupportText(String supportText) {
            this.supportText = supportText;
        }

        public String getSupportEmail() {
            return supportEmail;
        }

        public void setSupportEmail(String supportEmail) {
            this.supportEmail = supportEmail;
        }
    }
}
