package com.brainwellnessspa.DashboardTwoModule.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RecommendedCategoryModel {
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
        @SerializedName("Mental Health")
        @Expose
        var mentalHealth: List<MentalHealth>? = null

        @SerializedName("Self - Development")
        @Expose
        var selfDevelopment: List<SelfDevelopment>? = null

        @SerializedName("Addiction")
        @Expose
        var addiction: List<Addiction>? = null

        class SelfDevelopment {
            @SerializedName("Name")
            @Expose
            var name: String? = null
        }

        class MentalHealth {
            @SerializedName("Name")
            @Expose
            var name: String? = null
        }

        class Addiction {
            @SerializedName("Name")
            @Expose
            var name: String? = null
        }
    }
}

