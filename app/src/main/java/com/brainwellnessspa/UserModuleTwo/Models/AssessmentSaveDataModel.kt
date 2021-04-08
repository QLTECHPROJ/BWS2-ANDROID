package com.brainwellnessspa.UserModuleTwo.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssessmentSaveDataModel {
    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = ""

    @SerializedName("ResponseData")
    @Expose
    var responseData: ResponseData = ResponseData()

     class ResponseData {

        @SerializedName("indexScore")
        @Expose
        var indexScore: String? = ""
        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""
    }
}