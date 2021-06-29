package com.brainwellnessspa.membershipModule.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

open class MembershipPlanListModel protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData?

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String?

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String?

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String?
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeParcelable(responseData, i)
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeString(responseStatus)
    }

    open class ResponseData protected constructor(`in`: Parcel) : Parcelable {
        @SerializedName("Image")
        @Expose
        var image: String? = `in`.readString()

        @SerializedName("Title")
        @Expose
        var title: String? = `in`.readString()

        @SerializedName("Desc")
        @Expose
        var desc: String? = `in`.readString()

        @SerializedName("TrialPeriod")
        @Expose
        var trialPeriod: String? = `in`.readString()

        @SerializedName("Plan")
        @Expose
        var plan: ArrayList<Plan>? = null

        @SerializedName("AudioFiles")
        @Expose
        var audioFiles: ArrayList<AudioFile>? = null

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(image)
            parcel.writeString(title)
            parcel.writeString(desc)
            parcel.writeString(trialPeriod)
        }

        companion object {
            @JvmField val CREATOR: Creator<ResponseData?> = object : Creator<ResponseData?> {
                override fun createFromParcel(`in`: Parcel): ResponseData {
                    return ResponseData(`in`)
                }

                override fun newArray(size: Int): Array<ResponseData?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }

    open class Plan protected constructor(`in`: Parcel) : Parcelable {
        @SerializedName("PlanPosition")
        @Expose
        var planPosition: String? = `in`.readString()

        @SerializedName("PlanID")
        @Expose
        var planID: String? = `in`.readString()

        @SerializedName("PlanAmount")
        @Expose
        var planAmount: String? = `in`.readString()

        @SerializedName("PlanCurrency")
        @Expose
        var planCurrency: String? = `in`.readString()

        @SerializedName("PlanInterval")
        @Expose
        var planInterval: String? = `in`.readString()

        @SerializedName("PlanImage")
        @Expose
        var planImage: String? = `in`.readString()

        @SerializedName("PlanTenure")
        @Expose
        var planTenure: String? = `in`.readString()

        @SerializedName("PlanNextRenewal")
        @Expose
        var planNextRenewal: String? = `in`.readString()

        @SerializedName("SubName")
        @Expose
        var subName: String? = `in`.readString()

        @SerializedName("PlanFeatures")
        @Expose
        var planFeatures: List<PlanFeatures>? = null

        @SerializedName("RecommendedFlag")
        @Expose
        var recommendedFlag: String? = `in`.readString()

        @SerializedName("PlanFlag")
        @Expose
        var planFlag: String? = `in`.readString()

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(planPosition)
            parcel.writeString(planID)
            parcel.writeString(planAmount)
            parcel.writeString(planCurrency)
            parcel.writeString(planInterval)
            parcel.writeString(planImage)
            parcel.writeString(planTenure)
            parcel.writeString(planNextRenewal)
            parcel.writeString(subName)
            parcel.writeString(recommendedFlag)
            parcel.writeString(planFlag)
        }

        companion object {
            @JvmField val CREATOR: Creator<Plan?> = object : Creator<Plan?> {
                override fun createFromParcel(`in`: Parcel): Plan {
                    return Plan(`in`)
                }

                override fun newArray(size: Int): Array<Plan?> {
                    return arrayOfNulls(size)
                }
            }
        }

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

    companion object {
        @JvmField val CREATOR: Creator<MembershipPlanListModel?> = object : Creator<MembershipPlanListModel?> {
            override fun createFromParcel(`in`: Parcel): MembershipPlanListModel {
                return MembershipPlanListModel(`in`)
            }

            override fun newArray(size: Int): Array<MembershipPlanListModel?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        responseData = `in`.readParcelable(ResponseData::class.java.classLoader)
        responseCode = `in`.readString()
        responseMessage = `in`.readString()
        responseStatus = `in`.readString()
    }
}