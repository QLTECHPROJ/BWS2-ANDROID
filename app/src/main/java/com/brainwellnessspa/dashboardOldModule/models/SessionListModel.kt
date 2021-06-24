package com.brainwellnessspa.dashboardOldModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SessionListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

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
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("CatName")
        @Expose
        var catName: String? = null

        @SerializedName("Image")
        @Expose
        var image: String? = null

        @SerializedName("Category")
        @Expose
        var category: String? = null

        @SerializedName("CatMenual")
        @Expose
        var catMenual: String? = null

        @SerializedName("DescInfusion")
        @Expose
        var descInfusion: String? = null

        @SerializedName("Desc")
        @Expose
        var desc: String? = null

        @SerializedName("Date")
        @Expose
        var date: String? = null

        @SerializedName("Duration")
        @Expose
        var duration: String? = null

        @SerializedName("Time")
        @Expose
        var time: String? = null

        @SerializedName("Status")
        @Expose
        var status: String? = null

        @SerializedName("Session")
        @Expose
        var session: String? = null
    }
}