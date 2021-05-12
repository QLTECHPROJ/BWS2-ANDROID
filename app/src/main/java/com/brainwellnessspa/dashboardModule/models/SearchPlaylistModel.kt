package com.brainwellnessspa.dashboardModule.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class SearchPlaylistModel protected constructor(`in`: Parcel) : Parcelable {
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

    class ResponseData(`in`: Parcel) : Parcelable {
        @SerializedName("PlaylistID")
        @Expose
        var iD: String?

        @SerializedName("PlaylistName")
        @Expose
        var name: String?

        @SerializedName("PlaylistDesc")
        @Expose
        var desc: String?

        @SerializedName("PlaylistImage")
        @Expose
        var image: String?

        @SerializedName("PlaylistMastercat")
        @Expose
        var masterCat: String?

        @SerializedName("PlaylistSubcat")
        @Expose
        var subCat: String?

        @SerializedName("Download")
        @Expose
        var download: String?

        @SerializedName("TotalAudio")
        @Expose
        var totalAudio: String?

        @SerializedName("Totalhour")
        @Expose
        var totalhour: String?

        @SerializedName("Totalminute")
        @Expose
        var totalminute: String?

        @SerializedName("Created")
        @Expose
        var created: String?
        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeString(iD)
            parcel.writeString(name)
            parcel.writeString(desc)
            parcel.writeString(image)
            parcel.writeString(masterCat)
            parcel.writeString(subCat)
            parcel.writeString(download)
            parcel.writeString(totalAudio)
            parcel.writeString(totalhour)
            parcel.writeString(totalminute)
            parcel.writeString(created)
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

        init {
            iD = `in`.readString()
            name = `in`.readString()
            desc = `in`.readString()
            image = `in`.readString()
            masterCat = `in`.readString()
            subCat = `in`.readString()
            download = `in`.readString()
            totalAudio = `in`.readString()
            totalhour = `in`.readString()
            totalminute = `in`.readString()
            created = `in`.readString()
        }
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<SearchPlaylistModel> = object : Parcelable.Creator<SearchPlaylistModel> {
            override fun createFromParcel(`in`: Parcel): SearchPlaylistModel? {
                return SearchPlaylistModel(`in`)
            }

            override fun newArray(size: Int): Array<SearchPlaylistModel?> {
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