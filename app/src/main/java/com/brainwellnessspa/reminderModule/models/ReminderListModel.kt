package com.brainwellnessspa.reminderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReminderListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData?>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = ""

    class ResponseData {
        @SerializedName("PlaylistId")
        @Expose
        var playlistId: String? = ""

        @SerializedName("PlaylistName")
        @Expose
        var playlistName: String? = ""

        @SerializedName("ReminderDay")
        @Expose
        var reminderDay: String? = ""

        @SerializedName("ReminderTime")
        @Expose
        var reminderTime: String? = ""

        @SerializedName("IsLock")
        @Expose
        var isLock: String? = ""

        @SerializedName("RDay")
        @Expose
        var rDay: String? = ""

        @SerializedName("IsCheck")
        @Expose
        var isCheck: String? = ""

        @SerializedName("ReminderId")
        @Expose
        var reminderId: String? = ""

        @SerializedName("Created")
        @Expose
        var created: String? = ""
    }
}