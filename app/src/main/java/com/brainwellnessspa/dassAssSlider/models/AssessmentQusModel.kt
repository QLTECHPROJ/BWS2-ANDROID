package com.brainwellnessspa.dassAssSlider.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssessmentQusModel {
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

        @SerializedName("Toptitle")
        @Expose
        var toptitle: String? = null

        @SerializedName("Subtitle")
        @Expose
        var subtitle: String? = null

        @SerializedName("Content")
        @Expose
        var content: List<Content>? = null

        @SerializedName("Questions")
        @Expose
        var questions: List<Questions>? = null

        class Content {

            @SerializedName("condition")
            @Expose
            var condition: String? = null
        }
        class Questions {
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

        }
    }
}