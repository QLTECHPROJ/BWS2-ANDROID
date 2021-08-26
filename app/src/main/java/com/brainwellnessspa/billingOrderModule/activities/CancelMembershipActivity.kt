package com.brainwellnessspa.billingOrderModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication.*
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
    lateinit var activity: Activity
    var audioPause = false
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    var screenView: String? = ""
    var planDeviceType: String? = ""

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
        planDeviceType = shared1.getString(CONSTANTS.PREFE_ACCESS_PlanDeviceType, "")

        if (intent.extras != null) {
            screenView = intent.getStringExtra("screenView")
        }

        deleteId = "1"
        cancelId = "1"

        val measureRatio = measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)
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
                val p = Properties()
                addToSegment("Delete Account Screen Viewed", p, CONSTANTS.screen)
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
                        showToast("Cancellation reason is required", activity)
                    } else {/*This dialog is cancel membership  */
                        val dialog = Dialog(ctx)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.cancel_membership)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.transparent_white)))
                        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                        val tvSubTitle = dialog.findViewById<TextView>(R.id.tvSubTitle)
                        val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                        tvTitle.text = getString(R.string.delete_account_popup_title)
                        tvSubTitle.text = getString(R.string.delete_account_popup_subtitle)
                        val btn = dialog.findViewById<Button>(R.id.Btn)
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

                        /* This click event is called when cancelling subscription */
                        btn.setOnTouchListener { view1: View, event: MotionEvent ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    val views = view1 as Button
                                    views.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(0x77000000, BlendModeCompat.SRC_ATOP)
                                    view1.invalidate()
                                }
                                MotionEvent.ACTION_UP -> {
                                    if (isNetworkConnected(ctx)) {
                                        val listCall = APINewClient.client.getDeleteAccount(userId, deleteId, binding.edtCancelBoxAc.text.toString())
                                        listCall?.enqueue(object : Callback<DeleteInviteUserModel?> {
                                            override fun onResponse(call: Call<DeleteInviteUserModel?>, response: Response<DeleteInviteUserModel?>) {
                                                try {
                                                    val listModel = response.body()
                                                    if (listModel != null) {
                                                        when {
                                                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                                                dialog.dismiss()
                                                                deleteCall(activity)
                                                                showToast(listModel.responseMessage, activity)
                                                                val properties = Properties()
                                                                when (deleteId) {
                                                                    "1" -> {
                                                                        properties.putValue("reason", binding.cbOneAc.text.toString())
                                                                        properties.putValue("comments", binding.edtCancelBoxAc.text.toString())
                                                                    }
                                                                    "2" -> {
                                                                        properties.putValue("reason", binding.cbTwoAc.text.toString())
                                                                        properties.putValue("comments", binding.edtCancelBoxAc.text.toString())
                                                                    }
                                                                    "3" -> {
                                                                        properties.putValue("reason", binding.cbThreeAc.text.toString())
                                                                        properties.putValue("comments", binding.edtCancelBoxAc.text.toString())
                                                                    }
                                                                    "4" -> {
                                                                        properties.putValue("reason", binding.cbFourAc.text.toString())
                                                                        properties.putValue("comments", binding.edtCancelBoxAc.text.toString())
                                                                    }
                                                                }
                                                                addToSegment("Account Deleted", properties, CONSTANTS.track)
                                                                val i = Intent(activity, SignInActivity::class.java)
                                                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                                                i.putExtra("mobileNo", "")
                                                                i.putExtra("countryCode", "")
                                                                i.putExtra("name", "")
                                                                i.putExtra("email", "")
                                                                i.putExtra("countryShortName", "")
                                                                startActivity(i)
                                                                finish()
                                                            }
                                                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                                                deleteCall(activity)
                                                                showToast(listModel.responseMessage, activity)
                                                                val i = Intent(activity, SignInActivity::class.java)
                                                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                                                i.putExtra("mobileNo", "")
                                                                i.putExtra("countryCode", "")
                                                                i.putExtra("name", "")
                                                                i.putExtra("email", "")
                                                                i.putExtra("countryShortName", "")
                                                                startActivity(i)
                                                                finish()
                                                            }
                                                            else -> {
                                                                showToast(listModel.responseMessage, activity)
                                                            }
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }

                                            override fun onFailure(call: Call<DeleteInviteUserModel?>, t: Throwable) {
                                            }
                                        })

                                    } else {
                                        showToast(getString(R.string.no_server_found), activity)
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
                        player.playWhenReady = true
                    }
                    finish()
                }

                /*This is to youtube video playing */
                binding.youtubeView.initialize(API_KEY, this)
                val p = Properties()
                p.putValue("plan", intent.getStringExtra("plan"))
                p.putValue("planStatus",  intent.getStringExtra("planStatus"))
                p.putValue("planStartDt",  intent.getStringExtra("planStartDt"))
                p.putValue("planExpiryDt",  intent.getStringExtra("planExpiryDt"))
                p.putValue("planAmount",  intent.getStringExtra("planAmount"))
                addToSegment("Cancel Subscription Screen Viewed", p, CONSTANTS.screen)

                /*This condition is to audio playing or not  */
                if (player != null) {
                    if (player.playWhenReady) {
                        player.playWhenReady = false
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
                    if (player != null) {
                        if (player.playWhenReady) {
                            player.playWhenReady = false
                            audioPause = true
                        }
                    }
                    if (cancelId.equals("4", ignoreCase = true) && binding.edtCancelBox.text.toString().equals("", ignoreCase = true)) {
                        showToast("Cancellation reason is required", activity)
                    } else {/*This dialog is cancel membership  */
                        if (planDeviceType == "1") {
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

                            /* This click event is called when cancelling subscription */
                            btn.setOnTouchListener { view1: View, event: MotionEvent ->
                                when (event.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        val views = view1 as Button
                                        views.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(0x77000000, BlendModeCompat.SRC_ATOP)

                                        view1.invalidate()
                                    }
                                    MotionEvent.ACTION_UP -> {
                                        if (isNetworkConnected(ctx)) {
                                            APINewClient.client.getCancelPlan(userId, cancelId, binding.edtCancelBox.text.toString())?.enqueue(object : Callback<CancelPlanModel?> {
                                                override fun onResponse(call: Call<CancelPlanModel?>, response: Response<CancelPlanModel?>) {
                                                    try {
                                                        val model = response.body()
                                                        showToast(model!!.responseMessage, activity)
                                                        try {
                                                            val p = Properties()
                                                            p.putValue("cancelId",cancelId)
                                                            p.putValue("cancelReason", binding.edtCancelBox.text.toString())
                                                            p.putValue("plan", intent.getStringExtra("plan"))
                                                            p.putValue("planStatus",  intent.getStringExtra("planStatus"))
                                                            p.putValue("planStartDt",  intent.getStringExtra("planStartDt"))
                                                            p.putValue("planExpiryDt",  intent.getStringExtra("planExpiryDt"))
                                                            p.putValue("planAmount",  intent.getStringExtra("planAmount"))
                                                            addToSegment("Subscription Cancelled", p, CONSTANTS.screen)
                                                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?sku=weekly_2_profile&package=com.brainwellnessspa")))
//                                                        https://play.google.com/store/account/subscriptions
                                                        } catch (e: ActivityNotFoundException) {
                                                            showToast("Cant open the browser", activity)
                                                            e.printStackTrace()
                                                        }
                                                        dialog.dismiss()
                                                        finish()
                                                        if (player != null) {
                                                            if (player.playWhenReady) {
                                                                player.playWhenReady = false
                                                                audioPause = true
                                                            }
                                                        }

                                                        //                                            Properties p = new Properties();
                                                        //                                            p.putValue("cancelId", cancelId);
                                                        //                                            p.putValue("cancelReason", CancelReason);
                                                        //                                            BWSApplication.addToSegment("Cancel Subscription Clicked", p, CONSTANTS.track);

                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }

                                                override fun onFailure(call: Call<CancelPlanModel?>, t: Throwable) {
                                                }
                                            })
                                        } else {
                                            showToast(getString(R.string.no_server_found), activity)
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
                                if (player != null) {
                                    if (player.playWhenReady) {
                                        player.playWhenReady = false
                                        audioPause = true
                                    }
                                }
                            }
                            dialog.show()
                            dialog.setCancelable(false)
                        }else {
                            val dialog = Dialog(ctx)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setContentView(R.layout.cancel_membership)
                            dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.transparent_white)))
                            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                            val tvSubTitle = dialog.findViewById<TextView>(R.id.tvSubTitle)
                            val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                            tvTitle.text = "You can cancel the plan in your ios account."
                            tvSubTitle.text = getString(R.string.delete_account_popup_subtitle)
                            val btn = dialog.findViewById<Button>(R.id.Btn)
                            btn.text = getString(R.string.ok)
                            tvGoBack.visibility = View.GONE
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

                            /* This click event is called when cancelling subscription */
                            btn.setOnTouchListener { view1: View, event: MotionEvent ->
                                when (event.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        val views = view1 as Button
                                        views.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(0x77000000, BlendModeCompat.SRC_ATOP)
                                        view1.invalidate()
                                    }
                                    MotionEvent.ACTION_UP -> {
                                        dialog.dismiss()
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

                            dialog.show()
                            dialog.setCancelable(false)
                        }
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
                player.playWhenReady = true
            }
            finish()
        }
    }

    /* This fuction is youtube video sucessfully playing */
    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        if (!screenView.equals("0")) {
            if (!wasRestored) {
                youTubePlayer.loadVideo(VIDEO_ID)
                youTubePlayer.setShowFullscreenButton(true)
            }
        }
    }

    /* This fuction is youtube video can't play and generating error  */
    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        if (!screenView.equals("0")) {
            if (errorReason.isUserRecoverableError) {
                errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
            } else {
                val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
                showToast(errorMessage, activity)
            }
        }
    }

    /* This is the initialize youtube player view */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (!screenView.equals("0")) {
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
                notificationManager.cancel(notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    /* This is object declaration */
    companion object {
        const val API_KEY = "AIzaSyCzqUwQUD58tA8wrINDc1OnL0RgcU52jzQ"
        const val VIDEO_ID = "y1rfRW6WX08"
        const val RECOVERY_DIALOG_REQUEST = 1
    }
}