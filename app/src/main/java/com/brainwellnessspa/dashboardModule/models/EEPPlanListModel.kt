package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EEPPlanListModel {

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
        @SerializedName("Image")
        @Expose
        var image: String? = null

        @SerializedName("Title")
        @Expose
        var title: String? = null

        @SerializedName("Desc")
        @Expose
        var desc: String? = null

        @SerializedName("PlanFeatures")
        @Expose
        var planFeatures: List<PlanFeature>? = null

        @SerializedName("Plan")
        @Expose
        var plan: List<Plan>? = null

        @SerializedName("AudioFiles")
        @Expose
        var audioFiles: List<Any>? = null

        @SerializedName("IntroductorySession")
        @Expose
        var introductorySession: List<Any>? = null

        @SerializedName("TestminialVideo")
        @Expose
        var testminialVideo: List<TestminialVideo>? = null

        @SerializedName("FAQs")
        @Expose
        var fAQs: List<Faq>? = null

        class PlanFeature {
            @SerializedName("Feature")
            @Expose
            var feature: String? = null
        }

        class Plan {
            @SerializedName("PlanPosition")
            @Expose
            var planPosition: String? = null

            @SerializedName("ProfileCount")
            @Expose
            var profileCount: String? = null

            @SerializedName("PlanID")
            @Expose
            var planID: String? = null

            @SerializedName("PlanAmount")
            @Expose
            var planAmount: String? = null

            @SerializedName("PlanCurrency")
            @Expose
            var planCurrency: String? = null

            @SerializedName("PlanInterval")
            @Expose
            var planInterval: String? = null

            @SerializedName("PlanImage")
            @Expose
            var planImage: String? = null

            @SerializedName("PlanTenure")
            @Expose
            var planTenure: String? = null

            @SerializedName("PlanNextRenewal")
            @Expose
            var planNextRenewal: String? = null

            @SerializedName("FreeTrial")
            @Expose
            var freeTrial: String? = null

            @SerializedName("IOSplanId")
            @Expose
            var iOSplanId: String? = null

            @SerializedName("StripePlanId")
            @Expose
            var stripePlanId: String? = null

            @SerializedName("SubName")
            @Expose
            var subName: String? = null

            @SerializedName("RecommendedFlag")
            @Expose
            var recommendedFlag: String? = null

            @SerializedName("PlanFlag")
            @Expose
            var planFlag: String? = null
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
    }
}