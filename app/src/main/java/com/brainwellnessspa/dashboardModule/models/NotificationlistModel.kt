package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationlistModel {
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

        @SerializedName("Image")
        @Expose
        var image: String? = ""

        @SerializedName("Msg")
        @Expose
        var msg: String? = ""

        @SerializedName("Desc")
        @Expose
        var desc: String? = ""

        @SerializedName("DurationTime")
        @Expose
        var durationTime: String? = ""
    }

}