package com.qltech.bws.DashboardModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchPlaylistModel implements Parcelable {
    public static final Creator<SearchPlaylistModel> CREATOR = new Creator<SearchPlaylistModel>() {
        @Override
        public SearchPlaylistModel createFromParcel(Parcel in) {
            return new SearchPlaylistModel(in);
        }

        @Override
        public SearchPlaylistModel[] newArray(int size) {
            return new SearchPlaylistModel[size];
        }
    };
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

    protected SearchPlaylistModel(Parcel in) {
        responseData = in.createTypedArrayList(ResponseData.CREATOR);
        responseCode = in.readString();
        responseMessage = in.readString();
        responseStatus = in.readString();
    }

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
        parcel.writeTypedList(responseData);
        parcel.writeString(responseCode);
        parcel.writeString(responseMessage);
        parcel.writeString(responseStatus);
    }

    public static class ResponseData implements Parcelable {
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
        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Desc")
        @Expose
        private String desc;
        @SerializedName("Image")
        @Expose
        private String image;
        @SerializedName("IsLock")
        @Expose
        private String isLock;
        @SerializedName("MasterCat")
        @Expose
        private String masterCat;
        @SerializedName("SubCat")
        @Expose
        private String subCat;
        @SerializedName("Download")
        @Expose
        private String download;
        @SerializedName("TotalAudio")
        @Expose
        private String totalAudio;
        @SerializedName("Totalhour")
        @Expose
        private String totalhour;
        @SerializedName("Totalminute")
        @Expose
        private String totalminute;

        public ResponseData(Parcel in) {
            iD = in.readString();
            name = in.readString();
            desc = in.readString();
            image = in.readString();
            isLock = in.readString();
            masterCat = in.readString();
            subCat = in.readString();
            download = in.readString();
            totalAudio = in.readString();
            totalhour = in.readString();
            totalminute = in.readString();
        }

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getIsLock() {
            return isLock;
        }

        public void setIsLock(String isLock) {
            this.isLock = isLock;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTotalAudio() {
            return totalAudio;
        }

        public void setTotalAudio(String totalAudio) {
            this.totalAudio = totalAudio;
        }

        public String getTotalhour() {
            return totalhour;
        }

        public void setTotalhour(String totalhour) {
            this.totalhour = totalhour;
        }

        public String getTotalminute() {
            return totalminute;
        }

        public void setTotalminute(String totalminute) {
            this.totalminute = totalminute;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getMasterCat() {
            return masterCat;
        }

        public void setMasterCat(String masterCat) {
            this.masterCat = masterCat;
        }

        public String getSubCat() {
            return subCat;
        }

        public void setSubCat(String subCat) {
            this.subCat = subCat;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(iD);
            parcel.writeString(name);
            parcel.writeString(desc);
            parcel.writeString(image);
            parcel.writeString(isLock);
            parcel.writeString(masterCat);
            parcel.writeString(subCat);
            parcel.writeString(download);
            parcel.writeString(totalAudio);
            parcel.writeString(totalhour);
            parcel.writeString(totalminute);
        }
    }
}
