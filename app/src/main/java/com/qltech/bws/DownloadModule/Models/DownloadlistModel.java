package com.qltech.bws.DownloadModule.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;

import java.util.ArrayList;
import java.util.List;

public class DownloadlistModel {
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
        @SerializedName("Audio")
        @Expose
        private ArrayList<Audio> audio = null;
        @SerializedName("Playlist")
        @Expose
        private ArrayList<Playlist> playlist = null;

        protected ResponseData(Parcel in) {
            audio = in.createTypedArrayList(Audio.CREATOR);
            playlist = in.createTypedArrayList(Playlist.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(audio);
            dest.writeTypedList(playlist);
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

        public ArrayList<Audio> getAudio() {
            return audio;
        }

        public void setAudio(ArrayList<Audio> audio) {
            this.audio = audio;
        }

        public ArrayList<Playlist> getPlaylist() {
            return playlist;
        }

        public void setPlaylist(ArrayList<Playlist> playlist) {
            this.playlist = playlist;
        }
    }

    public static class Audio implements Parcelable {
        @SerializedName("ID")
        @Expose
        private String audioID;
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
            audioID = in.readString();
            playlistId = in.readString();
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

        public String getAudioID() {
            return audioID;
        }

        public void setAudioID(String audioID) {
            this.audioID = audioID;
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
            parcel.writeString(audioID);
            parcel.writeString(playlistId);
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

    public static class Playlist implements Parcelable {
        @SerializedName("PlaylistId")
        @Expose
        private String playlistId;
        @SerializedName("PlaylistName")
        @Expose
        private String playlistName;
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
        @SerializedName("Audiolist")
        @Expose
        private List<Audiolist> audiolist = null;

        protected Playlist(Parcel in) {
            playlistId = in.readString();
            playlistName = in.readString();
            playlistImage = in.readString();
            totalAudio = in.readString();
            totalDuration = in.readString();
            totalhour = in.readString();
            totalminute = in.readString();
        }

        public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
            @Override
            public Playlist createFromParcel(Parcel in) {
                return new Playlist(in);
            }

            @Override
            public Playlist[] newArray(int size) {
                return new Playlist[size];
            }
        };

        public String getPlaylistId() {
            return playlistId;
        }

        public void setPlaylistId(String playlistId) {
            this.playlistId = playlistId;
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

        public static Creator<Playlist> getCREATOR() {
            return CREATOR;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(playlistId);
            parcel.writeString(playlistName);
            parcel.writeString(playlistImage);
            parcel.writeString(totalAudio);
            parcel.writeString(totalDuration);
            parcel.writeString(totalhour);
            parcel.writeString(totalminute);
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
