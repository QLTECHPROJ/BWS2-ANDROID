package com.brainwellnessspa.userModule.models

data class UserAccessModel(val ResponseCode: String,
    val ResponseData: UserResponseData,
    val ResponseMessage: String,
    val ResponseStatus: String)

data class UserResponseData(val MobileNo: String,
    val OTP: String,
    val SignupFlag: String,
    val errormsg: String)