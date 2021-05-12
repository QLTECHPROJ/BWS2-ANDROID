package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddToPlaylistModel {
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

    inner class ResponseData {
        @SerializedName("PlaylistID")
        @Expose
        var playlistID: String? = null

        @SerializedName("ID")
        @Expose
        var iD: String? = null

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = null

        @SerializedName("SortId")
        @Expose
        var sortId: String? = null

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = null

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = null

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = null

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = null

        @SerializedName("Like")
        @Expose
        var like: String? = null

        @SerializedName("Download")
        @Expose
        var download: String? = null
    }
}