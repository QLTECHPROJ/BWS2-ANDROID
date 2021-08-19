package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditProfileModel {
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
        @SerializedName("Name")
        @Expose
        var name: String? = ""

        @SerializedName("PhoneNumber")
        @Expose
        var phoneNumber: String? = ""

        @SerializedName("DOB")
        @Expose
        var dob: String? = ""

        @SerializedName("Email")
        @Expose
        var email: String? = ""

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""

        @SerializedName("ageSlabChange")
        @Expose
        var ageSlabChange: String? = ""
    }
}