package com.qltech.bws.DashboardModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainPlayModel {
    @SerializedName("ResponseData")
    @Expose
    private List<ResponseData> responseData = null;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("ResponseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("ResponseStatus")
    @Expose
    private String responseStatus;

    public List<ResponseData> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<ResponseData> responseData) {
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
        @SerializedName("GetLibraryID")
        @Expose
        private String getLibraryID;
        @SerializedName("View")
        @Expose
        private String view;
        @SerializedName("UserID")
        @Expose
        private String userID;
        @SerializedName("Details")
        @Expose
        private List<Detail> details = null;

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

        public List<Detail> getDetails() {
            return details;
        }

        public void setDetails(List<Detail> details) {
            this.details = details;
        }

        public class Detail {

            @SerializedName("LibraryID")
            @Expose
            private String libraryID;
            @SerializedName("PatientID")
            @Expose
            private String patientID;
            @SerializedName("LibraryName")
            @Expose
            private String libraryName;
            @SerializedName("LibraryDesc")
            @Expose
            private String libraryDesc;
            @SerializedName("MasterCategory")
            @Expose
            private String masterCategory;
            @SerializedName("SubCategory")
            @Expose
            private String subCategory;
            @SerializedName("LibraryImage")
            @Expose
            private String libraryImage;
            @SerializedName("PlaylistId")
            @Expose
            private String playlistId;
            @SerializedName("PlaylistName")
            @Expose
            private String playlistName;
            @SerializedName("PlaylistImage")
            @Expose
            private String playlistImage;
            @SerializedName("Audiolist")
            @Expose
            private List<Audiolist> audiolist = null;

            public String getLibraryID() {
                return libraryID;
            }

            public void setLibraryID(String libraryID) {
                this.libraryID = libraryID;
            }

            public String getPatientID() {
                return patientID;
            }

            public void setPatientID(String patientID) {
                this.patientID = patientID;
            }

            public String getLibraryName() {
                return libraryName;
            }

            public void setLibraryName(String libraryName) {
                this.libraryName = libraryName;
            }

            public String getLibraryDesc() {
                return libraryDesc;
            }

            public void setLibraryDesc(String libraryDesc) {
                this.libraryDesc = libraryDesc;
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

            public String getLibraryImage() {
                return libraryImage;
            }

            public void setLibraryImage(String libraryImage) {
                this.libraryImage = libraryImage;
            }

            public String getPlaylistId() {
                return playlistId;
            }

            public void setPlaylistId(String playlistId) {
                this.playlistId = playlistId;
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

            public List<Audiolist> getAudiolist() {
                return audiolist;
            }

            public void setAudiolist(List<Audiolist> audiolist) {
                this.audiolist = audiolist;
            }
            public class Audiolist {

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
            }
        }
    }
}
