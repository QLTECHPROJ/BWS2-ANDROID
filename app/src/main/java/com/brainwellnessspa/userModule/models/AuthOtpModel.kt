package com.brainwellnessspa.userModule.models

data class AuthOtpModel(val ResponseCode: String, val ResponseData: AuthOtpResponseData, val ResponseMessage: String, val ResponseStatus: String)

data class AuthOtpResponseData(val AreaOfFocus: List<AreaOfFocus>, val AvgSleepTime: String, val DOB: String, val Email: String, val Image: String, val MainAccountID: String, val Mobile: String, val Name: String, val ScoreLevel: String, val UserId: String, val directLogin: String, val errormsg: String, val indexScore: String, val isAssessmentCompleted: String, val isPinSet: String, val isProfileCompleted: String, val isMainAccount: String, val CoUserCount: String, val planDetails: List<Any>)

data class AreaOfFocus(val CatId: String, val MainCat: String, val RecommendedCat: String)