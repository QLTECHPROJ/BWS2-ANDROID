package com.brainwellnessspa.UserModuleTwo.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddUserModel {
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
        @SerializedName("ID")
        @Expose
        var iD: String? = null

        @SerializedName("CoUserId")
        @Expose
        var coUserId: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Email")
        @Expose
        var email: String? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null
    }
}