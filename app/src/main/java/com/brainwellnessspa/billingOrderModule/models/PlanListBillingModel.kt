package com.brainwellnessspa.billingOrderModule.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class PlanListBillingModel() : Parcelable{
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

    constructor(parcel: Parcel) : this() {
        responseCode = parcel.readString()
        responseMessage = parcel.readString()
        responseStatus = parcel.readString()
    }

    class ResponseData() :  Parcelable{
        @SerializedName("Image")
        @Expose
        var image: String? = null

        @SerializedName("Title")
        @Expose
        var title: String? = null

        @SerializedName("Desc")
        @Expose
        var desc: String? = null

        @SerializedName("Plan")
        @Expose
        var plan: ArrayList<Plan>? = null

        constructor(parcel: Parcel) : this() {
            image = parcel.readString()
            title = parcel.readString()
            desc = parcel.readString()
        }


        class Plan() : Parcelable{
            @SerializedName("PlanPosition")
            @Expose
            var planPosition: String? = null

            @SerializedName("PlanID")
            @Expose
            var planID: String? = null

            @SerializedName("PlanAmount")
            @Expose
            var planAmount = null

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

            @SerializedName("SubName")
            @Expose
            var subName: String? = null

            @SerializedName("RecommendedFlag")
            @Expose
            var recommendedFlag: String? = null

            @SerializedName("PlanFlag")
            @Expose
            var planFlag: String? = null

            @SerializedName("PlanFeatures")
            @Expose
            var planFeatures: List<PlanFeature>? = null

            constructor(parcel: Parcel) : this() {
                planPosition = parcel.readString()
                planID = parcel.readString()
                planCurrency = parcel.readString()
                planInterval = parcel.readString()
                planImage = parcel.readString()
                planTenure = parcel.readString()
                planNextRenewal = parcel.readString()
                subName = parcel.readString()
                recommendedFlag = parcel.readString()
                planFlag = parcel.readString()
            }

            class PlanFeature() : Parcelable{
                @SerializedName("Feature")
                @Expose
                var feature: String? = null

                constructor(parcel: Parcel) : this() {
                    feature = parcel.readString()
                }

                override fun writeToParcel(parcel: Parcel, flags: Int) {
                    parcel.writeString(feature)
                }

                override fun describeContents(): Int {
                    return 0
                }

                companion object CREATOR : Parcelable.Creator<PlanFeature> {
                    override fun createFromParcel(parcel: Parcel): PlanFeature {
                        return PlanFeature(parcel)
                    }

                    override fun newArray(size: Int): Array<PlanFeature?> {
                        return arrayOfNulls(size)
                    }
                }
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(planPosition)
                parcel.writeString(planID)
                parcel.writeString(planCurrency)
                parcel.writeString(planInterval)
                parcel.writeString(planImage)
                parcel.writeString(planTenure)
                parcel.writeString(planNextRenewal)
                parcel.writeString(subName)
                parcel.writeString(recommendedFlag)
                parcel.writeString(planFlag)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<Plan> {
                override fun createFromParcel(parcel: Parcel): Plan {
                    return Plan(parcel)
                }

                override fun newArray(size: Int): Array<Plan?> {
                    return arrayOfNulls(size)
                }
            }

        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(image)
            parcel.writeString(title)
            parcel.writeString(desc)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ResponseData> {
            override fun createFromParcel(parcel: Parcel): ResponseData {
                return ResponseData(parcel)
            }

            override fun newArray(size: Int): Array<ResponseData?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeString(responseStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlanListBillingModel> {
        override fun createFromParcel(parcel: Parcel): PlanListBillingModel {
            return PlanListBillingModel(parcel)
        }

        override fun newArray(size: Int): Array<PlanListBillingModel?> {
            return arrayOfNulls(size)
        }
    }
}