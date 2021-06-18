package com.brainwellnessspa.dashboardOldModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ViewAllPlayListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = ""

    class ResponseData {


        @SerializedName("GetLibraryID")
        @Expose
        var getLibraryID: String? = ""

        @SerializedName("View")
        @Expose
        var view: String? = ""

        @SerializedName("UserId")
        @Expose
        var userId: String? = ""

        @SerializedName("Details")
        @Expose
        var details: List<Detail>? = null

        class Detail {
            @SerializedName("PlaylistID")
            @Expose
            var playlistID: String? = ""

            @SerializedName("PlaylistName")
            @Expose
            var playlistName: String? = ""

            @SerializedName("PlaylistDesc")
            @Expose
            var playlistDesc: String? = ""

            @SerializedName("PlaylistMastercat")
            @Expose
            var playlistMastercat: String? = ""

            @SerializedName("PlaylistSubcat")
            @Expose
            var playlistSubcat: String? = ""

            @SerializedName("PlaylistImage")
            @Expose
            var playlistImage: String? = ""

            @SerializedName("Created")
            @Expose
            var created: String? = ""

            @SerializedName("TotalAudio")
            @Expose
            var totalAudio: String? = ""

            @SerializedName("TotalDuration")
            @Expose
            var totalDuration: String? = ""

            @SerializedName("Totalhour")
            @Expose
            var totalhour: String? = ""

            @SerializedName("Totalminute")
            @Expose
            var totalminute: String? = ""
        }
    }
}