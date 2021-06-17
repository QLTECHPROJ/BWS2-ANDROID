package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddToPlaylistModel {
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

    inner class ResponseData {
        @SerializedName("PlaylistID")
        @Expose
        var playlistID: String? = ""

        @SerializedName("ID")
        @Expose
        var iD: String? = ""

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = ""

        @SerializedName("SortId")
        @Expose
        var sortId: String? = ""

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = ""

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = ""

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = ""

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = ""

        @SerializedName("Like")
        @Expose
        var like: String? = ""

        @SerializedName("Download")
        @Expose
        var download: String? = ""
    }
}