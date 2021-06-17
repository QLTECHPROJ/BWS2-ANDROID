package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VerifyPinModel {
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
        @SerializedName("MainAccountID")
        @Expose
        var mainAccountID: String? = ""

        @SerializedName("UserId")
        @Expose
        var userId: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("Email")
        @Expose
        var email: String? = ""

        @SerializedName("Mobile")
        @Expose
        var mobile: String? = ""

        @SerializedName("Image")
        @Expose
        var image: String? = ""

        @SerializedName("DOB")
        @Expose
        var dob: String? = ""

        @SerializedName("isProfileCompleted")
        @Expose
        var isProfileCompleted: String? = ""

        @SerializedName("isAssessmentCompleted")
        @Expose
        var isAssessmentCompleted: String? = ""

        @SerializedName("ScoreLevel")
        @Expose
        var scoreLevel: String? = ""

        @SerializedName("indexScore")
        @Expose
        var indexScore: String? = ""

        @SerializedName("planDetails")
        @Expose
        var planDetails: List<PlanDetails>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""

        @SerializedName("AvgSleepTime")
        @Expose
        var avgSleepTime: String? = ""

        @SerializedName("AreaOfFocus")
        @Expose
        var areaOfFocus: List<AreaOfFocus>? = null

        class PlanDetails {

        }

        class AreaOfFocus {
            @SerializedName("MainCat")
            @Expose
            var mainCat: String? = ""

            @SerializedName("RecommendedCat")
            @Expose
            var recommendedCat: String? = ""
        }
    }
}