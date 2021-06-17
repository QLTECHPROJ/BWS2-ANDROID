package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RecommendedCategoryModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ArrayList<ResponseData>? = null

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

        @SerializedName("View")
        @Expose
        var view: String? = ""

        @SerializedName("Details")
        @Expose
        var details: ArrayList<Detail>? = null

        class Detail {
            @SerializedName("ProblemName")
            @Expose
            var problemName: String? = ""
        }
    }
}

