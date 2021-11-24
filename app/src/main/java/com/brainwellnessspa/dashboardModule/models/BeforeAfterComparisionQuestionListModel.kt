package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BeforeAfterComparisionQuestionListModel {
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
        @SerializedName("questions")
        @Expose
        var questions: List<Question>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        class Question {
            @SerializedName("option_type")
            @Expose
            var optionType: String? = null

            @SerializedName("session_title")
            @Expose
            var sessionTitle: String? = null

            @SerializedName("step_title")
            @Expose
            var stepTitle: String? = null

            @SerializedName("question")
            @Expose
            var question: String? = null

            @SerializedName("step_short_description")
            @Expose
            var stepShortDescription: String? = null

            @SerializedName("step_long_description")
            @Expose
            var stepLongDescription: String? = null

            @SerializedName("question_options")
            @Expose
            var questionOptions: List<String>? = null
        }
    }
}