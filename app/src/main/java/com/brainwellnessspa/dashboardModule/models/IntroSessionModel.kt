package com.brainwellnessspa.dashboardModule.models
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class IntroSessionModel {
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
        @SerializedName("freeSessionImg")
        @Expose
        var freeSessionImg: String? = null

        @SerializedName("freeSessionTitle")
        @Expose
        var freeSessionTitle: String? = null

        @SerializedName("freeSessionContent")
        @Expose
        var freeSessionContent: String? = null
    }
}