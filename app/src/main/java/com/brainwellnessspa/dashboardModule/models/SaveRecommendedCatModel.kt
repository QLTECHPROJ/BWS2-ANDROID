package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveRecommendedCatModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

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
        @SerializedName("UserId")
        @Expose
        var coUserId: String? = ""

        @SerializedName("AvgSleepTime")
        @Expose
        var avgSleepTime: String? = ""

        @SerializedName("CategoryData")
        @Expose
        var categoryData: List<CategoryData>? = null

        class CategoryData {
            @SerializedName("MainCat")
            @Expose
            var mainCat: String? = ""

            @SerializedName("RecommendedCat")
            @Expose
            var recommendedCat: String? = ""
        }
    }
}