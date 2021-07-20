package com.brainwellnessspa.assessmentProgressModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssesmentGetDetailsModel {
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
        @SerializedName("indexScore")
        @Expose
        var indexScore: String? = null

        @SerializedName("ScoreLevel")
        @Expose
        var scoreLevel: String? = null

        @SerializedName("AssesmentTitle")
        @Expose
        var assesmentTitle: String? = null

        @SerializedName("AssesmentContent")
        @Expose
        var assesmentContent: String? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null
    }
}