package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SessionListModel {
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
        @SerializedName("completedSession")
        @Expose
        var completedSession: String? = null

        @SerializedName("completion_percentage")
        @Expose
        var completionPercentage: String? = null

        @SerializedName("totalSession")
        @Expose
        var totalSession: String? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

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

            @SerializedName("session_date")
            @Expose
            var sessionDate: String? = null

            @SerializedName("session_time")
            @Expose
            var sessionTime: String? = null

            @SerializedName("user_session_status")
            @Expose
            var userSessionStatus: String? = null

            @SerializedName("status_img")
            @Expose
            var statusImg: String? = null

            @SerializedName("pre_session_audio_title")
            @Expose
            var preSessionAudioTitle: String? = null

            @SerializedName("pre_session_audio_status")
            @Expose
            var preSessionAudioStatus: String? = null

            @SerializedName("booklet_title")
            @Expose
            var bookletTitle: String? = null

            @SerializedName("booklet_status")
            @Expose
            var bookletStatus: String? = null

            @SerializedName("before_session")
            @Expose
            val beforeSession: List<BeforeSession>? = null

            @SerializedName("after_session")
            @Expose
            val afterSession: List<AfterSession>? = null

            class AfterSession {
                @SerializedName("key")
                @Expose
                var key: String? = null

                @SerializedName("color")
                @Expose
                var color: String? = null
            }

            class BeforeSession {
                @SerializedName("key")
                @Expose
                var key: String? = null

                @SerializedName("color")
                @Expose
                var color: String? = null
            }
        }
    }
}