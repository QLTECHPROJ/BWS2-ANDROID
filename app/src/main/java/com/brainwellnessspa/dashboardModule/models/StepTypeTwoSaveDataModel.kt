package com.brainwellnessspa.dashboardModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class StepTypeTwoSaveDataModel {

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
        @SerializedName("section_title")
        @Expose
        var sectionTitle: String? = null

        @SerializedName("section_image")
        @Expose
        var sectionImage: String? = null

        @SerializedName("section_subtitle")
        @Expose
        var sectionSubtitle: String? = null

        @SerializedName("current_section")
        @Expose
        var currentSection: String? = null

        @SerializedName("section_description")
        @Expose
        var sectionDescription: String? = null

        @SerializedName("total_section")
        @Expose
        var totalSection: String? = null

        @SerializedName("formType")
        @Expose
        var formType: String? = null

        @SerializedName("question_title")
        @Expose
        var questionTitle: String? = null

        @SerializedName("question_description")
        @Expose
        var questionDescription: String? = null

        @SerializedName("option_type")
        @Expose
        var optionType: String? = null

        @SerializedName("chunk_size")
        @Expose
        var chunkSize: String? = null

        @SerializedName("errormsg")
        @Expose
        var errormsg: String? = null

        @SerializedName("questions")
        @Expose
        var questions: List<Question>? = null

        class Question {

            @SerializedName("question_id")
            @Expose
            var questionId: String? = null

            @SerializedName("question")
            @Expose
            var question: String? = null

            @SerializedName("question_options")
            @Expose
            var questionOptions: List<String>? = null
        }
    }
}