package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class PlanlistInappModel{
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
        @SerializedName("Image")
        @Expose
        var image: String? = ""

        @SerializedName("Title")
        @Expose
        var title: String? = ""

        @SerializedName("Desc")
        @Expose
        var desc: String? = ""

        @SerializedName("TrialPeriod")
        @Expose
        var trialPeriod: String? = ""

        @SerializedName("PlanFeatures")
        @Expose
        var planFeatures: List<PlanFeature>? = null

        @SerializedName("Plan")
        @Expose
        var plan: List<Plan>? = null

        @SerializedName("AudioFiles")
        @Expose
        var audioFiles: List<AudioFile>? = null

        @SerializedName("IntroductorySession")
        @Expose
        var introductorySession: List<Any>? = null

        @SerializedName("TestminialVideo")
        @Expose
        var testminialVideo: List<TestminialVideo>? = null

        @SerializedName("FAQs")
        @Expose
        var fAQs: List<Faq>? = null

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

        class PlanFeature {
            @SerializedName("Feature")
            @Expose
            var feature: String? = ""
        }

        class Plan {


            @SerializedName("PlanPosition")
            @Expose
            var planPosition: String? = ""

            @SerializedName("ProfileCount")
            @Expose
            var profileCount: String? = ""

            @SerializedName("PlanID")
            @Expose
            var planID: String? = ""

            @SerializedName("PlanAmount")
            @Expose
            var planAmount: String? = ""

            @SerializedName("PlanCurrency")
            @Expose
            var planCurrency: String? = ""

            @SerializedName("PlanInterval")
            @Expose
            var planInterval: String? = ""

            @SerializedName("PlanImage")
            @Expose
            var planImage: String? = ""

            @SerializedName("PlanTenure")
            @Expose
            var planTenure: String? = ""

            @SerializedName("PlanNextRenewal")
            @Expose
            var planNextRenewal: String? = ""

            @SerializedName("FreeTrial")
            @Expose
            var freeTrial: String? = ""

            @SerializedName("IOSplanId")
            @Expose
            var iOSplanId: String? = ""

            @SerializedName("AndroidplanId")
            @Expose
            var androidplanId: String? = ""

            @SerializedName("SubName")
            @Expose
            var subName: String? = ""

            @SerializedName("RecommendedFlag")
            @Expose
            var recommendedFlag: String? = ""

            @SerializedName("PlanFlag")
            @Expose
            var planFlag: String? = ""
        }
    }
}