package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class PlaylistDetailsModel {
    @SerializedName("ResponseData")
    @Expose
    val responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    val responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    val responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    val responseStatus: String? = ""

    class ResponseData {

        @SerializedName("PlaylistID")
        @Expose
        var playlistID: String? = ""

        @SerializedName("PlaylistName")
        @Expose
        var playlistName: String? = ""

        @SerializedName("PlaylistDesc")
        @Expose
        var playlistDesc: String? = ""

        @SerializedName("PlaylistMastercat")
        @Expose
        var playlistMastercat: String? = ""

        @SerializedName("PlaylistSubcat")
        @Expose
        var playlistSubcat: String? = ""

        @SerializedName("PlaylistImage")
        @Expose
        var playlistImage: String? = ""

        @SerializedName("PlaylistImageDetail")
        @Expose
        var playlistImageDetail: String? = ""

        @SerializedName("PlaylistSongs")
        @Expose
        var playlistSongs: ArrayList<PlaylistSong>? = null

        @SerializedName("Created")
        @Expose
        var created: String? = ""

        @SerializedName("TotalAudio")
        @Expose
        var totalAudio: String? = ""

        @SerializedName("TotalDuration")
        @Expose
        var totalDuration: String? = ""

        @SerializedName("Totalhour")
        @Expose
        var totalhour: String? = ""

        @SerializedName("Totalminute")
        @Expose
        var totalminute: String? = ""

        @SerializedName("IsReminder")
        @Expose
        var isReminder: String? = ""

        @SerializedName("ReminderDay")
        @Expose
        var reminderDay: String? = ""

        @SerializedName("ReminderTime")
        @Expose
        var reminderTime: String? = ""

        @SerializedName("ReminderId")
        @Expose
        var reminderId: String? = ""

        class PlaylistSong {


            @SerializedName("ID")
            @Expose
            var id: String? = ""

            @SerializedName("Name")
            @Expose
            var name: String? = ""

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = ""

            @SerializedName("PlaylistID")
            @Expose
            var playlistID: String? = ""

            @SerializedName("PSID")
            @Expose
            var psid: String? = ""

            @SerializedName("SortId")
            @Expose
            var sortId: String? = ""

            @SerializedName("AudioFile")
            @Expose
            var audioFile: String? = ""

            @SerializedName("Audiomastercat")
            @Expose
            var audiomastercat: String? = ""

            @SerializedName("AudioSubCategory")
            @Expose
            var audioSubCategory: String? = ""

            @SerializedName("AudioDuration")
            @Expose
            var audioDuration: String? = ""

            @SerializedName("AudioDirection")
            @Expose
            var audioDirection: String? = ""

            @SerializedName("Bitrate")
            @Expose
            var bitrate: String? = ""
        }
    }
}
