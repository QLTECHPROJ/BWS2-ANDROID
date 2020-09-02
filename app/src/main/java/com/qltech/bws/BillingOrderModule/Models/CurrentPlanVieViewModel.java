package com.qltech.bws.BillingOrderModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentPlanVieViewModel {
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

        @SerializedName("Plan")
        @Expose
        private String plan;
        @SerializedName("OrderTotal")
        @Expose
        private String orderTotal;
        @SerializedName("IsActive")
        @Expose
        private String IsActive;
        @SerializedName("Feature")
        @Expose
        private List<Feature> feature = null;

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        public String getIsActive() {
            return IsActive;
        }

        public void setIsActive(String isActive) {
            IsActive = isActive;
        }

        public String getOrderTotal() {
            return orderTotal;
        }

        public void setOrderTotal(String orderTotal) {
            this.orderTotal = orderTotal;
        }

        public List<Feature> getFeature() {
            return feature;
        }

        public void setFeature(List<Feature> feature) {
            this.feature = feature;
        }
        public class Feature {

            @SerializedName("Feature")
            @Expose
            private String feature;

            public String getFeature() {
                return feature;
            }

            public void setFeature(String feature) {
                this.feature = feature;
            }

        }
    }
}
