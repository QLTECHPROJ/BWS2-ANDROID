package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveRecommendedCatModel {
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
        @SerializedName("CoUserId")
        @Expose
        var coUserId: String? = null

        @SerializedName("AvgSleepTime")
        @Expose
        var avgSleepTime: String? = null

        @SerializedName("CategoryData")
        @Expose
        var categoryData: List<CategoryData>? = null

        class CategoryData {
            @SerializedName("MainCat")
            @Expose
            var mainCat: String? = null

            @SerializedName("RecommendedCat")
            @Expose
            var recommendedCat: String? = null
        }
    }
}