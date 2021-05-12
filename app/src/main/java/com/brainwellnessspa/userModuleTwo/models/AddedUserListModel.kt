package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddedUserListModel {
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
        @SerializedName("Maxuseradd")
        @Expose
        var maxuseradd: String? = null

        @SerializedName("CoUserList")
        @Expose
        var coUserList: List<CoUser>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        class CoUser {
            @SerializedName("UserID")
            @Expose
            var userID: String? = null

            @SerializedName("CoUserId")
            @Expose
            var coUserId: String? = null

            @SerializedName("Name")
            @Expose
            var name: String? = null

            @SerializedName("Email")
            @Expose
            var email: String? = null

            @SerializedName("Mobile")
            @Expose
            var mobile: String? = null

            @SerializedName("DOB")
            @Expose
            var dob: String? = null

            @SerializedName("Image")
            @Expose
            var image: String? = null
        }

        fun getResponseMessage(): String? {
            return maxuseradd
        }

        fun setResponseMessage(maxuseradd: String?) {
            this.maxuseradd = maxuseradd
        }

        fun getCoUser(): List<CoUser>? {
            return coUserList
        }

        fun setCoUser(coUserList: List<CoUser>?) {
            this.coUserList = coUserList
        }
    }
}