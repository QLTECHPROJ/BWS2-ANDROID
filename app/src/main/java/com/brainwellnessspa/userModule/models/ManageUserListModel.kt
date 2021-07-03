package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ManageUserListModel {
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

        @SerializedName("UserList")
        @Expose
        var userList: List<User>? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null
    }

    class User {
        @SerializedName("MainAccountID")
        @Expose
        var mainAccountID: String? = null

        @SerializedName("Name")
        @Expose
        var name: String? = null

        @SerializedName("Mobile")
        @Expose
        var mobile: String? = null

        @SerializedName("InviteStatus")
        @Expose
        var inviteStatus: String? = null
    }
}