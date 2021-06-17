package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlanlistModel {
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
            var userName: String? = ""

            @SerializedName("VideoLink")
            @Expose
            var videoLink: String? = ""

            @SerializedName("VideoDesc")
            @Expose
            var videoDesc: String? = ""
        }

        class Faq {
            @SerializedName("ID")
            @Expose
            var id: String? = ""

            @SerializedName("Title")
            @Expose
            var title: String? = ""

            @SerializedName("Desc")
            @Expose
            var desc: String? = ""

            @SerializedName("VideoURL")
            @Expose
            var videoURL: String? = ""

            @SerializedName("Category")
            @Expose
            var category: String? = ""
        }

        class AudioFile {
            @SerializedName("ID")
            @Expose
            var id: String? = ""

            @SerializedName("Name")
            @Expose
            var name: String? = ""

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = ""
        }
    }
}