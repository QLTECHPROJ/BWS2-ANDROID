package com.brainwellnessspa.dashboardOldModule.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

open class AppointmentDetailModel protected constructor(`in`: Parcel) : Parcelable {
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
        @SerializedName("Id")
        @Expose
        var id: String? = `in`.readString()

        @SerializedName("Name")
        @Expose
        var name: String? = `in`.readString()

        @SerializedName("Desc")
        @Expose
        var desc: String? = `in`.readString()

        @SerializedName("Facilitator")
        @Expose
        var facilitator: String? = `in`.readString()

        @SerializedName("UserName")
        @Expose
        var userName: String? = `in`.readString()

        @SerializedName("Image")
        @Expose
        var image: String? = `in`.readString()

        @SerializedName("Date")
        @Expose
        var date: String? = `in`.readString()

        @SerializedName("Duration")
        @Expose
        var duration: String? = `in`.readString()

        @SerializedName("Time")
        @Expose
        var time: String? = `in`.readString()

        @SerializedName("BookUrl")
        @Expose
        var bookUrl: String? = `in`.readString()

        @SerializedName("Status")
        @Expose
        var status: String? = `in`.readString()

        @SerializedName("Audio")
        @Expose
        var audio: ArrayList<Audio?>? = `in`.createTypedArrayList(Audio.CREATOR)

        @SerializedName("Booklet")
        @Expose
        var booklet: String? = `in`.readString()

        @SerializedName("MyAnswers")
        @Expose
        var myAnswers: String? = `in`.readString()
        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(id)
            parcel.writeString(name)
            parcel.writeString(desc)
            parcel.writeString(facilitator)
            parcel.writeString(userName)
            parcel.writeString(image)
            parcel.writeString(date)
            parcel.writeString(duration)
            parcel.writeString(time)
            parcel.writeString(bookUrl)
            parcel.writeString(status)
            parcel.writeTypedList(audio)
            parcel.writeString(booklet)
            parcel.writeString(myAnswers)
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

    open class Audio : Parcelable {
        @SerializedName("ID")
        @Expose
        var iD: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("AudioFile")
        @Expose
        var audioFile: String? = null

        @SerializedName("AudioDescription")
        @Expose
        var audioDescription: String? = null

        @SerializedName("ImageFile")
        @Expose
        var imageFile: String? = null

        @SerializedName("IsLock")
        @Expose
        var isLock: String? = null

        @SerializedName("IsPlay")
        @Expose
        var isPlay: String? = null

        @SerializedName("AudioDuration")
        @Expose
        var audioDuration: String? = null

        @SerializedName("Audiomastercat")
        @Expose
        var audiomastercat: String? = null

        @SerializedName("AudioSubCategory")
        @Expose
        var audioSubCategory: String? = null

        @SerializedName("AudioDirection")
        @Expose
        var audioDirection: String? = null

        @SerializedName("Like")
        @Expose
        var like: String? = null

        @SerializedName("Download")
        @Expose
        var download: String? = null

        constructor()
        protected constructor(`in`: Parcel) {
            iD = `in`.readString()
            name = `in`.readString()
            audioFile = `in`.readString()
            imageFile = `in`.readString()
            audioDuration = `in`.readString()
            audiomastercat = `in`.readString()
            audioSubCategory = `in`.readString()
            audioDirection = `in`.readString()
            like = `in`.readString()
            download = `in`.readString()
            audioDescription = `in`.readString()
            isLock = `in`.readString()
            isPlay = `in`.readString()
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
            parcel.writeString(audiomastercat)
            parcel.writeString(audioSubCategory)
            parcel.writeString(audioDirection)
            parcel.writeString(like)
            parcel.writeString(download)
            parcel.writeString(audioDescription)
            parcel.writeString(isLock)
            parcel.writeString(isPlay)
        }

        companion object {
            @JvmField val CREATOR: Creator<Audio?> = object : Creator<Audio?> {
                override fun createFromParcel(`in`: Parcel): Audio {
                    return Audio(`in`)
                }

                override fun newArray(size: Int): Array<Audio?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        @JvmField val CREATOR: Creator<AppointmentDetailModel?> = object : Creator<AppointmentDetailModel?> {
            override fun createFromParcel(`in`: Parcel): AppointmentDetailModel {
                return AppointmentDetailModel(`in`)
            }

            override fun newArray(size: Int): Array<AppointmentDetailModel?> {
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