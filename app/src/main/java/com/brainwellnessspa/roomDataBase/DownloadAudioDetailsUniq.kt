package com.brainwellnessspa.roomDataBase

import java.io.Serializable


class DownloadAudioDetailsUniq : Serializable {
    var ID: String? = null
    var UserID: String? = null
    var Name: String? = null
    var AudioFile: String? = null
    var AudioDirection: String? = null
    var Audiomastercat: String? = null
    var AudioSubCategory: String? = null
    var ImageFile: String? = null
    var AudioDuration: String? = null
    var PlaylistId: String? = null
    var IsSingle: String? = null
    var IsDownload: String? = null
    var DownloadProgress = 0
}