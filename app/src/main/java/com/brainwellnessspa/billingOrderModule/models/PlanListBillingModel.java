package com.brainwellnessspa.billingOrderModule.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlanListBillingModel {

        @SerializedName("ResponseData")
        @Expose
        public ResponseData responseData;
        @SerializedName("ResponseCode")
        @Expose
        public String responseCode;
        @SerializedName("ResponseMessage")
        @Expose
        public String responseMessage;
        @SerializedName("ResponseStatus")
        @Expose
        public String responseStatus;

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
            public String image;
            @SerializedName("Title")
            @Expose
            public String title;
            @SerializedName("Desc")
            @Expose
            public String desc;
            @SerializedName("Plan")
            @Expose
            public ArrayList<Plan> plan = null;

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

        public ArrayList<Plan> getPlan() {
            return plan;
        }

        public void setPlan(ArrayList<Plan> plan) {
            this.plan = plan;
        }

        public class Plan{

                @SerializedName("PlanPosition")
                @Expose
                public String planPosition;
                @SerializedName("PlanID")
                @Expose
                public String planID;
                @SerializedName("PlanAmount")
                @Expose
                public String planAmount;
                @SerializedName("PlanCurrency")
                @Expose
                public String planCurrency;
                @SerializedName("PlanInterval")
                @Expose
                public String planInterval;
                @SerializedName("PlanImage")
                @Expose
                public String planImage;
                @SerializedName("PlanTenure")
                @Expose
                public String planTenure;
                @SerializedName("PlanNextRenewal")
                @Expose
                public String planNextRenewal;
                @SerializedName("SubName")
                @Expose
                public String subName;
                @SerializedName("RecommendedFlag")
                @Expose
                public String recommendedFlag;
                @SerializedName("PlanFlag")
                @Expose
                public String planFlag;
                @SerializedName("PlanFeatures")
                @Expose
                public ArrayList<PlanFeature> planFeatures = null;

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

                public ArrayList<PlanFeature> getPlanFeatures() {
                    return planFeatures;
                }

                public void setPlanFeatures(ArrayList<PlanFeature> planFeatures) {
                    this.planFeatures = planFeatures;
                }

                public class PlanFeature {

                    @SerializedName("Feature")
                    @Expose
                    public String feature;

                    public String getFeature() {
                        return feature;
                    }

                    public void setFeature(String feature) {
                        this.feature = feature;
                    }
                }
            }
        }

}