package com.brainwellnessspa.userModule.models

data class AuthOtpModel(val ResponseCode: String, val ResponseData: AuthOtpResponseData, val ResponseMessage: String, val ResponseStatus: String)

data class AuthOtpResponseData(val oldPaymentDetails: List<oldPaymentDetails>,val AreaOfFocus: List<AreaOfFocus>, val AvgSleepTime: String, val DOB: String, val Email: String, val IsFirst: String, val IsInCouser: String, val Image: String, val MainAccountID: String,val CountryCode: String, val Mobile: String, val Name: String, val ScoreLevel: String, val UserId: String, val directLogin: String, val errormsg: String, val indexScore: String, val isAssessmentCompleted: String, val isPinSet: String, val Islock: String, val isProfileCompleted: String, val isMainAccount: String, val CoUserCount: String, val isEmailVerified: String,val paymentType: String, val planDetails: List<planDetails>)

data class AreaOfFocus(val CatId: String, val MainCat: String, val RecommendedCat: String)

data class planDetails(val UserId: String, val UserGroupId: String, val PlanId: String, val PlanPurchaseDate: String, val PlanExpireDate: String, val OriginalTransactionId: String, val TransactionId: String, val TrialPeriodStart: String, val TrialPeriodEnd: String, val PlanStatus: String, val PlanContent: String)

data class oldPaymentDetails(val Plan: String, val PlanId: String, val PlanFlag: String, val purchaseDate: String, val OrderTotal: String,val PlanStr: String, val expireDate: String, val CardId: String,val PlanStatus: String)