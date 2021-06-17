package com.brainwellnessspa.resourceModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResourceListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = ""

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = ""

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = ""

    inner class ResponseData {
        @SerializedName("ID")
        @Expose
        var iD: String? = ""

        @SerializedName("title")
        @Expose
        var title: String? = ""

        @SerializedName("type")
        @Expose
        var type: String? = ""

        @SerializedName("master_category")
        @Expose
        var masterCategory: String? = ""

        @SerializedName("sub_category")
        @Expose
        var subCategory: String? = ""

        @SerializedName("ResourceDesc")
        @Expose
        var description: String? = ""

        @SerializedName("Detailimage")
        @Expose
        var detailimage: String? = ""

        @SerializedName("image")
        @Expose
        var image: String? = ""

        @SerializedName("author")
        @Expose
        var author: String? = ""

        @SerializedName("resource_link_1")
        @Expose
        var resourceLink1: String? = ""

        @SerializedName("resource_link_2")
        @Expose
        var resourceLink2: String? = ""
    }
}