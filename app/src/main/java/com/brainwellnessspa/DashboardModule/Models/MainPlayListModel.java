package com.brainwellnessspa.DashboardModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MainPlayListModel {
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

    public static class ResponseData {
        @SerializedName("GetLibraryID")
        @Expose
        private String getLibraryID;
        @SerializedName("View")
        @Expose
        private String view;
        @SerializedName("UserID")
        @Expose
        private String userID;
        @SerializedName("IsLock")
        @Expose
        private String IsLock;
        @SerializedName("Details")
        @Expose
        private ArrayList<Detail> details = null;

        public String getGetLibraryID() {
            return getLibraryID;
        }

        public void setGetLibraryID(String getLibraryID) {
            this.getLibraryID = getLibraryID;
        }

        public String getView() {
            return view;
        }

        public void setView(String view) {
            this.view = view;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getIsLock() {
            return IsLock;
        }

        public void setIsLock(String isLock) {
            IsLock = isLock;
        }

        public ArrayList<Detail> getDetails() {
            return details;
        }

        public void setDetails(ArrayList<Detail> details) {
            this.details = details;
        }

        public static class Detail implements Parcelable {
            @SerializedName("PlaylistID")
            @Expose
            private String playlistID;
            @SerializedName("TotalAudio")
            @Expose
            private String totalAudio;
            @SerializedName("Totalhour")
            @Expose
            private String totalhour;
            @SerializedName("Totalminute")
            @Expose
            private String totalminute;
            @SerializedName("PlaylistName")
            @Expose
            private String playlistName;
            @SerializedName("PlaylistDesc")
            @Expose
            private String playlistDesc;
            @SerializedName("MasterCategory")
            @Expose
            private String masterCategory;
            @SerializedName("SubCategory")
            @Expose
            private String subCategory;
            @SerializedName("PlaylistImage")
            @Expose
            private String playlistImage;
            @SerializedName("PlaylistId")
            @Expose
            private String playlistId;
            @SerializedName("Audiolist")
            @Expose
            private ArrayList<Audiolist> audiolist = null;

            public Detail() {}
            protected Detail(Parcel in) {
                playlistID = in.readString();
                totalAudio = in.readString();
                playlistName = in.readString();
                playlistDesc = in.readString();
                masterCategory = in.readString();
                subCategory = in.readString();
                playlistImage = in.readString();
                playlistId = in.readString();
                totalhour = in.readString();
                totalminute = in.readString();
                audiolist = in.createTypedArrayList(Audiolist.CREATOR);
            }

            public static final Creator<Detail> CREATOR = new Creator<Detail>() {
                @Override
                public Detail createFromParcel(Parcel in) {
                    return new Detail(in);
                }

                @Override
                public Detail[] newArray(int size) {
                    return new Detail[size];
                }
            };

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

            public static Creator<Detail> getCREATOR() {
                return CREATOR;
            }

            public String getPlaylistID() {
                return playlistID;
            }

            public void setPlaylistID(String playlistID) {
                this.playlistID = playlistID;
            }

            public String getPlaylistDesc() {
                return playlistDesc;
            }

            public void setPlaylistDesc(String playlistDesc) {
                this.masterCategory = playlistDesc;
            }

            public String getMasterCategory() {
                return masterCategory;
            }

            public void setMasterCategory(String masterCategory) {
                this.masterCategory = masterCategory;
            }

            public String getSubCategory() {
                return subCategory;
            }

            public void setSubCategory(String subCategory) {
                this.subCategory = subCategory;
            }

            public String getPlaylistName() {
                return playlistName;
            }

            public void setPlaylistName(String playlistName) {
                this.playlistName = playlistName;
            }

            public String getPlaylistImage() {
                return playlistImage;
            }

            public void setPlaylistImage(String playlistImage) {
                this.playlistImage = playlistImage;
            }

            public String getPlaylistId() {
                return playlistId;
            }

            public void setPlaylistId(String playlistId) {
                this.playlistId = playlistId;
            }

            public ArrayList<Audiolist> getAudiolist() {
                return audiolist;
            }

            public void setAudiolist(ArrayList<Audiolist> audiolist) {
                this.audiolist = audiolist;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(playlistID);
                parcel.writeString(totalAudio);
                parcel.writeString(playlistName);
                parcel.writeString(playlistDesc);
                parcel.writeString(masterCategory);
                parcel.writeString(subCategory);
                parcel.writeString(playlistImage);
                parcel.writeString(playlistId);
                parcel.writeString(totalhour);
                parcel.writeString(totalminute);
            }

            public static class Audiolist implements Parcelable {
                @SerializedName("AudioID")
                @Expose
                private String audioID;
                @SerializedName("AudioName")
                @Expose
                private String audioName;
                @SerializedName("AudioFile")
                @Expose
                private String audioFile;
                @SerializedName("ImageFile")
                @Expose
                private String imageFile;
                @SerializedName("AudioDuration")
                @Expose
                private String audioDuration;

                public static final Creator<Audiolist> CREATOR = new Creator<Audiolist>() {
                    @Override
                    public Audiolist createFromParcel(Parcel in) {
                        return new Audiolist(in);
                    }

                    @Override
                    public Audiolist[] newArray(int size) {
                        return new Audiolist[size];
                    }
                };

                protected Audiolist(Parcel in) {
                    audioID = in.readString();
                    audioName = in.readString();
                    audioFile = in.readString();
                    imageFile = in.readString();
                    audioDuration = in.readString();
                }

                public String getAudioID() {
                    return audioID;
                }

                public void setAudioID(String audioID) {
                    this.audioID = audioID;
                }

                public String getAudioName() {
                    return audioName;
                }

                public void setAudioName(String audioName) {
                    this.audioName = audioName;
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

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel parcel, int i) {
                    parcel.writeString(audioID);
                    parcel.writeString(audioName);
                    parcel.writeString(audioFile);
                    parcel.writeString(imageFile);
                    parcel.writeString(audioDuration);
                }
            }
        }
    }
}
