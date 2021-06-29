package com.brainwellnessspa.dashboardModule.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

open class MainAudioModel protected constructor(`in`: Parcel) : Parcelable {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ArrayList<ResponseData?>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null

    init {
        responseData = `in`.createTypedArrayList(ResponseData.CREATOR)
        responseCode = `in`.readString()
        responseMessage = `in`.readString()
        responseStatus = `in`.readString()
    }

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
        @SerializedName("HomeID")
        @Expose
        var homeID: String? = null

        @SerializedName("View")
        @Expose
        var view: String? = null

        @SerializedName("Type")
        @Expose
        var type: String? = null

        @SerializedName("UserID")
        @Expose
        var userID: String? = null

        @SerializedName("IsLock")
        @Expose
        var isLock: String? = null

        @SerializedName("expireDate")
        @Expose
        var expireDate: String? = null

        @SerializedName("Details")
        @Expose
        var details: ArrayList<Detail>? = null

        constructor()
        constructor(`in`: Parcel) {
            homeID = `in`.readString()
            view = `in`.readString()
            type = `in`.readString()
            userID = `in`.readString()
            expireDate = `in`.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(homeID)
            parcel.writeString(view)
            parcel.writeString(type)
            parcel.writeString(userID)
            parcel.writeString(expireDate)
        }

        class Detail : Parcelable {
            @SerializedName("ID")
            @Expose
            var iD: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null

            @SerializedName("CategoryName")
            @Expose
            var categoryName: String? = null

            @SerializedName("CatImage")
            @Expose
            var catImage: String? = null

            @SerializedName("IsPlay")
            @Expose
            var isPlay: String? = null

            @SerializedName("AudioFile")
            @Expose
            var audioFile: String? = null

            @SerializedName("AudioDirection")
            @Expose
            var audioDirection: String? = null

            @SerializedName("Audiomastercat")
            @Expose
            var audiomastercat: String? = null

            @SerializedName("AudioSubCategory")
            @Expose
            var audioSubCategory: String? = null

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = null

            @SerializedName("Like")
            @Expose
            var like: String? = null

            @SerializedName("Download")
            @Expose
            var download: String? = null

            @SerializedName("AudioDuration")
            @Expose
            var audioDuration: String? = null

            @SerializedName("Bitrate")
            @Expose
            var bitrate: String? = null

            constructor()

            constructor(`in`: Parcel) {
                iD = `in`.readString()
                name = `in`.readString()
                categoryName = `in`.readString()
                catImage = `in`.readString()
                isPlay = `in`.readString()
                audioFile = `in`.readString()
                audioDirection = `in`.readString()
                audiomastercat = `in`.readString()
                audioSubCategory = `in`.readString()
                imageFile = `in`.readString()
                like = `in`.readString()
                download = `in`.readString()
                audioDuration = `in`.readString()
                bitrate = `in`.readString()
            }

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(parcel: Parcel, i: Int) {
                parcel.writeString(iD)
                parcel.writeString(name)
                parcel.writeString(categoryName)
                parcel.writeString(catImage)
                parcel.writeString(isPlay)
                parcel.writeString(audioFile)
                parcel.writeString(audioDirection)
                parcel.writeString(audiomastercat)
                parcel.writeString(audioSubCategory)
                parcel.writeString(imageFile)
                parcel.writeString(like)
                parcel.writeString(download)
                parcel.writeString(audioDuration)
                parcel.writeString(bitrate)
            }

            companion object {
                @JvmField val CREATOR: Creator<Detail?> = object : Creator<Detail?> {
                    override fun createFromParcel(`in`: Parcel): Detail {
                        return Detail(`in`)
                    }

                    override fun newArray(size: Int): Array<Detail?> {
                        return arrayOfNulls(size)
                    }
                }
            }
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

    companion object {
        @JvmField val CREATOR: Creator<MainAudioModel?> = object : Creator<MainAudioModel?> {
            override fun createFromParcel(`in`: Parcel): MainAudioModel {
                return MainAudioModel(`in`)
            }

            override fun newArray(size: Int): Array<MainAudioModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}