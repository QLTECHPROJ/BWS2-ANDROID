package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class CreatePlaylistingModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

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
        @SerializedName("ID")
        @Expose
        var id: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Image")
        @Expose
        var image: String? = null

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