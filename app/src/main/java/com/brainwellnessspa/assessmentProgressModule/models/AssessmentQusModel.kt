package com.brainwellnessspa.assessmentProgressModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/* Assessment Que API Keystores */
class AssessmentQusModel {
    @SerializedName("ResponseData") @Expose var responseData: ResponseData? = null

    @SerializedName("ResponseCode") @Expose var responseCode: String? = ""

    @SerializedName("ResponseMessage") @Expose var responseMessage: String? = ""

    @SerializedName("ResponseStatus") @Expose var responseStatus: String? = ""

    class ResponseData {
        @SerializedName("Toptitle") @Expose var toptitle: String? = ""

        @SerializedName("Subtitle") @Expose var subtitle: String? = ""

        @SerializedName("Content") @Expose var content: List<Content>? = null

        @SerializedName("Questions") @Expose var questions: List<Questions>? = null

        class Content {
            @SerializedName("condition") @Expose var condition: String? = ""
        }

        class Questions {
            @SerializedName("Question") @Expose var question: String? = ""

            @SerializedName("Type") @Expose var type: Int? = 0

            @SerializedName("Answer") @Expose var answer: String? = ""

            @SerializedName("Loop") @Expose var loop: String? = ""
        }
    }
}