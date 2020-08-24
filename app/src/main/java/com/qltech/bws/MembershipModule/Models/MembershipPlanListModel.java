package com.qltech.bws.MembershipModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MembershipPlanListModel {

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

        @SerializedName("Image")
        @Expose
        private String image;
        @SerializedName("Title")
        @Expose
        private String title;
        @SerializedName("Desc")
        @Expose
        private String desc;
        @SerializedName("TrialPeriod")
        @Expose
        private String trialPeriod;
        @SerializedName("Plan")
        @Expose
        private List<Plan> plan = null;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTrialPeriod() {
            return trialPeriod;
        }

        public void setTrialPeriod(String trialPeriod) {
            this.trialPeriod = trialPeriod;
        }

        public List<Plan> getPlan() {
            return plan;
        }

        public void setPlan(List<Plan> plan) {
            this.plan = plan;
        }
    }

    public class Plan {

        @SerializedName("PlanPosition")
        @Expose
        private String planPosition;
        @SerializedName("PlanID")
        @Expose
        private String planID;
        @SerializedName("PlanAmount")
        @Expose
        private String planAmount;
        @SerializedName("PlanCurrency")
        @Expose
        private String planCurrency;
        @SerializedName("PlanInterval")
        @Expose
        private String planInterval;
        @SerializedName("PlanImage")
        @Expose
        private String planImage;
        @SerializedName("PlanTenure")
        @Expose
        private String planTenure;
        @SerializedName("PlanNextRenewal")
        @Expose
        private String planNextRenewal;
        @SerializedName("SubName")
        @Expose
        private String subName;
        @SerializedName("PlanFeatures")
        @Expose
        private PlanFeatures planFeatures;
        @SerializedName("RecommendedFlag")
        @Expose
        private String recommendedFlag;
        @SerializedName("PlanFlag")
        @Expose
        private String planFlag;

        public String getPlanPosition() {
            return planPosition;
        }

        public void setPlanPosition(String planPosition) {
            this.planPosition = planPosition;
        }

        public String getPlanID() {
            return planID;
        }

        public void setPlanID(String planID) {
            this.planID = planID;
        }

        public String getPlanAmount() {
            return planAmount;
        }

        public void setPlanAmount(String planAmount) {
            this.planAmount = planAmount;
        }

        public String getPlanCurrency() {
            return planCurrency;
        }

        public void setPlanCurrency(String planCurrency) {
            this.planCurrency = planCurrency;
        }

        public String getPlanInterval() {
            return planInterval;
        }

        public void setPlanInterval(String planInterval) {
            this.planInterval = planInterval;
        }

        public String getPlanImage() {
            return planImage;
        }

        public void setPlanImage(String planImage) {
            this.planImage = planImage;
        }

        public String getPlanTenure() {
            return planTenure;
        }

        public void setPlanTenure(String planTenure) {
            this.planTenure = planTenure;
        }

        public String getPlanNextRenewal() {
            return planNextRenewal;
        }

        public void setPlanNextRenewal(String planNextRenewal) {
            this.planNextRenewal = planNextRenewal;
        }

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
        }

        public PlanFeatures getPlanFeatures() {
            return planFeatures;
        }

        public void setPlanFeatures(PlanFeatures planFeatures) {
            this.planFeatures = planFeatures;
        }

        public String getRecommendedFlag() {
            return recommendedFlag;
        }

        public void setRecommendedFlag(String recommendedFlag) {
            this.recommendedFlag = recommendedFlag;
        }

        public String getPlanFlag() {
            return planFlag;
        }

        public void setPlanFlag(String planFlag) {
            this.planFlag = planFlag;
        }

    }

    public class PlanFeatures {
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
