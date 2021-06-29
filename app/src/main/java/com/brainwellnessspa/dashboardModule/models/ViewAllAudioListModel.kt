package com.brainwellnessspa.dashboardModule.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ViewAllAudioListModel {
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

    class ResponseData() : Parcelable {
        @SerializedName("HomeAudioID")
        @Expose
        var homeAudioID: String? = null

        @SerializedName("View")
        @Expose
        var view: String? = null

        @SerializedName("UserId")
        @Expose
        var coUserId: String? = null

        @SerializedName("Details")
        @Expose
        var details: ArrayList<Detail>? = null

        constructor(parcel: Parcel) : this() {
            homeAudioID = parcel.readString()
            view = parcel.readString()
            coUserId = parcel.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(homeAudioID)
            parcel.writeString(view)
            parcel.writeString(coUserId)
        }

        open class Detail : Parcelable {
            @SerializedName("ID")
            @Expose
            var iD: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null

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

            @SerializedName("IsPlay")
            @Expose
            var isPlay: String? = null

            @SerializedName("Bitrate")
            @Expose
            var bitrate: String? = null

            constructor()
            protected constructor(`in`: Parcel) {
                iD = `in`.readString()
                name = `in`.readString()
                audioFile = `in`.readString()
                imageFile = `in`.readString()
                audioDuration = `in`.readString()
                audioDirection = `in`.readString()
                isPlay = `in`.readString()
                audiomastercat = `in`.readString()
                audioSubCategory = `in`.readString()
                bitrate = `in`.readString()
            }

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(parcel: Parcel, i: Int) {
                parcel.writeString(iD)
                parcel.writeString(name)
                parcel.writeString(audioFile)
                parcel.writeString(imageFile)
                parcel.writeString(audioDuration)
                parcel.writeString(audioDirection)
                parcel.writeString(isPlay)
                parcel.writeString(audiomastercat)
                parcel.writeString(audioSubCategory)
                parcel.writeString(bitrate)
            }

            companion object {
                @JvmField val CREATOR: Creator<MainAudioModel.ResponseData.Detail?> = object : Creator<MainAudioModel.ResponseData.Detail?> {
                    override fun createFromParcel(`in`: Parcel): MainAudioModel.ResponseData.Detail {
                        return MainAudioModel.ResponseData.Detail(`in`)
                    }

                    override fun newArray(size: Int): Array<MainAudioModel.ResponseData.Detail?> {
                        return arrayOfNulls(size)
                    }
                }
            }
        }

        companion object {
            @JvmField val CREATOR: Creator<MainAudioModel.ResponseData?> = object : Creator<MainAudioModel.ResponseData?> {
                override fun createFromParcel(`in`: Parcel): MainAudioModel.ResponseData {
                    return MainAudioModel.ResponseData(`in`)
                }

                override fun newArray(size: Int): Array<MainAudioModel.ResponseData?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}