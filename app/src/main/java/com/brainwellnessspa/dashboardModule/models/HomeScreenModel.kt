package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeScreenModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    private var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    private var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    private var responseStatus: String? = ""

    class ResponseData {
        @SerializedName("IndexScore")
        @Expose
        var indexScore: String? = ""

        @SerializedName("shouldCheckIndexScore")
        @Expose
        var shouldCheckIndexScore: String? = ""

        @SerializedName("disclaimerAudio")
        @Expose
        var disclaimerAudio: DisclaimerAudio? = null

        @SerializedName("ScoreIncDec")
        @Expose
        var scoreIncDec: String? = ""

        @SerializedName("shouldPlayDisclaimer")
        @Expose
        var shouldPlayDisclaimer: String? = ""

        @SerializedName("IndexScoreDiff")
        @Expose
        var indexScoreDiff: String? = ""

        @SerializedName("IsFirst")
        @Expose
        var isFirst: String? = ""

        @SerializedName("SuggestedPlaylist")
        @Expose
        var suggestedPlaylist: SuggestedPlaylist? = null

        @SerializedName("PastIndexScore")
        @Expose
        var pastIndexScore: List<PastIndexScore>? = null

        @SerializedName("SessionScore")
        @Expose
        var sessionScore: List<SessionScore>? = null

        @SerializedName("SessionProgress")
        @Expose
        var sessionProgress: List<SessionProgress>? = null

        class SuggestedPlaylist {
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

            @SerializedName("PlaylistSongs")
            @Expose
            var playlistSongs: List<PlaylistSong>? = null

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

        class DisclaimerAudio {
            @SerializedName("ID")
            @Expose
            var id: String? = ""

            @SerializedName("Name")
            @Expose
            var name: String? = ""

            @SerializedName("ImageFile")
            @Expose
            var imageFile: String? = ""

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

        class SessionScore {

        }

        class PastIndexScore {
            @SerializedName("Month")
            @Expose
            var month: String? = ""

            @SerializedName("MonthName")
            @Expose
            var monthName: String? = ""

            @SerializedName("IndexScore")
            @Expose
            var indexScore: String? = ""
        }

        class SessionProgress {

        }
    }
}