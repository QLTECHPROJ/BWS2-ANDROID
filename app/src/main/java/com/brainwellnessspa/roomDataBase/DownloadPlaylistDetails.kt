package com.brainwellnessspa.roomDataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "playlist_table")
class DownloadPlaylistDetails : Serializable {
    @PrimaryKey(autoGenerate = true)
    var uid = 0

    @ColumnInfo(name = "UserID")
    var UserID: String? = null

    @ColumnInfo(name = "PlaylistID")
    var PlaylistID: String? = null

    @ColumnInfo(name = "PlaylistName")
    var PlaylistName: String? = null

    @ColumnInfo(name = "PlaylistDesc")
    var PlaylistDesc: String? = null

    @ColumnInfo(name = "IsReminder")
    var IsReminder: String? = null

    @ColumnInfo(name = "PlaylistMastercat")
    var PlaylistMastercat: String? = null

    @ColumnInfo(name = "PlaylistSubcat")
    var PlaylistSubcat: String? = null

    @ColumnInfo(name = "PlaylistImage")
    var PlaylistImage: String? = null

    @ColumnInfo(name = "PlaylistImageDetails")
    var PlaylistImageDetails: String? = null

    @ColumnInfo(name = "TotalAudio")
    var TotalAudio: String? = null

    @ColumnInfo(name = "TotalDuration")
    var TotalDuration: String? = null

    @ColumnInfo(name = "Totalhour")
    var Totalhour: String? = null

    @ColumnInfo(name = "Totalminute")
    var Totalminute: String? = null

    @ColumnInfo(name = "Created")
    var Created: String? = null

    @ColumnInfo(name = "Download")
    var Download: String? = null

    @ColumnInfo(name = "Like")
    var Like: String? = null
}