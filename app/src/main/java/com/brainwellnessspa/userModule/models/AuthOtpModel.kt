package com.brainwellnessspa.userModule.models

data class AuthOtpModel(
    val ResponseCode: String,
    val ResponseData: AuthOtpResponseData,
    val ResponseMessage: String,
    val ResponseStatus: String
)

data class AuthOtpResponseData(
    val AreaOfFocus: List<Any>,
    val AvgSleepTime: String,
    val CountryCode: String,
    val DOB: String,
    val Email: String,
    val Image: String,
    val MainAccountID: String,
    val MobileNo: String,
    val EmailSend: String,
    val Name: String,
    val ScoreLevel: String,
    val UserId: String,
    val errormsg: String,
    val indexScore: String,
    val isAssessmentCompleted: String,
    val isProfileCompleted: String,
    val planDetails: List<Any>
)