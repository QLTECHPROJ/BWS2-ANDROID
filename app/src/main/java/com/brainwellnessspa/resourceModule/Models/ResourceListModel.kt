package com.brainwellnessspa.resourceModule.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResourceListModel {
    @SerializedName("ResponseData")
    @Expose
    var responseData: List<ResponseData>? = null

    @SerializedName("ResponseCode")
    @Expose
    var responseCode: String? = null

    @SerializedName("ResponseMessage")
    @Expose
    var responseMessage: String? = null

    @SerializedName("ResponseStatus")
    @Expose
    var responseStatus: String? = null

    inner class ResponseData {
        @SerializedName("ID")
        @Expose
        var iD: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("type")
        @Expose
        var type: String? = null

        @SerializedName("master_category")
        @Expose
        var masterCategory: String? = null

        @SerializedName("sub_category")
        @Expose
        var subCategory: String? = null

        @SerializedName("ResourceDesc")
        @Expose
        var description: String? = null

        @SerializedName("Detailimage")
        @Expose
        var detailimage: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("author")
        @Expose
        var author: String? = null

        @SerializedName("resource_link_1")
        @Expose
        var resourceLink1: String? = null

        @SerializedName("resource_link_2")
        @Expose
        var resourceLink2: String? = null
    }
}