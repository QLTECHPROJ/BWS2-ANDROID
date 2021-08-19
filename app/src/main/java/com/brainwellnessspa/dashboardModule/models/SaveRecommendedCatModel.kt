package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveRecommendedCatModel {
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

    class ResponseData {

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        @SerializedName("showAlert")
        @Expose
        var showAlert: String? = null

        @SerializedName("popupTitle")
        @Expose
        var popupTitle: String? = null

        @SerializedName("popupContent")
        @Expose
        var popupContent: String? = null

        @SerializedName("UserId")
        @Expose
        var userId: String? = null

        @SerializedName("AvgSleepTime")
        @Expose
        var avgSleepTime: String? = null

        @SerializedName("NoUpdation")
        @Expose
        var noUpdation: String? = null

        @SerializedName("AreaOfFocus")
        @Expose
        var areaOfFocus: List<AreaOfFocus>? = null

        @SerializedName("SuggestedPlaylist")
        @Expose
        var suggestedPlaylist: SuggestedPlaylist? = null

        class AreaOfFocus {
            @SerializedName("CatId")
            @Expose
            var catId: String? = null

            @SerializedName("MainCat")
            @Expose
            var mainCat: String? = null

            @SerializedName("RecommendedCat")
            @Expose
            var recommendedCat: String? = null
        }
        class SuggestedPlaylist {
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

            @SerializedName("playlistDirection")
            @Expose
            var playlistDirection: String? = null

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

            @SerializedName("Totalsecond")
            @Expose
            var totalsecond: String? = null

            @SerializedName("ReminderId")
            @Expose
            var reminderId: String? = null

            @SerializedName("IsReminder")
            @Expose
            var isReminder: String? = null

            @SerializedName("ReminderDay")
            @Expose
            var reminderDay: String? = null

            @SerializedName("ReminderTime")
            @Expose
            var reminderTime: String? = null

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

                @SerializedName("IsPlay")
                @Expose
                var isPlay: String? = null
            }
        }
    }
}