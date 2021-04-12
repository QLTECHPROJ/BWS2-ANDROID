package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CreateNewPlaylistModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

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
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("Iscreate")
        @Expose
        var iscreate: String? = null

    }
}