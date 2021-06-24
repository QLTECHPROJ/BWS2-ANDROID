package com.brainwellnessspa.faqModule.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

open class FaqListModel protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ArrayList<ResponseData>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = `in`.readString()

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = `in`.readString()

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = `in`.readString()
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeString(responseStatus)
    }

    class ResponseData protected constructor(`in`: Parcel) : Parcelable {
        @SerializedName("ID")
        @Expose
        var iD: String? = `in`.readString()

        @SerializedName("Title")
        @Expose
        var title: String? = `in`.readString()

        @SerializedName("Desc")
        @Expose
        var desc: String? = `in`.readString()

        @SerializedName("VideoURL")
        @Expose
        var videoURL: String? = `in`.readString()

        @SerializedName("Category")
        @Expose
        var category: String? = `in`.readString()
        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(iD)
            parcel.writeString(title)
            parcel.writeString(desc)
            parcel.writeString(videoURL)
            parcel.writeString(this.category)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<ResponseData?> =
                object : Parcelable.Creator<ResponseData?> {
                    override fun createFromParcel(`in`: Parcel): ResponseData? {
                        return ResponseData(`in`)
                    }

                    override fun newArray(size: Int): Array<ResponseData?> {
                        return arrayOfNulls(size)
                    }
                }
        }

    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FaqListModel?> =
            object : Parcelable.Creator<FaqListModel?> {
                override fun createFromParcel(`in`: Parcel): FaqListModel? {
                    return FaqListModel(`in`)
                }

                override fun newArray(size: Int): Array<FaqListModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}