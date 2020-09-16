package com.qltech.bws.DashboardModule.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewAllPlayListModel {
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
        @SerializedName("GetLibraryID")
        @Expose
        private String getLibraryID;
        @SerializedName("View")
        @Expose
        private String view;
        @SerializedName("IsLock")
        @Expose
        private String IsLock;
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

        public String getIsLock() {
            return IsLock;
        }

        public void setIsLock(String isLock) {
            IsLock = isLock;
        }

        public List<Detail> getDetails() {
            return details;
        }

        public void setDetails(List<Detail> details) {
            this.details = details;
        }

        public class Detail {
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

            public String getPlaylistImage() {
                return playlistImage;
            }

            public void setPlaylistImage(String playlistImage) {
                this.playlistImage = playlistImage;
            }

        }
    }
}
