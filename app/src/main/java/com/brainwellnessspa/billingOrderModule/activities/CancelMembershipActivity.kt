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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.models.CancelPlanModel
import com.brainwellnessspa.databinding.ActivityCancelMembershipBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.userModule.models.DeleteInviteUserModel
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/* This is the old BWA cancel membership activity */
class CancelMembershipActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    lateinit var binding: ActivityCancelMembershipBinding
    lateinit var ctx: Context
    var mainAccountId: String? = ""
    var userId: String? = ""
    var cancelId = ""
    var deleteId = ""
    lateinit var activity: Activity
    var audioPause = false
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    var screenView: String? = ""

    /* This is the first lunched function */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)/* This is the layout showing */
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cancel_membership)
        ctx = this@CancelMembershipActivity
        activity = this@CancelMembershipActivity

        /* This is the get string userID */
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        if (intent.extras != null) {
            screenView = intent.getStringExtra("screenView")
        }

        val measureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
        binding.imageView.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.imageView.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.imageView.scaleType = ImageView.ScaleType.FIT_XY
        binding.imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.delete_account_bg))

        /* This condition is check about application in background or foreground */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }

        /* This is the screen back button click */

        when {
//            Delete Account
            screenView.equals("0") -> {
                binding.rlDeleteAc.visibility = View.VISIBLE
                binding.rlCancelPlan.visibility = View.GONE
                binding.tvTilte.text = getString(R.string.delete_account)

                binding.llBack.setOnClickListener {
                    finish()
                }

                binding.cbOneAc.setOnClickListener {
                    binding.cbOneAc.isChecked = true
                    binding.cbTwoAc.isChecked = false
                    binding.cbThreeAc.isChecked = false
                    binding.cbFourAc.isChecked = false
                    deleteId = "1"
                    binding.edtCancelBoxAc.visibility = View.GONE
                    binding.edtCancelBoxAc.setText("")
                }

                binding.cbTwoAc.setOnClickListener {
                    binding.cbOneAc.isChecked = false
                    binding.cbTwoAc.isChecked = true
                    binding.cbThreeAc.isChecked = false
                    binding.cbFourAc.isChecked = false
                    deleteId = "2"
                    binding.edtCancelBoxAc.visibility = View.GONE
                    binding.edtCancelBoxAc.setText("")
                }

                binding.cbThreeAc.setOnClickListener {
                    binding.cbOneAc.isChecked = false
                    binding.cbTwoAc.isChecked = false
                    binding.cbThreeAc.isChecked = true
                    binding.cbFourAc.isChecked = false
                    deleteId = "3"
                    binding.edtCancelBoxAc.visibility = View.GONE
                    binding.edtCancelBoxAc.setText("")
                }

                binding.cbFourAc.setOnClickListener {
                    binding.cbOneAc.isChecked = false
                    binding.cbTwoAc.isChecked = false
                    binding.cbThreeAc.isChecked = false
                    binding.cbFourAc.isChecked = true
                    deleteId = "4"
                    binding.edtCancelBoxAc.visibility = View.VISIBLE
                }

                binding.btnDeleteAccount.setOnClickListener {
                    if (deleteId.equals("4", ignoreCase = true) && binding.edtCancelBoxAc.text.toString().equals("", ignoreCase = true)) {
                        BWSApplication.showToast("Cancellation reason is required", activity)
                    } else {/*This dialog is cancel membership  */
                        val dialog = Dialog(ctx)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.cancel_membership)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(ctx, R.color.dark_blue_gray)))
                        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                        val btn = dialog.findViewById<Button>(R.id.Btn)
                        dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss()
                                if (BWSApplication.player != null) {
                                    if (BWSApplication.player.playWhenReady) {
                                        BWSApplication.player.playWhenReady = false
                                        audioPause = true
                                    }
                                }
                                return@setOnKeyListener true
                            }
                            false
                        }

                        /* This click event is called when cancelling subscription */
                        btn.setOnTouchListener { view1: View, event: MotionEvent ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    val views = view1 as Button
                                    views.background.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                                    view1.invalidate()
                                }
                                MotionEvent.ACTION_UP -> {
                                    if (BWSApplication.isNetworkConnected(ctx)) {
                                        val listCall = APINewClient.client.getDeleteAccount(userId, deleteId, binding.edtCancelBoxAc.text.toString())
                                        listCall?.enqueue(object : Callback<DeleteInviteUserModel?> {
                                            override fun onResponse(call: Call<DeleteInviteUserModel?>, response: Response<DeleteInviteUserModel?>) {
                                                try {
                                                    val model = response.body()
                                                    BWSApplication.showToast(model!!.responseMessage, activity)
                                                    dialog.dismiss()
                                                    deleteCall()
                                                    val i = Intent(activity, SignInActivity::class.java)
                                                    i.putExtra("mobileNo", "")
                                                    i.putExtra("countryCode", "")
                                                    i.putExtra("name", "")
                                                    i.putExtra("email", "")
                                                    i.putExtra("countryShortName", "")
                                                    startActivity(i)
                                                    finish()
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }

                                            override fun onFailure(call: Call<DeleteInviteUserModel?>, t: Throwable) {
                                            }
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

                        /* This click event is called when not cancelling subscription */
                        tvGoBack.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                        dialog.setCancelable(false)
                    }
                }

            }
//            Cancel plan
            screenView.equals("1") -> {
                binding.rlDeleteAc.visibility = View.GONE
                binding.rlCancelPlan.visibility = View.VISIBLE
                binding.tvTilte.text = getString(R.string.cancel_plan)

                binding.llBack.setOnClickListener {
                    myBackPress = true
                    if (audioPause) {
                        BWSApplication.player.playWhenReady = true
                    }
                    finish()
                }

                /*This is to youtube video playing */
                binding.youtubeView.initialize(API_KEY, this)
                val p = Properties()
                p.putValue("plan", "")
                p.putValue("planStatus", "")
                p.putValue("planStartDt", "")
                p.putValue("planExpiryDt", "")
                p.putValue("planAmount", "")
                BWSApplication.addToSegment(CONSTANTS.Cancel_Subscription_Viewed, p, CONSTANTS.screen)

                /*This condition is to audio playing or not  */
                if (BWSApplication.player != null) {
                    if (BWSApplication.player.playWhenReady) {
                        BWSApplication.player.playWhenReady = false
                        audioPause = true
                    }
                }

                /*This click event is option one select  */
                binding.cbOne.setOnClickListener {
                    binding.cbOne.isChecked = true
                    binding.cbTwo.isChecked = false
                    binding.cbThree.isChecked = false
                    binding.cbFour.isChecked = false
                    cancelId = "1"
                    binding.edtCancelBox.visibility = View.GONE
                    binding.edtCancelBox.setText("")
                }

                /*This click event is option two select  */
                binding.cbTwo.setOnClickListener {
                    binding.cbOne.isChecked = false
                    binding.cbTwo.isChecked = true
                    binding.cbThree.isChecked = false
                    binding.cbFour.isChecked = false
                    cancelId = "2"
                    binding.edtCancelBox.visibility = View.GONE
                    binding.edtCancelBox.setText("")
                }

                /*This click event is option three select  */
                binding.cbThree.setOnClickListener {
                    binding.cbOne.isChecked = false
                    binding.cbTwo.isChecked = false
                    binding.cbThree.isChecked = true
                    binding.cbFour.isChecked = false
                    cancelId = "3"
                    binding.edtCancelBox.visibility = View.GONE
                    binding.edtCancelBox.setText("")
                }

                /*This click event is option four select  */
                binding.cbFour.setOnClickListener {
                    binding.cbOne.isChecked = false
                    binding.cbTwo.isChecked = false
                    binding.cbThree.isChecked = false
                    binding.cbFour.isChecked = true
                    cancelId = "4"
                    binding.edtCancelBox.visibility = View.VISIBLE
                }
                /*This click event is called when going to cancel subscription  */
                binding.btnCancelSubscrible.setOnClickListener {
                    myBackPress = true
                    if (BWSApplication.player != null) {
                        if (BWSApplication.player.playWhenReady) {
                            BWSApplication.player.playWhenReady = false
                            audioPause = true
                        }
                    }
                    if (cancelId.equals("4", ignoreCase = true) && binding.edtCancelBox.text.toString().equals("", ignoreCase = true)) {
                        BWSApplication.showToast("Cancellation reason is required", activity)
                    } else {/*This dialog is cancel membership  */
                        val dialog = Dialog(ctx)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.cancel_membership)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(ctx, R.color.dark_blue_gray)))
                        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                        val btn = dialog.findViewById<Button>(R.id.Btn)
                        dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss()
                                if (BWSApplication.player != null) {
                                    if (BWSApplication.player.playWhenReady) {
                                        BWSApplication.player.playWhenReady = false
                                        audioPause = true
                                    }
                                }
                                return@setOnKeyListener true
                            }
                            false
                        }

                        /* This click event is called when cancelling subscription */
                        btn.setOnTouchListener { view1: View, event: MotionEvent ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    val views = view1 as Button
                                    views.background.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                                    view1.invalidate()
                                }
                                MotionEvent.ACTION_UP -> {
                                    if (BWSApplication.isNetworkConnected(ctx)) {
                                        val listCall = APINewClient.client.getCancelPlan(mainAccountId, cancelId, binding.edtCancelBox.text.toString())
                                        if (listCall != null) {
                                            listCall.enqueue(object : Callback<CancelPlanModel?> {
                                                override fun onResponse(call: Call<CancelPlanModel?>, response: Response<CancelPlanModel?>) {
                                                    try {
                                                        val model = response.body()
                                                        BWSApplication.showToast(model!!.responseMessage, activity)
                                                        dialog.dismiss()

                                                        //                                            Properties p = new Properties();
                                                        //                                            p.putValue("cancelId", cancelId);
                                                        //                                            p.putValue("cancelReason", CancelReason);
                                                        //                                            BWSApplication.addToSegment("Cancel Subscription Clicked", p, CONSTANTS.track);

                                                        if (BWSApplication.player != null) {
                                                            if (BWSApplication.player.playWhenReady) {
                                                                BWSApplication.player.playWhenReady = false
                                                                audioPause = true
                                                            }
                                                        }
                                                        finish()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }

                                                override fun onFailure(call: Call<CancelPlanModel?>, t: Throwable) {
                                                }
                                            })
                                        }
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

                        /* This click event is called when not cancelling subscription */
                        tvGoBack.setOnClickListener {
                            dialog.dismiss()
                            if (BWSApplication.player != null) {
                                if (BWSApplication.player.playWhenReady) {
                                    BWSApplication.player.playWhenReady = false
                                    audioPause = true
                                }
                            }
                        }
                        dialog.show()
                        dialog.setCancelable(false)
                    }
                }
            }
        }
    }

    /* This is the device back click event */
    override fun onBackPressed() {
        if (screenView.equals("0")) {
            finish()
        } else {
            myBackPress = true
            if (audioPause) {
                BWSApplication.player.playWhenReady = true
            }
            finish()
        }
    }

    /* This fuction is youtube video sucessfully playing */
    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        if (screenView.equals("0")) {

        } else {
            if (!wasRestored) {
                youTubePlayer.loadVideo(VIDEO_ID)
                youTubePlayer.setShowFullscreenButton(true)
            }
        }
    }

    /* This fuction is youtube video can't play and generating error  */
    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        if (screenView.equals("0")) {

        } else {
            if (errorReason.isUserRecoverableError) {
                errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
            } else {
                val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
                BWSApplication.showToast(errorMessage, activity)
            }
        }
    }

    /* This is the initialize youtube player view */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (screenView.equals("0")) {

        } else {
            if (requestCode == RECOVERY_DIALOG_REQUEST) {
                youTubePlayerProvider.initialize(API_KEY, this)
            }
        }
    }

    /* This is the get youtube player view */
    private val youTubePlayerProvider: YouTubePlayer.Provider
        get() = binding.youtubeView

    /* This class is check about application in background or foreground */
    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND")
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
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Restored")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BWSApplication.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    private fun deleteCall() {
        val preferences = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        val edit = preferences.edit()
        edit.remove(CONSTANTS.PREFE_ACCESS_mainAccountID)
        edit.remove(CONSTANTS.PREFE_ACCESS_UserId)
        edit.remove(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER)
        edit.remove(CONSTANTS.PREFE_ACCESS_NAME)
        edit.remove(CONSTANTS.PREFE_ACCESS_USEREMAIL)
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceType)
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceID)
        edit.clear()
        edit.apply()
        val preferred = getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        val edited = preferred.edit()
        edited.remove(CONSTANTS.selectedCategoriesTitle)
        edited.remove(CONSTANTS.selectedCategoriesName)
        edited.remove(CONSTANTS.PREFE_ACCESS_SLEEPTIME)
        edited.clear()
        edited.apply()
        val preferred1 = getSharedPreferences(CONSTANTS.AssMain, Context.MODE_PRIVATE)
        val edited1 = preferred1.edit()
        edited1.remove(CONSTANTS.AssQus)
        edited1.remove(CONSTANTS.AssAns)
        edited1.remove(CONSTANTS.AssSort)
        edited1.clear()
        edited1.apply()
        /*   val shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGOUT, Context.MODE_PRIVATE)
           val editorcv = shared.edit()
           editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_UserID, userId)
           editorcv.putString(CONSTANTS.PREF_KEY_LOGOUT_CoUserID, coUserId)
           editorcv.apply()*/
        val preferred2 = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val edited2 = preferred2.edit()
        edited2.remove(CONSTANTS.PREF_KEY_MainAudioList)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerAudioList)
        edited2.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPlaylistId)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPlaylistName)
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPosition)
        edited2.remove(CONSTANTS.PREF_KEY_Cat_Name)
        edited2.remove(CONSTANTS.PREF_KEY_PlayFrom)
        edited2.clear()
        edited2.apply()
        BWSApplication.logout = true
        BWSApplication.deleteCache(activity)
    }

    /* This is object declaration */
    companion object {
        const val API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ"
        const val VIDEO_ID = "y1rfRW6WX08"
        const val RECOVERY_DIALOG_REQUEST = 1
    }
}