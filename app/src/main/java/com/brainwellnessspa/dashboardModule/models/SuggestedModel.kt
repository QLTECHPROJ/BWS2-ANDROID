package com.brainwellnessspa.dashboardModule.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class SuggestedModel protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ArrayList<ResponseData?>? = null

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
        parcel.writeTypedList(responseData)
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeString(responseStatus)
    }

    class ResponseData : Parcelable {
        @SerializedName("ID")
        @Expose
        var iD: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("IsPlay")
        @Expose
        var isPlay: String? = ""

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = ""

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = ""

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = ""

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = ""

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = ""

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = ""

        @SerializedName("Like")
        @Expose
        var like: String? = ""

        @SerializedName("Download")
        @Expose
        var download: String? = ""

        @SerializedName("Bitrate")
        @Expose
        var bitrate: String? = ""

        constructor() {}
        constructor(`in`: Parcel) {
            iD = `in`.readString()
            name = `in`.readString()
            isPlay = `in`.readString()
            imageFile = `in`.readString()
            audioFile = `in`.readString()
            audioDuration = `in`.readString()
            audioDirection = `in`.readString()
            audiomastercat = `in`.readString()
            audioSubCategory = `in`.readString()
            like = `in`.readString()
            download = `in`.readString()
            bitrate = `in`.readString()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(iD)
            dest.writeString(name)
            dest.writeString(isPlay)
            dest.writeString(imageFile)
            dest.writeString(audioFile)
            dest.writeString(audioDuration)
            dest.writeString(audioDirection)
            dest.writeString(audiomastercat)
            dest.writeString(audioSubCategory)
            dest.writeString(like)
            dest.writeString(download)
            dest.writeString(bitrate)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object {
            @JvmField val CREATOR: Parcelable.Creator<ResponseData?> = object : Parcelable.Creator<ResponseData?> {
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
        @JvmField val CREATOR: Parcelable.Creator<SuggestedModel> = object : Parcelable.Creator<SuggestedModel> {
            override fun createFromParcel(`in`: Parcel): SuggestedModel? {
                return SuggestedModel(`in`)
            }

            override fun newArray(size: Int): Array<SuggestedModel?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        responseData = `in`.createTypedArrayList(ResponseData.CREATOR)
        responseCode = `in`.readString()
        responseMessage = `in`.readString()
        responseStatus = `in`.readString()
    }
}