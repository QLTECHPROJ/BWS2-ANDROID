package com.brainwellnessspa.roomDataBase

import java.io.Serializable

class DownloadPlaylistDetailsUnique : Serializable {
    var UserID: String? = null
    var PlaylistID: String? = null
    var PlaylistName: String? = null
    var PlaylistDesc: String? = null
    var IsReminder: String? = null
    var PlaylistMastercat: String? = null
    var PlaylistSubcat: String? = null
    var PlaylistImage: String? = null
    var PlaylistImageDetails: String? = null
    var TotalAudio: String? = null
    var TotalDuration: String? = null
    var Totalhour: String? = null
    var Totalminute: String? = null
    var Created: String? = null
}