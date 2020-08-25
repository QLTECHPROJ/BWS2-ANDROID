package com.qltech.bws.BillingOrderModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
        @SerializedName("Feature")
        @Expose
        private Feature feature;

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        public String getOrderTotal() {
            return orderTotal;
        }

        public void setOrderTotal(String orderTotal) {
            this.orderTotal = orderTotal;
        }

        public Feature getFeature() {
            return feature;
        }

        public void setFeature(Feature feature) {
            this.feature = feature;
        }
        public class Feature {

            @SerializedName("Feature1")
            @Expose
            private String feature1;
            @SerializedName("Feature2")
            @Expose
            private String feature2;
            @SerializedName("Feature3")
            @Expose
            private String feature3;
            @SerializedName("Feature4")
            @Expose
            private String feature4;

            public String getFeature1() {
                return feature1;
            }

            public void setFeature1(String feature1) {
                this.feature1 = feature1;
            }

            public String getFeature2() {
                return feature2;
            }

            public void setFeature2(String feature2) {
                this.feature2 = feature2;
            }

            public String getFeature3() {
                return feature3;
            }

            public void setFeature3(String feature3) {
                this.feature3 = feature3;
            }

            public String getFeature4() {
                return feature4;
            }

            public void setFeature4(String feature4) {
                this.feature4 = feature4;
            }
        }
    }
}
