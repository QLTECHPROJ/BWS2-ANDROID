package com.brainwellnessspa.DassAssSliderTwo.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssessmentQusModel {
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
    override fun toString(): String {
        return "assessmentQuestionModel(responseData=$responseData, responseCode=$responseCode, responseMessage=$responseMessage, responseStatus=$responseStatus)"
    }
}
    class ResponseData {
        @SerializedName("Question")
        @Expose
        var question: String? = null

        @SerializedName("Type")
        @Expose
        var type: Int? = 0

        @SerializedName("Answer")
        @Expose
        var answer: String? = null

        @SerializedName("Loop")
        @Expose
        var loop: String? = null


        override fun toString(): String {
            return "ResponseData(question=$question, type=$type, answer=$answer, loop=$loop)"
        }

    }