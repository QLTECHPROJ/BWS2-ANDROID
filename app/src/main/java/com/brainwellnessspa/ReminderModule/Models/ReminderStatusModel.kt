package com.brainwellnessspa.ReminderModule.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReminderStatusModel {
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
        @SerializedName("Day")
        @Expose
        var day: String? = null

        @SerializedName("Time")
        @Expose
        var time: String? = null

        @SerializedName("IsCheck")
        @Expose
        var isCheck: String? = null
    }
}