package com.brainwellnessspa.dashboardModule.models

data class RenameNewPlaylistModel(
    val ResponseCode: String,
    val ResponseData: ResponseData,
    val ResponseMessage: String,
    val ResponseStatus: String
)

data class ResponseData(
    val IsRename: String
)