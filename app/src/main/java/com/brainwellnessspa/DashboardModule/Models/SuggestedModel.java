package com.brainwellnessspa.DashboardModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SuggestedModel implements Parcelable {
    public static final Creator<SuggestedModel> CREATOR = new Creator<SuggestedModel>() {
        @Override
        public SuggestedModel createFromParcel(Parcel in) {
            return new SuggestedModel(in);
        }

        @Override
        public SuggestedModel[] newArray(int size) {
            return new SuggestedModel[size];
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

    protected SuggestedModel(Parcel in) {
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
        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("IsLock")
        @Expose
        private String isLock;
        @SerializedName("IsPlay")
        @Expose
        private String isPlay;
        @SerializedName("AudioFile")
        @Expose
        private String audioFile;
        @SerializedName("ImageFile")
        @Expose
        private String imageFile;
        @SerializedName("AudioDuration")
        @Expose
        private String audioDuration;
        @SerializedName("AudioDirection")
        @Expose
        private String audioDirection;
        @SerializedName("Audiomastercat")
        @Expose
        private String audiomastercat;
        @SerializedName("AudioSubCategory")
        @Expose
        private String audioSubCategory;
        @SerializedName("Like")
        @Expose
        private String like;
        @SerializedName("Download")
        @Expose
        private String download;

        public ResponseData(Parcel in) {
            iD = in.readString();
            name = in.readString();
            isLock = in.readString();
            isPlay = in.readString();
            imageFile = in.readString();
            audioFile = in.readString();
            audioDuration = in.readString();
            audioDirection = in.readString();
            audiomastercat = in.readString();
            audioSubCategory = in.readString();
            like = in.readString();
            download = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(iD);
            dest.writeString(name);
            dest.writeString(isLock);
            dest.writeString(isPlay);
            dest.writeString(imageFile);
            dest.writeString(audioFile);
            dest.writeString(audioDuration);
            dest.writeString(audioDirection);
            dest.writeString(audiomastercat);
            dest.writeString(audioSubCategory);
            dest.writeString(like);
            dest.writeString(download);
        }

        @Override
        public int describeContents() {
            return 0;
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

        public String getIsLock() {
            return isLock;
        }

        public void setIsLock(String isLock) {
            this.isLock = isLock;
        }

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

        public String getAudioFile() {
            return audioFile;
        }

        public void setAudioFile(String audioFile) {
            this.audioFile = audioFile;
        }

        public String getImageFile() {
            return imageFile;
        }

        public void setImageFile(String imageFile) {
            this.imageFile = imageFile;
        }

        public String getAudioDuration() {
            return audioDuration;
        }

        public void setAudioDuration(String audioDuration) {
            this.audioDuration = audioDuration;
        }

        public String getAudioDirection() {
            return audioDirection;
        }

        public void setAudioDirection(String audioDirection) {
            this.audioDirection = audioDirection;
        }

        public String getAudiomastercat() {
            return audiomastercat;
        }

        public void setAudiomastercat(String audiomastercat) {
            this.audiomastercat = audiomastercat;
        }

        public String getAudioSubCategory() {
            return audioSubCategory;
        }

        public void setAudioSubCategory(String audioSubCategory) {
            this.audioSubCategory = audioSubCategory;
        }

        public String getIsPlay() {
            return isPlay;
        }

        public void setIsPlay(String isPlay) {
            this.isPlay = isPlay;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

    }
}
