package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class PlaylistDetailsModel {
    @SerializedName("ResponseData")
    @Expose
    val responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    val responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    val responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    val responseStatus: String? = null

    class ResponseData {
        @SerializedName("PlaylistID")
        @Expose
        var playlistID: String? = null

        @SerializedName("PlaylistName")
        @Expose
        var playlistName: String? = null

        @SerializedName("PlaylistDesc")
        @Expose
        var playlistDesc: String? = null

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

        @SerializedName("PlaylistSongs")
        @Expose
        var playlistSongs: List<PlaylistSong>? = null

        @SerializedName("Created")
        @Expose
        var created: String? = null

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

    @SerializedName("Like")
    @Expose
    var like: String? = null

    @SerializedName("Download")
    @Expose
    var download: String? = null

        class PlaylistSong {
            @SerializedName("ID")
            @Expose
            var id: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = null

            @SerializedName("PlaylistID")
            @Expose
            var playlistID: String? = null

            @SerializedName("PSID")
            @Expose
            var psid: String? = null

            @SerializedName("SortId")
            @Expose
            var sortId: String? = null

            @SerializedName("AudioFile")
            @Expose
            var audioFile: String? = null

            @SerializedName("Audiomastercat")
            @Expose
            var audiomastercat: String? = null

            @SerializedName("AudioSubCategory")
            @Expose
            var audioSubCategory: String? = null

            @SerializedName("AudioDuration")
            @Expose
            var audioDuration: String? = null

            @SerializedName("AudioDirection")
            @Expose
            var audioDirection: String? = null

            @SerializedName("Bitrate")
            @Expose
            var bitrate: String? = null
        }
    }
}
