package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RecommendedCategoryModel {
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

        @SerializedName("View")
        @Expose
        var view: String? = null

        @SerializedName("Details")
        @Expose
        var details: List<Detail>? = null

        class Detail {
            @SerializedName("ProblemName")
            @Expose
            var problemName: String? = null
        }
    }
}

