package com.brainwellnessspa.resourceModule.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResourceFilterModel {
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
        @SerializedName("CategoryName")
        @Expose
        var categoryName: String? = null
    }
}