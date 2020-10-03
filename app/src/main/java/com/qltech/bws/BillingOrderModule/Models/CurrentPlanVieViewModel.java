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
        @SerializedName("CardId")
        @Expose
        private String cardId;
        @SerializedName("PlanId")
        @Expose
        private String planId;
        @SerializedName("PlanFlag")
        @Expose
        private String planFlag;
        @SerializedName("invoicePayId")
        @Expose
        private String invoicePayId;
        @SerializedName("PlanStr")
        @Expose
        private String planStr;
        @SerializedName("Activate")
        @Expose
        private String activate;
        @SerializedName("Status")
        @Expose
        private String status;
        @SerializedName("Subtitle")
        @Expose
        private String subtitle;
        @SerializedName("CardDigit")
        @Expose
        private String cardDigit;
        @SerializedName("OrderTotal")
        @Expose
        private String orderTotal;
        @SerializedName("IsActive")
        @Expose
        private String IsActive;
        @SerializedName("expireDate")
        @Expose
        private String expireDate;
        @SerializedName("Reattempt")
        @Expose
        private String reattempt;
        @SerializedName("Feature")
        @Expose
        private List<Feature> feature = null;

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        public String getReattempt() {
            return reattempt;
        }

        public void setReattempt(String reattempt) {
            this.reattempt = reattempt;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getInvoicePayId() {
            return invoicePayId;
        }

        public void setInvoicePayId(String invoicePayId) {
            this.invoicePayId = invoicePayId;
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

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getPlanFlag() {
            return planFlag;
        }

        public void setPlanFlag(String planFlag) {
            this.planFlag = planFlag;
        }

        public String getPlanStr() {
            return planStr;
        }

        public void setPlanStr(String planStr) {
            this.planStr = planStr;
        }

        public String getActivate() {
            return activate;
        }

        public void setActivate(String activate) {
            this.activate = activate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getCardDigit() {
            return cardDigit;
        }

        public void setCardDigit(String cardDigit) {
            this.cardDigit = cardDigit;
        }

        public String getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
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
