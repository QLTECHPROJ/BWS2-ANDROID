package com.qltech.bws.DashboardModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AppointmentDetailModel implements Parcelable {
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

    protected AppointmentDetailModel(Parcel in) {
        responseData = in.readParcelable(ResponseData.class.getClassLoader());
        responseCode = in.readString();
        responseMessage = in.readString();
        responseStatus = in.readString();
    }

    public static final Creator<AppointmentDetailModel> CREATOR = new Creator<AppointmentDetailModel>() {
        @Override
        public AppointmentDetailModel createFromParcel(Parcel in) {
            return new AppointmentDetailModel(in);
        }

        @Override
        public AppointmentDetailModel[] newArray(int size) {
            return new AppointmentDetailModel[size];
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
        @SerializedName("Id")
        @Expose
        private String id;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Desc")
        @Expose
        private String desc;
        @SerializedName("Facilitator")
        @Expose
        private String facilitator;
        @SerializedName("UserName")
        @Expose
        private String userName;
        @SerializedName("Image")
        @Expose
        private String image;
        @SerializedName("Date")
        @Expose
        private String date;
        @SerializedName("Duration")
        @Expose
        private String duration;
        @SerializedName("Time")
        @Expose
        private String time;
        @SerializedName("BookUrl")
        @Expose
        private String bookUrl;
        @SerializedName("Status")
        @Expose
        private String status;
        @SerializedName("Audio")
        @Expose
        private ArrayList<Audio> audio;
        @SerializedName("Booklet")
        @Expose
        private String booklet;
        @SerializedName("MyAnswers")
        @Expose
        private String myAnswers;

        protected ResponseData(Parcel in) {
            id = in.readString();
            name = in.readString();
            desc = in.readString();
            facilitator = in.readString();
            userName = in.readString();
            image = in.readString();
            date = in.readString();
            duration = in.readString();
            time = in.readString();
            bookUrl = in.readString();
            status = in.readString();
            audio = in.createTypedArrayList(Audio.CREATOR);
            booklet = in.readString();
            myAnswers = in.readString();
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getBookUrl() {
            return bookUrl;
        }

        public void setBookUrl(String bookUrl) {
            this.bookUrl = bookUrl;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFacilitator() {
            return facilitator;
        }

        public void setFacilitator(String facilitator) {
            this.facilitator = facilitator;
        }

        public ArrayList<Audio> getAudio() {
            return audio;
        }

        public void setAudio(ArrayList<Audio> audio) {
            this.audio = audio;
        }

        public String getBooklet() {
            return booklet;
        }

        public void setBooklet(String booklet) {
            this.booklet = booklet;
        }

        public String getMyAnswers() {
            return myAnswers;
        }

        public void setMyAnswers(String myAnswers) {
            this.myAnswers = myAnswers;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(name);
            parcel.writeString(desc);
            parcel.writeString(facilitator);
            parcel.writeString(userName);
            parcel.writeString(image);
            parcel.writeString(date);
            parcel.writeString(duration);
            parcel.writeString(time);
            parcel.writeString(bookUrl);
            parcel.writeString(status);
            parcel.writeTypedList(audio);
            parcel.writeString(booklet);
            parcel.writeString(myAnswers);
        }
    }

    public static class Audio implements Parcelable {

        @SerializedName("ID")
        @Expose
        private String iD;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("AudioFile")
        @Expose
        private String audioFile;
        @SerializedName("ImageFile")
        @Expose
        private String imageFile;
        @SerializedName("AudioDuration")
        @Expose
        private String audioDuration;
        @SerializedName("Audiomastercat")
        @Expose
        private String audiomastercat;
        @SerializedName("AudioSubCategory")
        @Expose
        private String audioSubCategory;
        @SerializedName("AudioDirection")
        @Expose
        private String audioDirection;
        @SerializedName("Like")
        @Expose
        private String like;
        @SerializedName("Download")
        @Expose
        private String download;

        protected Audio(Parcel in) {
            iD = in.readString();
            name = in.readString();
            audioFile = in.readString();
            imageFile = in.readString();
            audioDuration = in.readString();
            audiomastercat = in.readString();
            audioSubCategory = in.readString();
            audioDirection = in.readString();
            like = in.readString();
            download = in.readString();
        }

        public static final Creator<Audio> CREATOR = new Creator<Audio>() {
            @Override
            public Audio createFromParcel(Parcel in) {
                return new Audio(in);
            }

            @Override
            public Audio[] newArray(int size) {
                return new Audio[size];
            }
        };

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

        public String getAudioDirection() {
            return audioDirection;
        }

        public void setAudioDirection(String audioDirection) {
            this.audioDirection = audioDirection;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(iD);
            parcel.writeString(name);
            parcel.writeString(audioFile);
            parcel.writeString(imageFile);
            parcel.writeString(audioDuration);
            parcel.writeString(audiomastercat);
            parcel.writeString(audioSubCategory);
            parcel.writeString(audioDirection);
            parcel.writeString(like);
            parcel.writeString(download);
        }
    }
}
