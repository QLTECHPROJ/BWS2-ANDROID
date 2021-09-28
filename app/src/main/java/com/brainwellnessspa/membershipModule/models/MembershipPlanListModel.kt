package com.brainwellnessspa.membershipModule.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

open class MembershipPlanListModel {
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

    open class ResponseData {
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

        @SerializedName("Plan")
        @Expose
        var plan: ArrayList<Plan>? = null

        @SerializedName("AudioFiles")
        @Expose
        var audioFiles: ArrayList<AudioFile>? = null
    }

    open class Plan{
        @SerializedName("PlanPosition")
        @Expose
        var planPosition: String? = ""

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

        @SerializedName("SubName")
        @Expose
        var subName: String? = ""

        @SerializedName("PlanFeatures")
        @Expose
        var planFeatures: List<PlanFeatures>? = null

        @SerializedName("RecommendedFlag")
        @Expose
        var recommendedFlag: String? = ""

        @SerializedName("PlanFlag")
        @Expose
        var planFlag: String? = ""
    }

    inner class PlanFeatures {
        @SerializedName("Feature")
        @Expose
        var feature: String? = null
    }

    inner class AudioFile {
        @SerializedName("ID")
        @Expose
        var iD: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = null
    }
 
}