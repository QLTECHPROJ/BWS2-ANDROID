package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SessionStepListModel {
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
        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        @SerializedName("session_img")
        @Expose
        var sessionImg: String? = null

        @SerializedName("session_title")
        @Expose
        var sessionTitle: String? = null

        @SerializedName("session_short_desc")
        @Expose
        var sessionShortDesc: String? = null

        @SerializedName("session_desc")
        @Expose
        var sessionDesc: String? = null

        @SerializedName("session_status")
        @Expose
        var sessionStatus: String? = null

        @SerializedName("session_progress")
        @Expose
        var sessionProgress: String? = null

        @SerializedName("session_progress_color")
        @Expose
        var sessionProgressColor: String? = null

        @SerializedName("session_progress_img")
        @Expose
        var sessionProgressImg: String? = null

        @SerializedName("session_progress_text")
        @Expose
        var sessionProgressText: String? = null

        @SerializedName("shouldFillProfileFormTwo")
        @Expose
        var shouldFillProfileFormTwo: String? = null

        @SerializedName("shouldFillProfileFormThree")
        @Expose
        var shouldFillProfileFormThree: String? = null

        @SerializedName("data")
        @Expose
        var data: List<Data>? = null

        class Data {
            @SerializedName("title")
            @Expose
            var title: String? = null

            @SerializedName("desc")
            @Expose
            var desc: String? = null

            @SerializedName("status")
            @Expose
            var status: String? = null

            @SerializedName("user_id")
            @Expose
            var userId: String? = null

            @SerializedName("session_id")
            @Expose
            var sessionId: String? = null

            @SerializedName("step_id")
            @Expose
            var stepId: String? = null

            @SerializedName("status_img")
            @Expose
            var statusImg: String? = null

            @SerializedName("user_step_status")
            @Expose
            var userStepStatus: String? = null
        }
    }
}