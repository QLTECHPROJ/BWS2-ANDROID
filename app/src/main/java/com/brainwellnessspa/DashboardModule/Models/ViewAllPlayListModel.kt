package com.brainwellnessspa.DashboardModule.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ViewAllPlayListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null

    class ResponseData {
        @SerializedName("GetLibraryID")
        @Expose
        var getLibraryID: String? = null

        @SerializedName("View")
        @Expose
        var view: String? = null

        @SerializedName("IsLock")
        @Expose
        var isLock: String? = null

        @SerializedName("UserID")
        @Expose
        var userID: String? = null

        @SerializedName("Details")
        @Expose
        var details: List<Detail>? = null

        class Detail {
            @SerializedName("PlaylistID")
            @Expose
            var playlistID: String? = null

            @SerializedName("PlaylistName")
            @Expose
            var playlistName: String? = null

            @SerializedName("PlaylistDesc")
            @Expose
            var playlistDesc: String? = null

            @SerializedName("PlaylistMastercat")
            @Expose
            var playlistMastercat: String? = null

            @SerializedName("PlaylistSubcat")
            @Expose
            var playlistSubcat: String? = null

            @SerializedName("PlaylistImage")
            @Expose
            var playlistImage: String? = null
            var created: String? = null
            var totalAudio: String? = null
            var totalhour: String? = null
            var totalminute: String? = null
            var playlistImageDetails: String? = null
        }
    }
}