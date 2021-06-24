package com.brainwellnessspa.dashboardOldModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PreviousAppointmentsModel {
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
        @SerializedName("Category")
        @Expose
        var category: String? = null

        @SerializedName("CatMenual")
        @Expose
        var catMenual: String? = null

        @SerializedName("Image")
        @Expose
        var image: String? = null
    }
}