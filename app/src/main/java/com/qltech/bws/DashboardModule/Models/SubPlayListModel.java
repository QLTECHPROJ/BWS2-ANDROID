package com.qltech.bws.DashboardModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public class ResponseData {
        @SerializedName("PlaylistID")
        @Expose
        private String playlistID;
        @SerializedName("PlaylistName")
        @Expose
        private String playlistName;
        @SerializedName("PlaylistDesc")
        @Expose
        private String playlistDesc;
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
        @SerializedName("PlaylistSongs")
        @Expose
        private List<PlaylistSong> playlistSongs = null;

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

        public String getPlaylistImage() {
            return playlistImage;
        }

        public void setPlaylistImage(String playlistImage) {
            this.playlistImage = playlistImage;
        }

        public List<PlaylistSong> getPlaylistSongs() {
            return playlistSongs;
        }

        public void setPlaylistSongs(List<PlaylistSong> playlistSongs) {
            this.playlistSongs = playlistSongs;
        }

        public class PlaylistSong {
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
            @SerializedName("AudioID")
            @Expose
            private String audioID;
            @SerializedName("Name")
            @Expose
            private String name;
            @SerializedName("Download")
            @Expose
            private String download;
            @SerializedName("Like")
            @Expose
            private String like;

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

            public String getAudioFile() {
                return audioFile;
            }

            public void setAudioFile(String audioFile) {
                this.audioFile = audioFile;
            }

            public String getAudioID() {
                return audioID;
            }

            public void setAudioID(String audioID) {
                this.audioID = audioID;
            }

        }
    }
}
