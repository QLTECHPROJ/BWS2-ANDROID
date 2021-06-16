package com.brainwellnessspa.reminderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SelectPlaylistModel {
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
        @SerializedName("ID")
        @Expose
        var iD: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null
    }
}