package com.brainwellnessspa.UserModuleTwo.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

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
        @SerializedName("ID")
        @Expose
        var iD: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Code")
        @Expose
        var code: String? = null

        @SerializedName("ShortName")
        @Expose
        var shortName: String? = null

    }
}