package com.brainwellnessspa.DashboardModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnlockAudioList {


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

        @SerializedName("IsLock")
        @Expose
        private String isLock;
        @SerializedName("ID")
        @Expose
        private List<String> iD = null;

        public String getIsLock() {
            return isLock;
        }

        public void setIsLock(String isLock) {
            this.isLock = isLock;
        }

        public List<String> getID() {
            return iD;
        }

        public void setID(List<String> iD) {
            this.iD = iD;
        }

    }
}
