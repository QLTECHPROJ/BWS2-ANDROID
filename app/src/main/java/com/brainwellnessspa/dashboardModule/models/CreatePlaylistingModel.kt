package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class CreatePlaylistingModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

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
        @SerializedName("PlaylistID")
        @Expose
        var id: String? = ""

        @SerializedName("PlaylistName")
        @Expose
        var name: String? = ""

        @SerializedName("PlaylistImage")
        @Expose
        var image: String? = ""

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