package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AudioDetailModel {
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
        @SerializedName("ID")
        @Expose
        var id: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = ""

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = ""

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = ""

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = ""

        @SerializedName("AudioDescription")
        @Expose
        var audioDescription: String? = ""

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = ""

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = ""

        @SerializedName("Bitrate")
        @Expose
        var bitrate: String? = ""
    }
}