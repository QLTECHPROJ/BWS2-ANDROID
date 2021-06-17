package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateNewPlaylistModel {
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
        @SerializedName("PlaylistID")
        @Expose
        var playlistID: String? = ""

        @SerializedName("PlaylistName")
        @Expose
        var playlistName: String? = ""

        @SerializedName("Iscreate")
        @Expose
        var iscreate: String? = ""

    }
}