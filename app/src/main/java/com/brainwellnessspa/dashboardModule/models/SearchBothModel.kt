package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SearchBothModel {
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
        var iD: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = ""

        @SerializedName("IsLock")
        @Expose
        var isLock: String? = ""

        @SerializedName("disableAudio")
        @Expose
        var disableAudio: String? = ""

        @SerializedName("IsPlay")
        @Expose
        var isPlay: String? = ""

        @SerializedName("Iscategory")
        @Expose
        var iscategory: String? = ""

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = ""

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = ""

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = ""

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = ""

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = ""

        @SerializedName("Like")
        @Expose
        var like: String? = ""

        @SerializedName("Download")
        @Expose
        var download: String? = ""

        @SerializedName("Bitrate")
        @Expose
        var bitrate: String? = ""
    }
}