package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BrainCatListModel {
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
        @SerializedName("data")
        @Expose
        var data: List<Data>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        class Data {
            @SerializedName("id")
            @Expose
            var id: String? = null

            @SerializedName("name")
            @Expose
            var name: String? = null

            @SerializedName("cat_flag")
            @Expose
            var catFlag: String? = null

            @SerializedName("status")
            @Expose
            var status: String? = null

            @SerializedName("color")
            @Expose
            var color: String? = null
        }
    }
}