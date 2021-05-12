package com.brainwellnessspa.userModuleTwo.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForgotPasswordModel {
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
        private var errormsg: String? = null

        fun getErrormsg(): String? {
            return errormsg;
        }

        fun setErrormsg(errormsg: String?) {
            this.errormsg = errormsg;
        }
    }
}