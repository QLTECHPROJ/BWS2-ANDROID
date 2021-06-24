package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddedUserListModel {
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
        @SerializedName("Maxuseradd")
        @Expose
        var maxuseradd: String? = ""

        @SerializedName("UserList")
        @Expose
        var userList: List<CoUser>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = ""

        class CoUser {
            @SerializedName("MainAccountID")
            @Expose
            var mainAccountID: String? = ""

            @SerializedName("UserId")
            @Expose
            var userID: String? = ""

            @SerializedName("CoUserId")
            @Expose
            var coUserId: String? = ""

            @SerializedName("Name")
            @Expose
            var name: String? = ""

            @SerializedName("Email")
            @Expose
            var email: String? = ""

            @SerializedName("Mobile")
            @Expose
            var mobile: String? = ""

            @SerializedName("DOB")
            @Expose
            var dob: String? = ""

            @SerializedName("Image")
            @Expose
            var image: String? = ""
        }

    }
}