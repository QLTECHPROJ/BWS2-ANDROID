package com.brainwellnessspa.dashboardOldModule.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class NextSessionViewModel {
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

    inner class ResponseData {
        @SerializedName("Response")
        @Expose
        var response: String? = null

        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Date")
        @Expose
        var date: String? = null

        @SerializedName("Duration")
        @Expose
        var duration: String? = null

        @SerializedName("Time")
        @Expose
        var time: String? = null

        @SerializedName("Task")
        @Expose
        var task: Task? = null

        inner class Task {
            @SerializedName("title")
            @Expose
            var title: String? = null

            @SerializedName("AudioTask")
            @Expose
            var audioTask: String? = null

            @SerializedName("subtitle")
            @Expose
            var subtitle: String? = null

            @SerializedName("BookletTask")
            @Expose
            var bookletTask: String? = null

            @SerializedName("taskflag")
            @Expose
            var taskflag: String? = null
        }
    }
}