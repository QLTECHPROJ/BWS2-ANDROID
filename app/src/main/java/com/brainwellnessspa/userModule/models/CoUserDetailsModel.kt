package com.brainwellnessspa.userModule.models

data class CoUserDetailsModel(
    val ResponseCode: String?,
    val ResponseData: ResponseData?,
    val ResponseMessage: String?,
    val ResponseStatus: String?
)

data class ResponseData(
    val AreaOfFocus: List<AreaOfFocus>?,
    val AvgSleepTime: String?,
    val DOB: String?,
    val Email: String?,
    val Image: String?,
    val MainAccountID: String?,
    val Mobile: String?,
    val Name: String?,
    val ScoreLevel: String?,
    val UserId: String?,
    val errormsg: String?,
    val indexScore: String?,
    val isAssessmentCompleted: String?,
    val isProfileCompleted: String?,
    val planDetails: List<Any>?
)

data class AreaOfFocus(
    val CatId: String?,
    val MainCat: String?,
    val RecommendedCat: String?
)