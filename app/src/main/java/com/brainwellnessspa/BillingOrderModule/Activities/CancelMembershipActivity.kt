package com.brainwellnessspa.BillingOrderModule.Activities

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
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BillingOrderModule.Activities.CancelMembershipActivity
import com.brainwellnessspa.BillingOrderModule.Models.CancelPlanModel
import com.brainwellnessspa.DashboardModule.Account.AccountFragment
import com.brainwellnessspa.R
import com.brainwellnessspa.Services.GlobalInitExoPlayer
import com.brainwellnessspa.Utility.APIClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityCancelMembershipBinding
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
    var UserID: String? = null
    var CancelId = ""
    lateinit var activity: Activity
    var audioPause = false
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_membership)
        ctx = this@CancelMembershipActivity
        activity = this@CancelMembershipActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        UserID = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        binding.llBack.setOnClickListener { view: View? ->
            myBackPress = true
            AccountFragment.ComeScreenAccount = 1
            if (audioPause) {
                GlobalInitExoPlayer.player.playWhenReady = true
            } else {
            }
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        binding.youtubeView.initialize(API_KEY, this)
        val p = Properties()
        p.putValue("userId", UserID)
        p.putValue("plan", "")
        p.putValue("planStatus", "")
        p.putValue("planStartDt", "")
        p.putValue("planExpiryDt", "")
        p.putValue("planAmount", "")
        BWSApplication.addToSegment("Cancel Subscription Viewed", p, CONSTANTS.screen)
        if (GlobalInitExoPlayer.player != null) {
            if (GlobalInitExoPlayer.player.playWhenReady) {
                GlobalInitExoPlayer.player.playWhenReady = false
                audioPause = true
            }
        }
        binding.cbOne.setOnClickListener { view: View? ->
            binding.cbOne.isChecked = true
            binding.cbTwo.isChecked = false
            binding.cbThree.isChecked = false
            binding.cbFour.isChecked = false
            CancelId = "1"
            binding.edtCancelBox.visibility = View.GONE
            binding.edtCancelBox.setText("")
        }
        binding.cbTwo.setOnClickListener { view: View? ->
            binding.cbOne.isChecked = false
            binding.cbTwo.isChecked = true
            binding.cbThree.isChecked = false
            binding.cbFour.isChecked = false
            CancelId = "2"
            binding.edtCancelBox.visibility = View.GONE
            binding.edtCancelBox.setText("")
        }
        binding.cbThree.setOnClickListener { view: View? ->
            binding.cbOne.isChecked = false
            binding.cbTwo.isChecked = false
            binding.cbThree.isChecked = true
            binding.cbFour.isChecked = false
            CancelId = "3"
            binding.edtCancelBox.visibility = View.GONE
            binding.edtCancelBox.setText("")
        }
        binding.cbFour.setOnClickListener { view: View? ->
            binding.cbOne.isChecked = false
            binding.cbTwo.isChecked = false
            binding.cbThree.isChecked = false
            binding.cbFour.isChecked = true
            CancelId = "4"
            binding.edtCancelBox.visibility = View.VISIBLE
        }
        binding.btnCancelSubscrible.setOnClickListener { view: View? ->
            myBackPress = true
            if (GlobalInitExoPlayer.player != null) {
                if (GlobalInitExoPlayer.player.playWhenReady) {
                    GlobalInitExoPlayer.player.playWhenReady = false
                    audioPause = true
                }
            }
            if (CancelId.equals("4", ignoreCase = true) &&
                binding.edtCancelBox.text.toString().equals("", ignoreCase = true)
            ) {
                BWSApplication.showToast("Cancellation reason is required", activity)
            } else {
                val dialog = Dialog(ctx)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.cancel_membership)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.dark_blue_gray)))
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                val Btn = dialog.findViewById<Button>(R.id.Btn)
                dialog.setOnKeyListener { v: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        if (GlobalInitExoPlayer.player != null) {
                            if (GlobalInitExoPlayer.player.playWhenReady) {
                                GlobalInitExoPlayer.player.playWhenReady = false
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
                                val listCall = APIClient.getClient().getCancelPlan(
                                    UserID,
                                    CancelId,
                                    binding.edtCancelBox.text.toString()
                                )
                                listCall.enqueue(object : Callback<CancelPlanModel?> {
                                    override fun onResponse(
                                        call: Call<CancelPlanModel?>,
                                        response: Response<CancelPlanModel?>
                                    ) {
                                        try {
                                            val model = response.body()
                                            BWSApplication.showToast(
                                                model!!.responseMessage,
                                                activity
                                            )
                                            dialog.dismiss()
                                            val CancelReason = binding.edtCancelBox.text.toString()
                                            /*Properties p = new Properties();
                                            p.putValue("userId", UserID);
                                            p.putValue("cancelId", CancelId);
                                            p.putValue("cancelReason", CancelReason);
                                            BWSApplication.addToSegment("Cancel Subscription Clicked", p, CONSTANTS.track);*/if (GlobalInitExoPlayer.player != null) {
                                                if (GlobalInitExoPlayer.player.playWhenReady) {
                                                    GlobalInitExoPlayer.player.playWhenReady = false
                                                    audioPause = true
                                                }
                                            }
                                            finish()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<CancelPlanModel?>,
                                        t: Throwable
                                    ) {
                                    }
                                })
                            } else {
                                BWSApplication.showToast(
                                    getString(R.string.no_server_found),
                                    activity
                                )
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
                    if (GlobalInitExoPlayer.player != null) {
                        if (GlobalInitExoPlayer.player.playWhenReady) {
                            GlobalInitExoPlayer.player.playWhenReady = false
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
        AccountFragment.ComeScreenAccount = 1
        if (audioPause) {
            GlobalInitExoPlayer.player.playWhenReady = true
        } else {
        }
        finish()
        //        resumeMedia();
//        isPause = false;
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider,
        youTubePlayer: YouTubePlayer,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(VIDEO_ID)
            youTubePlayer.setShowFullscreenButton(true)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        errorReason: YouTubeInitializationResult
    ) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format(
                getString(R.string.error_player), errorReason.toString()
            )
            BWSApplication.showToast(errorMessage, activity)
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
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(GlobalInitExoPlayer.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        const val API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ"
        const val VIDEO_ID = "y1rfRW6WX08"
        const val RECOVERY_DIALOG_REQUEST = 1
    }
}