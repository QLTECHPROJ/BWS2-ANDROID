package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationlistModel {
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

        @SerializedName("Image")
        @Expose
        var image: String? = null

        @SerializedName("Msg")
        @Expose
        var msg: String? = null

        @SerializedName("Desc")
        @Expose
        var desc: String? = null

        @SerializedName("DurationTime")
        @Expose
        var durationTime: String? = null
    }

}