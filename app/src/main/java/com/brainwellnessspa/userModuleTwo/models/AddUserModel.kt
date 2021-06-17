package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddUserModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData? = null

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
        @SerializedName("MainAccountID")
        @Expose
        var mainAccountID: String? = ""

        @SerializedName("UserId")
        @Expose
        var userId: String? = ""

        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("Email")
        @Expose
        var email: String? = ""

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""
    }
}