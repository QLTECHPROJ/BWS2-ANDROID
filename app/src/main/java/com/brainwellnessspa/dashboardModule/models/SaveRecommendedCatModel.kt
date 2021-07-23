package com.brainwellnessspa.dashboardModule.models

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
        @SerializedName("UserId")
        @Expose
        var userId: String? = null

        @SerializedName("AvgSleepTime")
        @Expose
        var avgSleepTime: String? = null

        @SerializedName("AreaOfFocus")
        @Expose
        var areaOfFocus: List<AreaOfFocus>? = null

        class AreaOfFocus {
            @SerializedName("CatId")
            @Expose
            var catId: String? = null

            @SerializedName("MainCat")
            @Expose
            var mainCat: String? = null

            @SerializedName("RecommendedCat")
            @Expose
            var recommendedCat: String? = null
        }
    }
}