package com.qltech.bws.FaqModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FaqListModel implements Parcelable {
    @SerializedName("ResponseData")
    @Expose
    private ArrayList<ResponseData> responseData = null;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("ResponseStatus")
    @Expose
    private String responseStatus;

    protected FaqListModel(Parcel in) {
        responseCode = in.readString();
        responseMessage = in.readString();
        responseStatus = in.readString();
    }

    public static final Creator<FaqListModel> CREATOR = new Creator<FaqListModel>() {
        @Override
        public FaqListModel createFromParcel(Parcel in) {
            return new FaqListModel(in);
        }

        @Override
        public FaqListModel[] newArray(int size) {
            return new FaqListModel[size];
        }
    };

    public ArrayList<ResponseData> getResponseData() {
        return responseData;
    }

    public void setResponseData(ArrayList<ResponseData> responseData) {
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
        parcel.writeString(responseCode);
        parcel.writeString(responseMessage);
        parcel.writeString(responseStatus);
    }

    public static class ResponseData implements Parcelable{
        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Title")
        @Expose
        private String title;
        @SerializedName("Desc")
        @Expose
        private String desc;
        @SerializedName("VideoURL")
        @Expose
        private String videoURL;
        @SerializedName("Category")
        @Expose
        private String Category;

        protected ResponseData(Parcel in) {
            iD = in.readString();
            title = in.readString();
            desc = in.readString();
            videoURL = in.readString();
            Category = in.readString();
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

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
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

        public String getVideoURL() {
            return videoURL;
        }

        public void setVideoURL(String videoURL) {
            this.videoURL = videoURL;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String planFlag) {
            Category = planFlag;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(iD);
            parcel.writeString(title);
            parcel.writeString(desc);
            parcel.writeString(videoURL);
            parcel.writeString(Category);
        }
    }
}
