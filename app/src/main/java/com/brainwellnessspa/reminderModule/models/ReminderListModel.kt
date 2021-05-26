package com.brainwellnessspa.reminderModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReminderListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData?>? = null

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
        @SerializedName("PlaylistId")
        @Expose
        var playlistId: String? = null

        @SerializedName("PlaylistName")
        @Expose
        var playlistName: String? = null

        @SerializedName("ReminderDay")
        @Expose
        var reminderDay: String? = null

        @SerializedName("ReminderTime")
        @Expose
        var reminderTime: String? = null

        @SerializedName("IsLock")
        @Expose
        var isLock: String? = null

        @SerializedName("IsActive")
        @Expose
        var isActive: String? = null

        @SerializedName("RDay")
        @Expose
        var rDay: String? = null

        @SerializedName("IsCheck")
        @Expose
        var isCheck: String? = null

        @SerializedName("ReminderId")
        @Expose
        var reminderId: String? = null

        @SerializedName("Created")
        @Expose
        var created: String? = null
    }
}