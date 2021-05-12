package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VerifyPinModel {
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
        @SerializedName("UserID")
        @Expose
        var userID: String? = null

        @SerializedName("CoUserId")
        @Expose
        var coUserId: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Email")
        @Expose
        var email: String? = null

        @SerializedName("Mobile")
        @Expose
        var mobile: String? = null

        @SerializedName("Image")
        @Expose
        var image: String? = null

        @SerializedName("DOB")
        @Expose
        var dob: String? = null

        @SerializedName("isProfileCompleted")
        @Expose
        var isProfileCompleted: String? = null

        @SerializedName("isAssessmentCompleted")
        @Expose
        var isAssessmentCompleted: String? = null

        @SerializedName("ScoreLevel")
        @Expose
        var scoreLevel: String? = null

        @SerializedName("indexScore")
        @Expose
        var indexScore: String? = null

        @SerializedName("planDetails")
        @Expose
        var planDetails: List<PlanDetails>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        @SerializedName("AvgSleepTime")
        @Expose
        var avgSleepTime: String? = null

        @SerializedName("AreaOfFocus")
        @Expose
        var areaOfFocus: List<AreaOfFocus>? = null

        class PlanDetails {

        }

        class AreaOfFocus {
            @SerializedName("MainCat")
            @Expose
            var mainCat: String? = null

            @SerializedName("RecommendedCat")
            @Expose
            var recommendedCat: String? = null
        }
    }
}