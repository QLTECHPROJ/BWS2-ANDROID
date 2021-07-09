package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteInviteUserModel {

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

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null
    }
}