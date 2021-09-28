package com.brainwellnessspa.billingOrderModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.player
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.models.CancelPlanModel
import com.brainwellnessspa.databinding.ActivityCancelMembershipBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.relesePlayer
import com.brainwellnessspa.utility.APINewClient.client
import com.brainwellnessspa.utility.CONSTANTS
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelMembershipActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    lateinit var binding: ActivityCancelMembershipBinding
    lateinit var ctx: Context
    var userID: String = ""
    var cancelId = ""
    lateinit var activity: Activity
    var audioPause = false
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_membership)
        ctx = this@CancelMembershipActivity
        activity = this@CancelMembershipActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")!!
        binding.rlCancelPlan.visibility = View.VISIBLE
        binding.llBack.setOnClickListener {
            myBackPress = true
            if (audioPause) {
                player.playWhenReady = true
            }
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        binding.youtubeView.initialize(API_KEY, this)
        val p = Properties()
        p.putValue("userId", userID)
        p.putValue("plan", "")
        p.putValue("planStatus", "")
        p.putValue("planStartDt", "")
        p.putValue("planExpiryDt", "")
        p.putValue("planAmount", "")
        BWSApplication.addToSegment("Cancel Subscription Viewed", p, CONSTANTS.screen)
        if (player != null) {
            if (player.playWhenReady) {
                player.playWhenReady = false
                audioPause = true
            }
        }
        binding.cbOne.setOnClickListener {
            binding.cbOne.isChecked = true
            binding.cbTwo.isChecked = false
            binding.cbThree.isChecked = false
            binding.cbFour.isChecked = false
            cancelId = "1"
            binding.edtCancelBox.visibility = GONE
            binding.edtCancelBox.setText("")
        }
        binding.cbTwo.setOnClickListener {
            binding.cbOne.isChecked = false
            binding.cbTwo.isChecked = true
            binding.cbThree.isChecked = false
            binding.cbFour.isChecked = false
            cancelId = "2"
            binding.edtCancelBox.visibility = GONE
            binding.edtCancelBox.setText("")
        }
        binding.cbThree.setOnClickListener {
            binding.cbOne.isChecked = false
            binding.cbTwo.isChecked = false
            binding.cbThree.isChecked = true
            binding.cbFour.isChecked = false
            cancelId = "3"
            binding.edtCancelBox.visibility = GONE
            binding.edtCancelBox.setText("")
        }
        binding.cbFour.setOnClickListener {
            binding.cbOne.isChecked = false
            binding.cbTwo.isChecked = false
            binding.cbThree.isChecked = false
            binding.cbFour.isChecked = true
            cancelId = "4"
            binding.edtCancelBox.visibility = VISIBLE
        }
        binding.btnCancelSubscrible.setOnClickListener {
            myBackPress = true
            if (player != null) {
                if (player.playWhenReady) {
                    player.playWhenReady = false
                    audioPause = true
                }
            }
            if (cancelId.equals("4", ignoreCase = true) && binding.edtCancelBox.text.toString() == "") {
                BWSApplication.showToast("Cancellation reason is required", activity)
            } else {
                val dialog = Dialog(ctx)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.cancel_membership)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.dark_blue_gray)))
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                val Btn = dialog.findViewById<Button>(R.id.Btn)
                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        if (player != null) {
                            if (player.playWhenReady) {
                                player.playWhenReady = false
                                audioPause = true
                            }
                        }
                        return@setOnKeyListener true
                    }
                    false
                }
                Btn.setOnTouchListener { view1: View, event: MotionEvent ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            val views = view1 as Button
                            views.background.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                            view1.invalidate()
                        }
                        MotionEvent.ACTION_UP -> {
                            if (BWSApplication.isNetworkConnected(ctx)) {
                                val listCall = client.getCancelPlanStripe(userID, cancelId, binding.edtCancelBox.text.toString())
                                listCall.enqueue(object : Callback<CancelPlanModel?> {
                                    override fun onResponse(call: Call<CancelPlanModel?>, response: Response<CancelPlanModel?>) {
                                        try {
                                            val model = response.body()
                                            BWSApplication.showToast(model!!.responseMessage, activity)
                                            dialog.dismiss()
                                            val CancelReason = binding.edtCancelBox.text.toString()
                                            /*Properties p = new Properties();
                                              p.putValue("userId", UserID);
                                              p.putValue("cancelId", CancelId);
                                              p.putValue("cancelReason", CancelReason);
                                              BWSApplication.addToSegment("Cancel Subscription Clicked", p, CONSTANTS.track);*/
                                            if (player != null) {
                                                if (player.playWhenReady) {
                                                    player.playWhenReady = false
                                                    audioPause = true
                                                }
                                            }
                                            finish()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call<CancelPlanModel?>, t: Throwable) {}
                                })
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), activity)
                            }
                            run {
                                val views = view1 as Button
                                views.background.clearColorFilter()
                                views.invalidate()
                            }
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            val views = view1 as Button
                            views.background.clearColorFilter()
                            views.invalidate()
                        }
                    }
                    true
                }
                tvGoBack.setOnClickListener { v: View? ->
                    dialog.dismiss()
                    if (player != null) {
                        if (player.playWhenReady) {
                            player.playWhenReady = false
                            audioPause = true
                        }
                    }
                }
                dialog.show()
                dialog.setCancelable(false)
            }
        }
    }

    override fun onBackPressed() {
        myBackPress = true
        if (audioPause) {
            player.playWhenReady = true
        }
        finish()
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(VIDEO_ID)
            youTubePlayer.setShowFullscreenButton(true)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
            BWSApplication.showToast(errorMessage, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            youTubePlayerProvider.initialize(API_KEY, this)
        }
    }

    private val youTubePlayerProvider: YouTubePlayer.Provider
        private get() = binding.youtubeView

    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND")
                //app went to foreground
            }
            numStarted++
        }

        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            numStarted--
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false")
                    stackStatus = 2
                } else {
                    myBackPress = true
                    stackStatus = 1
                    Log.e("APPLICATION", "back press true ")
                }
                Log.e("APPLICATION", "App is in BACKGROUND")
                // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BWSApplication.notificationId)
                relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        const val API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ"
        const val VIDEO_ID = "y1rfRW6WX08"
        private const val RECOVERY_DIALOG_REQUEST = 1
    }
}