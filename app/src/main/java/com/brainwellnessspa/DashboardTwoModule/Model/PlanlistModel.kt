package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlanlistModel {
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
        @SerializedName("Plan")
        @Expose
        var plan: List<Plan>? = null

        @SerializedName("AudioFiles")
        @Expose
        var audioFiles: List<AudioFile>? = null

        @SerializedName("IntroductorySession")
        @Expose
        var introductorySession: List<IntroductorySession>? = null

        @SerializedName("TestminialVideo")
        @Expose
        var testminialVideo: List<TestminialVideo>? = null

        @SerializedName("FAQs")
        @Expose
        var fAQs: List<Faq>? = null

        class Plan {

        }

        class IntroductorySession {

        }

        class TestminialVideo {

            @SerializedName("UserName")
            @Expose
            var userName: String? = null

            @SerializedName("VideoLink")
            @Expose
            var videoLink: String? = null

            @SerializedName("VideoDesc")
            @Expose
            var videoDesc: String? = null
        }

        class Faq {
            @SerializedName("ID")
            @Expose
            var id: String? = null

            @SerializedName("Title")
            @Expose
            var title: String? = null

            @SerializedName("Desc")
            @Expose
            var desc: String? = null

            @SerializedName("VideoURL")
            @Expose
            var videoURL: String? = null

            @SerializedName("Category")
            @Expose
            var category: String? = null
        }

        class AudioFile {
            @SerializedName("ID")
            @Expose
            var id: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = null
        }
    }
}