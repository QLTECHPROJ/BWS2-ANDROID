package com.brainwellnessspa.dashboardModule.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ViewAllAudioListModel {
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

    public static class ResponseData  implements Parcelable {


        @SerializedName("HomeAudioID")
        @Expose
        public String homeAudioID;
        @SerializedName("View")
        @Expose
        public String view;
        @SerializedName("UserId")
        @Expose
        public String userId;
        @SerializedName("Details")
        @Expose
        public ArrayList<Detail> details = null;

        public String getHomeAudioID() {
            return homeAudioID;
        }

        public void setHomeAudioID(String homeAudioID) {
            this.homeAudioID = homeAudioID;
        }

        public String getView() {
            return view;
        }

        public void setView(String view) {
            this.view = view;
        }

        public String getCoUserId() {
            return userId;
        }

        public void setCoUserId(String userId) {
            this.userId = userId;
        }

        public ArrayList<Detail> getDetails() {
            return details;
        }

        public void setDetails(ArrayList<Detail> details) {
            this.details = details;
        }

        public static Creator<MainAudioModel.ResponseData> getCREATOR() {
            return CREATOR;
        }

        public static final Creator<MainAudioModel.ResponseData> CREATOR = new Creator<MainAudioModel.ResponseData>() {
            @Override
            public MainAudioModel.ResponseData createFromParcel(Parcel in) {
                return new MainAudioModel.ResponseData(in);
            }

            @Override
            public MainAudioModel.ResponseData[] newArray(int size) {
                return new MainAudioModel.ResponseData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(homeAudioID);
            parcel.writeString(view);
            parcel.writeString(userId);
        }

        public static class Detail  implements Parcelable {
            @SerializedName("ID")
            @Expose
            public String ID;
            @SerializedName("Name")
            @Expose
            public String name;
            @SerializedName("AudioFile")
            @Expose
            public String audioFile;
            @SerializedName("ImageFile")
            @Expose
            public String imageFile;
            @SerializedName("AudioDuration")
            @Expose
            public String audioDuration;
            @SerializedName("AudioDirection")
            @Expose
            public String audioDirection;
            @SerializedName("Audiomastercat")
            @Expose
            public String audiomastercat;
            @SerializedName("AudioSubCategory")
            @Expose
            public String audioSubCategory;
            @SerializedName("IsPlay")
            @Expose
            public String isPlay;
            @SerializedName("Bitrate")
            @Expose
            public String bitrate;
            public Detail() {
            }
            protected Detail(Parcel in) {
                ID = in.readString();
                name = in.readString();
                audioFile = in.readString();
                imageFile = in.readString();
                audioDuration = in.readString();
                audioDirection = in.readString();
                isPlay = in.readString();
                audiomastercat = in.readString();
                audioSubCategory = in.readString();
                bitrate = in.readString();
            }

            public static final Creator<MainAudioModel.ResponseData.Detail> CREATOR = new Creator<MainAudioModel.ResponseData.Detail>() {
                @Override
                public MainAudioModel.ResponseData.Detail createFromParcel(Parcel in) {
                    return new MainAudioModel.ResponseData.Detail(in);
                }

                @Override
                public MainAudioModel.ResponseData.Detail[] newArray(int size) {
                    return new MainAudioModel.ResponseData.Detail[size];
                }
            };

            public String getID() {
                return ID;
            }

            public void setID(String id) {
                this.ID = id;
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

            public String getBitrate() {
                return bitrate;
            }

            public void setBitrate(String bitrate) {
                this.bitrate = bitrate;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(ID);
                parcel.writeString(name);
                parcel.writeString(audioFile);
                parcel.writeString(imageFile);
                parcel.writeString(audioDuration);
                parcel.writeString(audioDirection);
                parcel.writeString(isPlay);
                parcel.writeString(audiomastercat);
                parcel.writeString(audioSubCategory);
                parcel.writeString(bitrate);
            }
        }
    }
}
