package com.brainwellnessspa.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.downloader.PRDownloader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MyNetworkReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val status = BWSApplication.isNetworkConnected(context)
        Log.d("network", status.toString())
        if (!status) {
            if (DownloadMedia.isDownloading) {
                PRDownloader.pause(DownloadMedia.downloadIdOne)
                DownloadMedia.isDownloading = false //                BWSApplication.showToast(String.valueOf(status)+Status.valueOf(PRDownloader.getStatus(downloadIdOne).name()),context);
            }
        } else {
            val shareded = context.getSharedPreferences(CONSTANTS.PREF_KEY_USER_ACTIVITY, Service.MODE_PRIVATE)
            val gson = Gson()
            val json = shareded.getString(CONSTANTS.PREF_KEY_USER_TRACK_ARRAY, gson.toString())
            var userActivityTrackModel = ArrayList<UserActivityTrackModel>()
            if (!json.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<ArrayList<UserActivityTrackModel?>?>() {}.type
                userActivityTrackModel = gson.fromJson(json, type)
                if (userActivityTrackModel.size != 0) {
                    val global = GlobalInitExoPlayer()
                    global.getUserActivityCall(context, "", "", "")
                }
            }
// TODO check shared pref and aema jo value hoy to get karine api call karvani
            // do some thing
            GlobalInitExoPlayer.callResumePlayer(context)
            if (!DownloadMedia.isDownloading) {
                val fileNameList: List<String>
                val audioFile: List<String>
                val playlistDownloadId: List<String>
                if (BWSApplication.isNetworkConnected(context)) {
                    val shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE)
                    val gson = Gson()
                    val json = shared.getString(CONSTANTS.PREF_KEY_DownloadName, gson.toString())
                    val json1 = shared.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson.toString())
                    val json2 = shared.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson.toString())
                    if (!json1.equals(gson.toString(), ignoreCase = true)) {
                        val type = object : TypeToken<List<String?>?>() {}.type
                        fileNameList = gson.fromJson(json, type)
                        audioFile = gson.fromJson(json1, type)
                        playlistDownloadId = gson.fromJson(json2, type)
                        /* fileNameList = new ArrayList<>();
                    audioFile = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    SharedPreferences sharedxxx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedxxx.edit();
                    String nameJson = gson.toJson(fileNameList);
                    String urlJson = gson.toJson(audioFile);
                    String playlistIdJson = gson.toJson(playlistDownloadId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.commit();*/
                        if (fileNameList.isNotEmpty()) {
                            DownloadMedia.isDownloading = true
                            val downloadMedia = DownloadMedia(context.applicationContext, context as Activity)
                            downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId /*, playlistSongs*/)
                        }
                    }
                }
            }
        }
    }
}