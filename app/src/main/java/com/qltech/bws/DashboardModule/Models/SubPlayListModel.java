package com.qltech.bws.DashboardModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SubPlayListModel {
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

    public static class ResponseData implements Parcelable {
        @SerializedName("PlaylistID")
        @Expose
        private String playlistID;
        @SerializedName("PlaylistName")
        @Expose
        private String playlistName;
        @SerializedName("PlaylistDesc")
        @Expose
        private String playlistDesc;
        @SerializedName("ReminderTime")
        @Expose
        private String reminderTime;
        @SerializedName("ReminderDay")
        @Expose
        private String reminderDay;
        @SerializedName("IsReminder")
        @Expose
        private String IsReminder;
        @SerializedName("PlaylistMastercat")
        @Expose
        private String playlistMastercat;
        @SerializedName("PlaylistSubcat")
        @Expose
        private String playlistSubcat;
        @SerializedName("PlaylistImage")
        @Expose
        private String playlistImage;
        @SerializedName("TotalAudio")
        @Expose
        private String totalAudio;
        @SerializedName("TotalDuration")
        @Expose
        private String totalDuration;
        @SerializedName("Totalhour")
        @Expose
        private String totalhour;
        @SerializedName("Totalminute")
        @Expose
        private String totalminute;
        @SerializedName("Created")
        @Expose
        private String created;
        @SerializedName("Download")
        @Expose
        private String download;
        @SerializedName("Like")
        @Expose
        private String like;
        @SerializedName("PlaylistSongs")
        @Expose
        private ArrayList<PlaylistSong> playlistSongs = null;

        public ResponseData() {
        }

        protected ResponseData(Parcel in) {
            playlistID = in.readString();
            playlistName = in.readString();
            playlistDesc = in.readString();
            playlistMastercat = in.readString();
            playlistSubcat = in.readString();
            playlistImage = in.readString();
            totalAudio = in.readString();
            totalDuration = in.readString();
            totalhour = in.readString();
            totalminute = in.readString();
            created = in.readString();
            download = in.readString();
            like = in.readString();
            IsReminder = in.readString();
            reminderTime = in.readString();
            reminderDay = in.readString();
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

        public String getIsReminder() {
            return IsReminder;
        }

        public void setIsReminder(String isReminder) {
            IsReminder = isReminder;
        }

        public String getPlaylistID() {
            return playlistID;
        }

        public void setPlaylistID(String playlistID) {
            this.playlistID = playlistID;
        }

        public String getPlaylistName() {
            return playlistName;
        }

        public void setPlaylistName(String playlistName) {
            this.playlistName = playlistName;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getPlaylistDesc() {
            return playlistDesc;
        }

        public void setPlaylistDesc(String playlistDesc) {
            this.playlistDesc = playlistDesc;
        }

        public String getPlaylistMastercat() {
            return playlistMastercat;
        }

        public void setPlaylistMastercat(String playlistMastercat) {
            this.playlistMastercat = playlistMastercat;
        }

        public String getPlaylistSubcat() {
            return playlistSubcat;
        }

        public void setPlaylistSubcat(String playlistSubcat) {
            this.playlistSubcat = playlistSubcat;
        }

        public String getTotalAudio() {
            return totalAudio;
        }

        public void setTotalAudio(String totalAudio) {
            this.totalAudio = totalAudio;
        }

        public String getTotalDuration() {
            return totalDuration;
        }

        public void setTotalDuration(String totalDuration) {
            this.totalDuration = totalDuration;
        }

        public String getReminderTime() {
            return reminderTime;
        }

        public void setReminderTime(String reminderTime) {
            this.reminderTime = reminderTime;
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

        public String getReminderDay() {
            return reminderDay;
        }

        public void setReminderDay(String reminderDay) {
            this.reminderDay = reminderDay;
        }

        public String getPlaylistImage() {
            return playlistImage;
        }

        public void setPlaylistImage(String playlistImage) {
            this.playlistImage = playlistImage;
        }

        public ArrayList<PlaylistSong> getPlaylistSongs() {
            return playlistSongs;
        }

        public void setPlaylistSongs(ArrayList<PlaylistSong> playlistSongs) {
            this.playlistSongs = playlistSongs;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(playlistID);
            parcel.writeString(playlistName);
            parcel.writeString(playlistDesc);
            parcel.writeString(playlistMastercat);
            parcel.writeString(playlistSubcat);
            parcel.writeString(playlistImage);
            parcel.writeString(totalAudio);
            parcel.writeString(totalDuration);
            parcel.writeString(totalhour);
            parcel.writeString(totalminute);
            parcel.writeString(created);
            parcel.writeString(like);
            parcel.writeString(IsReminder);
            parcel.writeString(reminderTime);
            parcel.writeString(reminderDay);
        }

        public static class PlaylistSong implements Parcelable {
            @SerializedName("ID")
            @Expose
            private String iD;
            @SerializedName("ImageFile")
            @Expose
            private String imageFile;
            @SerializedName("PatientID")
            @Expose
            private String patientID;
            @SerializedName("AudioDuration")
            @Expose
            private String audioDuration;
            @SerializedName("PlaylistID")
            @Expose
            private String playlistID;
            @SerializedName("AudioFile")
            @Expose
            private String audioFile;
            @SerializedName("PlaylistAudioId")
            @Expose
            private String playlistAudioId;
            @SerializedName("Name")
            @Expose
            private String name;
            @SerializedName("Download")
            @Expose
            private String download;
            @SerializedName("Like")
            @Expose
            private String like;
            @SerializedName("Audiomastercat")
            @Expose
            private String audiomastercat;
            @SerializedName("AudioSubCategory")
            @Expose
            private String audioSubCategory;
            @SerializedName("AudioDirection")
            @Expose
            private String audioDirection;

            public PlaylistSong() {
            }

            protected PlaylistSong(Parcel in) {
                iD = in.readString();
                imageFile = in.readString();
                patientID = in.readString();
                audioDuration = in.readString();
                playlistID = in.readString();
                audioFile = in.readString();
                name = in.readString();
                download = in.readString();
                like = in.readString();
                audiomastercat = in.readString();
                audioSubCategory = in.readString();
                audioDirection = in.readString();
                playlistAudioId = in.readString();
            }

            public static final Creator<PlaylistSong> CREATOR = new Creator<PlaylistSong>() {
                @Override
                public PlaylistSong createFromParcel(Parcel in) {
                    return new PlaylistSong(in);
                }

                @Override
                public PlaylistSong[] newArray(int size) {
                    return new PlaylistSong[size];
                }
            };

            public String getImageFile() {
                return imageFile;
            }

            public void setImageFile(String imageFile) {
                this.imageFile = imageFile;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDownload() {
                return download;
            }

            public void setDownload(String download) {
                this.download = download;
            }

            public String getLike() {
                return like;
            }

            public void setLike(String like) {
                this.like = like;
            }

            public String getID() {
                return iD;
            }

            public void setID(String iD) {
                this.iD = iD;
            }

            public String getPatientID() {
                return patientID;
            }

            public void setPatientID(String patientID) {
                this.patientID = patientID;
            }

            public String getPlaylistID() {
                return playlistID;
            }

            public void setPlaylistID(String playlistID) {
                this.playlistID = playlistID;
            }

            public String getAudioDuration() {
                return audioDuration;
            }

            public void setAudioDuration(String audioDuration) {
                this.audioDuration = audioDuration;
            }

            public String getPlaylistAudioId() {
                return playlistAudioId;
            }

            public void setPlaylistAudioId(String playlistAudioId) {
                this.playlistAudioId = playlistAudioId;
            }

            public String getAudioFile() {
                return audioFile;
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

            public void setAudioFile(String audioFile) {
                this.audioFile = audioFile;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(iD);
                parcel.writeString(imageFile);
                parcel.writeString(patientID);
                parcel.writeString(audioDuration);
                parcel.writeString(playlistID);
                parcel.writeString(audioFile);
                parcel.writeString(name);
                parcel.writeString(download);
                parcel.writeString(like);
                parcel.writeString(audiomastercat);
                parcel.writeString(audioSubCategory);
                parcel.writeString(audioDirection);
                parcel.writeString(playlistAudioId);
            }
        }
    }
}
