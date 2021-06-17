package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

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
        @SerializedName("ID")
        @Expose
        var iD: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("Code")
        @Expose
        var code: String? = ""

        @SerializedName("ShortName")
        @Expose
        var shortName: String? = ""

    }
}