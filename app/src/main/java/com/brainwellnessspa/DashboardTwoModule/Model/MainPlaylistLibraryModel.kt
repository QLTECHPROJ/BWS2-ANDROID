package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
class MainPlaylistLibraryModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ArrayList<ResponseData>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null
    public class ResponseData {

        @SerializedName("GetLibraryID")
        @Expose
        var getLibraryID: String? = null

        @SerializedName("View")
        @Expose
        var view: String? = null

        @SerializedName("UserID")
        @Expose
        var userID: String? = null

        @SerializedName("CoUserId")
        @Expose
        var coUserId: String? = null

        @SerializedName("Details")
        @Expose
        var details: ArrayList<Detail>? = null

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

            @SerializedName("Created")
            @Expose
            var created: String? = null

            @SerializedName("TotalAudio")
            @Expose
            var totalAudio: String? = null

            @SerializedName("TotalDuration")
            @Expose
            var totalDuration: String? = null

            @SerializedName("Totalhour")
            @Expose
            var totalhour: String? = null

            @SerializedName("Totalminute")
            @Expose
            var totalminute: String? = null
        }

    }
}