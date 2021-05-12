package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditProfileModel {
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
        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("PhoneNumber")
        @Expose
        var phoneNumber: String? = null

        @SerializedName("DOB")
        @Expose
        var dob: String? = null

        @SerializedName("Email")
        @Expose
        var email: String? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null
    }
}