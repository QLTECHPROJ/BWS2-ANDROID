package com.brainwellnessspa.dashboardModule.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class SubPlayListModel {
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

    open class ResponseData : Parcelable {
        @SerializedName("PlaylistID")
        @Expose
        var playlistID: String? = null

        @SerializedName("PlaylistName")
        @Expose
        var playlistName: String? = null

        @SerializedName("PlaylistDesc")
        @Expose
        var playlistDesc: String? = null

        @SerializedName("ReminderTime")
        @Expose
        var reminderTime: String? = null

        @SerializedName("ReminderDay")
        @Expose
        var reminderDay: String? = null

        @SerializedName("IsReminder")
        @Expose
        var isReminder: String? = null

        @SerializedName("PlaylistMastercat")
        @Expose
        var playlistMastercat: String? = null

        @SerializedName("PlaylistSubcat")
        @Expose
        var playlistSubcat: String? = null

        @SerializedName("PlaylistImage")
        @Expose
        var playlistImage: String? = null

        @SerializedName("PlaylistImageDetail")
        @Expose
        var playlistImageDetail: String? = null

        @SerializedName("TotalAudio")
        @Expose
        var totalAudio: String? = null

        @SerializedName("TotalDuration")
        @Expose
        var totalDuration: String? = null

        @SerializedName("Totalhour")
        @Expose
        var totalhour: String? = null

        @SerializedName("Totalminute")
        @Expose
        var totalminute: String? = null

        @SerializedName("Created")
        @Expose
        var created: String? = null

        @SerializedName("Download")
        @Expose
        var download: String? = null

        @SerializedName("Like")
        @Expose
        var like: String? = null

        @SerializedName("PlaylistSongs")
        @Expose
        var playlistSongs: ArrayList<PlaylistSong>? = null

        constructor()
        protected constructor(`in`: Parcel) {
            playlistID = `in`.readString()
            playlistName = `in`.readString()
            playlistDesc = `in`.readString()
            reminderDay = `in`.readString()
            reminderTime = `in`.readString()
            isReminder = `in`.readString()
            playlistMastercat = `in`.readString()
            playlistSubcat = `in`.readString()
            playlistImage = `in`.readString()
            playlistImageDetail = `in`.readString()
            totalAudio = `in`.readString()
            totalDuration = `in`.readString()
            totalhour = `in`.readString()
            totalminute = `in`.readString()
            created = `in`.readString()
            download = `in`.readString()
            like = `in`.readString()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(playlistID)
            parcel.writeString(playlistName)
            parcel.writeString(playlistDesc)
            parcel.writeString(reminderDay)
            parcel.writeString(reminderTime)
            parcel.writeString(isReminder)
            parcel.writeString(playlistMastercat)
            parcel.writeString(playlistSubcat)
            parcel.writeString(playlistImage)
            parcel.writeString(playlistImageDetail)
            parcel.writeString(totalAudio)
            parcel.writeString(totalDuration)
            parcel.writeString(totalhour)
            parcel.writeString(totalminute)
            parcel.writeString(created)
            parcel.writeString(download)
            parcel.writeString(like)
        }

        open class PlaylistSong : Parcelable {
            @SerializedName("ID")
            @Expose
            var iD: String? = null

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = null

            @SerializedName("PatientID")
            @Expose
            var patientID: String? = null

            @SerializedName("AudioDuration")
            @Expose
            var audioDuration: String? = null

            @SerializedName("PlaylistID")
            @Expose
            var playlistID: String? = null

            @SerializedName("AudioFile")
            @Expose
            var audioFile: String? = null

            @SerializedName("PlaylistAudioId")
            @Expose
            var playlistAudioId: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null
            var isDownload = false

            @SerializedName("Download")
            @Expose
            private var download: String? = null

            @SerializedName("Like")
            @Expose
            var like: String? = null

            @SerializedName("Audiomastercat")
            @Expose
            var audiomastercat: String? = null

            @SerializedName("AudioSubCategory")
            @Expose
            var audioSubCategory: String? = null

            @SerializedName("AudioDirection")
            @Expose
            var audioDirection: String? = null

            @SerializedName("Bitrate")
            @Expose
            var bitrate: String? = null

            protected constructor(`in`: Parcel) {
                iD = `in`.readString()
                imageFile = `in`.readString()
                patientID = `in`.readString()
                audioDuration = `in`.readString()
                playlistID = `in`.readString()
                audioFile = `in`.readString()
                playlistAudioId = `in`.readString()
                name = `in`.readString()
                isDownload = `in`.readByte().toInt() != 0
                download = `in`.readString()
                like = `in`.readString()
                audiomastercat = `in`.readString()
                audioSubCategory = `in`.readString()
                audioDirection = `in`.readString()
                bitrate = `in`.readString()
            }

            constructor()

            fun getDownload(): String? {
                return download
            }

            fun setDownload(download: String?) {
                this.download = download
            }

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(parcel: Parcel, i: Int) {
                parcel.writeString(iD)
                parcel.writeString(imageFile)
                parcel.writeString(patientID)
                parcel.writeString(audioDuration)
                parcel.writeString(playlistID)
                parcel.writeString(audioFile)
                parcel.writeString(playlistAudioId)
                parcel.writeString(name)
                parcel.writeByte((if (isDownload) 1 else 0).toByte())
                parcel.writeString(download)
                parcel.writeString(like)
                parcel.writeString(audiomastercat)
                parcel.writeString(audioSubCategory)
                parcel.writeString(audioDirection)
                parcel.writeString(bitrate)
            }

            companion object {
                @JvmField val CREATOR: Creator<PlaylistSong?> = object : Creator<PlaylistSong?> {
                    override fun createFromParcel(`in`: Parcel): PlaylistSong {
                        return PlaylistSong(`in`)
                    }

                    override fun newArray(size: Int): Array<PlaylistSong?> {
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
}