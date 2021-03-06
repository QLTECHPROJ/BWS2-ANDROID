package com.brainwellnessspa.userModule.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VersionModel(val ResponseCode: String,
                        val ResponseData: VersionResponseData,
                        val ResponseMessage: String,
                        val ResponseStatus: String)

data class VersionResponseData(val IsForce: String, val IsLoginFirstTime: String, val segmentKey: String, val supportTitle: String, val supportText: String, val supportEmail: String)