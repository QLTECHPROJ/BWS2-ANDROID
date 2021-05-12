package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AudioDetailModel {
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

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = null

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = null

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = null

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = null

        @SerializedName("AudioDescription")
        @Expose
        var audioDescription: String? = null

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = null

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = null

        @SerializedName("Bitrate")
        @Expose
        var bitrate: String? = null
    }
}