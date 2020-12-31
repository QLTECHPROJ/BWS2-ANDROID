package com.brainwellnessspa.MembershipModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MembershipPlanListModel implements Parcelable {

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

    protected MembershipPlanListModel(Parcel in) {
        responseData = in.readParcelable(ResponseData.class.getClassLoader());
        responseCode = in.readString();
        responseMessage = in.readString();
        responseStatus = in.readString();
    }

    public static final Creator<MembershipPlanListModel> CREATOR = new Creator<MembershipPlanListModel>() {
        @Override
        public MembershipPlanListModel createFromParcel(Parcel in) {
            return new MembershipPlanListModel(in);
        }

        @Override
        public MembershipPlanListModel[] newArray(int size) {
            return new MembershipPlanListModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(responseData, i);
        parcel.writeString(responseCode);
        parcel.writeString(responseMessage);
        parcel.writeString(responseStatus);
    }

    public static class ResponseData implements Parcelable {
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
        private ArrayList<Plan> plan = null;
        @SerializedName("AudioFiles")
        @Expose
        private ArrayList<AudioFile> audioFiles = null;

        protected ResponseData(Parcel in) {
            image = in.readString();
            title = in.readString();
            desc = in.readString();
            trialPeriod = in.readString();
        }

        public static final Creator<ResponseData> CREATOR = new Creator<ResponseData>() {
            @Override
            public ResponseData createFromParcel(Parcel in) {
                return new ResponseData(in);
            }

            @Override
            public ResponseData[] newArray(int size) {
                return new ResponseData[size];
            }
        };


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

        public ArrayList<Plan> getPlan() {
            return plan;
        }

        public void setPlan(ArrayList<Plan> plan) {
            this.plan = plan;
        }

        public ArrayList<AudioFile> getAudioFiles() {
            return audioFiles;
        }

        public void setAudioFiles(ArrayList<AudioFile> audioFiles) {
            this.audioFiles = audioFiles;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(image);
            parcel.writeString(title);
            parcel.writeString(desc);
            parcel.writeString(trialPeriod);
        }
    }

    public static class Plan implements Parcelable {
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
        private List<PlanFeatures> planFeatures;
        @SerializedName("RecommendedFlag")
        @Expose
        private String recommendedFlag;
        @SerializedName("PlanFlag")
        @Expose
        private String planFlag;

        protected Plan(Parcel in) {
            planPosition = in.readString();
            planID = in.readString();
            planAmount = in.readString();
            planCurrency = in.readString();
            planInterval = in.readString();
            planImage = in.readString();
            planTenure = in.readString();
            planNextRenewal = in.readString();
            subName = in.readString();
            recommendedFlag = in.readString();
            planFlag = in.readString();
        }

        public static final Creator<Plan> CREATOR = new Creator<Plan>() {
            @Override
            public Plan createFromParcel(Parcel in) {
                return new Plan(in);
            }

            @Override
            public Plan[] newArray(int size) {
                return new Plan[size];
            }
        };

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

        public List<PlanFeatures> getPlanFeatures() {
            return planFeatures;
        }

        public void setPlanFeatures(List<PlanFeatures> planFeatures) {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(planPosition);
            parcel.writeString(planID);
            parcel.writeString(planAmount);
            parcel.writeString(planCurrency);
            parcel.writeString(planInterval);
            parcel.writeString(planImage);
            parcel.writeString(planTenure);
            parcel.writeString(planNextRenewal);
            parcel.writeString(subName);
            parcel.writeString(recommendedFlag);
            parcel.writeString(planFlag);
        }
    }

    public class PlanFeatures {
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

    public class AudioFile {
        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("ImageFile")
        @Expose
        private String imageFile;

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

        public String getImageFile() {
            return imageFile;
        }

        public void setImageFile(String imageFile) {
            this.imageFile = imageFile;
        }

    }
}
