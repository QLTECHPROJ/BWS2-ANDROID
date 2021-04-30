package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeScreenModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

    @SerializedName("ResponseCode")
    @Expose
    private var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    private var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    private var responseStatus: String? = null

    class ResponseData {
        @SerializedName("IndexScore")
        @Expose
        var indexScore: String? = null

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

            @SerializedName("PlaylistSongs")
            @Expose
            var playlistSongs: List<PlaylistSongs>? = null

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

            @SerializedName("IsReminder")
            @Expose
            var isReminder: String? = null

            @SerializedName("ReminderDay")
            @Expose
            var reminderDay: String? = null

            @SerializedName("ReminderTime")
            @Expose
            var reminderTime: String? = null

            class PlaylistSongs {

            }
        }

        class SessionScore {

        }

        class PastIndexScore {

        }

        class SessionProgress {

        }
    }
}