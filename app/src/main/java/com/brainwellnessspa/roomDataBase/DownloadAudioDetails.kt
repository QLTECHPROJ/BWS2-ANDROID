package com.brainwellnessspa.roomDataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "audio_table")
class DownloadAudioDetails : Serializable {
    @PrimaryKey(autoGenerate = true)
    var uid = 0

    @ColumnInfo(name = "UserID")
    var UserID: String? = null

    @ColumnInfo(name = "ID")
    var ID: String? = null

    @ColumnInfo(name = "Name")
    var Name: String? = null

    @ColumnInfo(name = "AudioFile")
    var AudioFile: String? = null

    @ColumnInfo(name = "AudioDirection")
    var AudioDirection: String? = null

    @ColumnInfo(name = "Audiomastercat")
    var Audiomastercat: String? = null

    @ColumnInfo(name = "AudioSubCategory")
    var AudioSubCategory: String? = null

    @ColumnInfo(name = "ImageFile")
    var ImageFile: String? = null

    @ColumnInfo(name = "Like")
    var Like: String? = null

    @ColumnInfo(name = "Download")
    var Download: String? = null

    @ColumnInfo(name = "AudioDuration")
    var AudioDuration: String? = null

    @ColumnInfo(name = "PlaylistId")
    var PlaylistId: String? = null

    @ColumnInfo(name = "IsSingle")
    var IsSingle: String? = null

    @ColumnInfo(name = "IsDownload")
    var IsDownload: String? = null

    @ColumnInfo(name = "DownloadProgress")
    var DownloadProgress = 0
}