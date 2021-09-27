package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StepTypeOneSaveDataModel {
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
        @SerializedName("session_title")
        @Expose
        var sessionTitle: String? = null

        @SerializedName("step_title")
        @Expose
        var stepTitle: String? = null

        @SerializedName("step_short_description")
        @Expose
        var stepShortDescription: String? = null

        @SerializedName("step_long_description")
        @Expose
        var stepLongDescription: String? = null

        @SerializedName("step_audio")
        @Expose
        var stepAudio: StepAudio? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        class StepAudio {
            @SerializedName("ID")
            @Expose
            var id: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null

            @SerializedName("AudioFile")
            @Expose
            var audioFile: String? = null

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = null

            @SerializedName("AudioDuration")
            @Expose
            var audioDuration: String? = null

            @SerializedName("AudioDirection")
            @Expose
            var audioDirection: String? = null

            @SerializedName("Audiomastercat")
            @Expose
            var audiomastercat: String? = null

            @SerializedName("AudioSubCategory")
            @Expose
            var audioSubCategory: String? = null
        }
    }
}