package com.brainwellnessspa.DashboardTwoModule.Model

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
        var iD: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("IsLock")
        @Expose
        var isLock: String? = null

        @SerializedName("IsPlay")
        @Expose
        var isPlay: String? = null

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = null

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = null

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = null

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = null

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = null

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = null

        @SerializedName("Like")
        @Expose
        var like: String? = null

        @SerializedName("Download")
        @Expose
        var download: String? = null

        @SerializedName("Bitrate")
        @Expose
        var bitrate: String? = null

        constructor() {}
        constructor(`in`: Parcel) {
            iD = `in`.readString()
            name = `in`.readString()
            isLock = `in`.readString()
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
            dest.writeString(isLock)
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