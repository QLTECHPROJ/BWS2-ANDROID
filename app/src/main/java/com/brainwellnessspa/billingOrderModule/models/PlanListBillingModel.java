package com.brainwellnessspa.billingOrderModule.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlanListBillingModel  implements Parcelable{
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

    protected PlanListBillingModel(Parcel in) {
        responseData = in.readParcelable(ResponseData.class.getClassLoader());
        responseCode = in.readString();
        responseMessage = in.readString();
        responseStatus = in.readString();
    }

    public static final Creator<PlanListBillingModel> CREATOR = new Creator<PlanListBillingModel>() {
        @Override
        public PlanListBillingModel createFromParcel(Parcel in) {
            return new PlanListBillingModel(in);
        }

        @Override
        public PlanListBillingModel[] newArray(int size) {
            return new PlanListBillingModel[size];
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

    public static class ResponseData implements Parcelable{

        @SerializedName("Image")
        @Expose
        private String image;
        @SerializedName("Title")
        @Expose
        private String title;
        @SerializedName("Desc")
        @Expose
        private String desc;
        @SerializedName("Plan")
        @Expose
        private ArrayList<Plan> plan = null;

        protected ResponseData(Parcel in) {
            image = in.readString();
            title = in.readString();
            desc = in.readString();
            plan = in.createTypedArrayList(Plan.CREATOR);
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

        public ArrayList<Plan> getPlan() {
            return plan;
        }

        public void setPlan(ArrayList<Plan> plan) {
            this.plan = plan;
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
            parcel.writeTypedList(plan);
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
            @SerializedName("RecommendedFlag")
            @Expose
            private String recommendedFlag;
            @SerializedName("PlanFlag")
            @Expose
            private String planFlag;
            @SerializedName("PlanFeatures")
            @Expose
            private List<PlanFeature> planFeatures = null;

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


            public List<PlanFeature> getPlanFeatures() {
                return planFeatures;
            }

            public void setPlanFeatures(List<PlanFeature> planFeatures) {
                this.planFeatures = planFeatures;
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

            public class PlanFeature {

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
}
