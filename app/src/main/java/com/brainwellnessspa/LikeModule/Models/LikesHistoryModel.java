package com.brainwellnessspa.LikeModule.Models;

import android.os.Parcelable;

import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LikesHistoryModel {
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

    public static class ResponseData {
        @SerializedName("Audio")
        @Expose
        private List<Audio> audio = null;
        @SerializedName("Playlist")
        @Expose
        private List<Playlist> playlist = null;

        public List<Audio> getAudio() {
            return audio;
        }

        public void setAudio(List<Audio> audio) {
            this.audio = audio;
        }

        public List<Playlist> getPlaylist() {
            return playlist;
        }

        public void setPlaylist(List<Playlist> playlist) {
            this.playlist = playlist;
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
            @SerializedName("Audiomastercat")
            @Expose
            private String audiomastercat;
            @SerializedName("AudioSubCategory")
            @Expose
            private String audioSubCategory;
            @SerializedName("AudioDuration")
            @Expose
            private String audioDuration;
            @SerializedName("AudioDirection")
            @Expose
            private String audioDirection;
            @SerializedName("Like")
            @Expose
            private String like;
            @SerializedName("Download")
            @Expose
            private String download;

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

        public static class Audio {
            @SerializedName("ID")
            @Expose
            private String iD;
            @SerializedName("PlaylistId")
            @Expose
            private String playlistId;
            @SerializedName("Name")
            @Expose
            private String name;
            @SerializedName("AudioFile")
            @Expose
            private String audioFile;
            @SerializedName("ImageFile")
            @Expose
            private String imageFile;
            @SerializedName("Audiomastercat")
            @Expose
            private String audiomastercat;
            @SerializedName("AudioSubCategory")
            @Expose
            private String audioSubCategory;
            @SerializedName("AudioDuration")
            @Expose
            private String audioDuration;
            @SerializedName("AudioDirection")
            @Expose
            private String audioDirection;
            @SerializedName("Like")
            @Expose
            private String like;
            @SerializedName("Download")
            @Expose
            private String download;
            @SerializedName("IsLock")
            @Expose
            private String isLock;
            @SerializedName("IsPlay")
            @Expose
            private String isPlay;

            public String getID() {
                return iD;
            }

            public void setID(String iD) {
                this.iD = iD;
            }

            public String getPlaylistId() {
                return playlistId;
            }

            public void setPlaylistId(String playlistId) {
                this.playlistId = playlistId;
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

            public String getIsLock() {
                return isLock;
            }

            public void setIsLock(String isLock) {
                this.isLock = isLock;
            }

            public String getIsPlay() {
                return isPlay;
            }

            public void setIsPlay(String isPlay) {
                this.isPlay = isPlay;
            }
        }

        public static class Playlist {
            @SerializedName("PlaylistID")
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
            @SerializedName("IsLock")
            @Expose
            private String isLock;

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

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public String getIsLock() {
                return isLock;
            }

            public void setIsLock(String isLock) {
                this.isLock = isLock;
            }

            public static class Audiolist {
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
                @SerializedName("Audiomastercat")
                @Expose
                private String audiomastercat;
                @SerializedName("AudioSubCategory")
                @Expose
                private String audioSubCategory;
                @SerializedName("AudioDuration")
                @Expose
                private String audioDuration;
                @SerializedName("AudioDirection")
                @Expose
                private String audioDirection;
                @SerializedName("Like")
                @Expose
                private String like;
                @SerializedName("Download")
                @Expose
                private String download;

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
    }
}
