package com.brainwellnessspa.referralModule.activities

import android.Manifest
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityReferFriendBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.userModule.coUserModule.ContactBookActivity
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.webView.TncActivity
import com.segment.analytics.Properties

class ReferFriendActivity : AppCompatActivity() {
    lateinit var binding: ActivityReferFriendBinding
    var ctx: Context? = null
    lateinit var activity: Activity
    private var UserPromocode: String? = ""
    private var ReferLink: String? = ""
    var UserID: String? = ""
    var p: Properties? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_friend)
        ctx = this@ReferFriendActivity
        activity = this@ReferFriendActivity
        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        UserID = shared.getString(CONSTANTS.PREF_KEY_UserID, "")
        val shareded = getSharedPreferences(CONSTANTS.PREF_KEY_Referral, Context.MODE_PRIVATE)
        UserPromocode = shareded.getString(CONSTANTS.PREF_KEY_UserPromocode, "")
        ReferLink = shareded.getString(CONSTANTS.PREF_KEY_ReferLink, "")
        p = Properties()
        p!!.putValue("referLink", ReferLink)
        p!!.putValue("userReferCode", UserPromocode)
//        BWSApplication.addToSegment("Refer A Friend Screen Viewed", p, CONSTANTS.screen)
        binding.tvTitle.text = getString(R.string.refer_title)
        binding.tvDesc.text = getString(R.string.refer_desc)
        binding.tvInviteTitle.text = getString(R.string.refer_invite_title)
        binding.tvInviteRules.text = getString(R.string.refer_invite_rules_click)
        val measureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 40f)
        binding.ivReferImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivReferImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.ivReferImage.scaleType = ImageView.ScaleType.FIT_XY
        binding.ivReferImage.setImageResource(R.drawable.refer_friend_banner)
        binding.tvCodeCopy.text = UserPromocode
        binding.llBack.setOnClickListener {
            myBackPress = true
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        if (UserPromocode.equals("", ignoreCase = true)) {
            binding.tvCodeCopy.visibility = View.INVISIBLE
        } else {
            binding.tvCodeCopy.visibility = View.VISIBLE
        }
        binding.btnReferred.setOnClickListener {
            myBackPress = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this@ReferFriendActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ReferFriendActivity, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@ReferFriendActivity, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), MY_PERMISSIONS_REQUEST_READ_CONTACTS)/*  Intent intent = new Intent();
                    String manufacturer = Build.MANUFACTURER;
                    if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                    } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    }

                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0) {
                        startActivity(intent);
                    }*/
                } else {
                    myBackPress = true // Permission has already been granted
                    /* AlertDialog.Builder buildermain = new AlertDialog.Builder(ReferFriendActivity.this);
                buildermain.setMessage(getString(R.string.opps_msg)+" Please Try After Some Time");
                buildermain.setCancelable(true);
                buildermain.setPositiveButton(
                        getString(R.string.okay),
                        (dialogmain, id1) -> {
                            dialogmain.dismiss();
                        });

                AlertDialog alert11 = buildermain.create();
                alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                alert11.show();
                alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkorange));*//*   Fragment sessionsFragment = new ContactBookFragment();
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flfriend, sessionsFragment).commit();*/
                    val i = Intent(this@ReferFriendActivity, ContactBookActivity::class.java)
                    startActivity(i)
                }
            }
        }
        binding.llCodeCopy.setOnClickListener {
            if (UserPromocode.equals("", ignoreCase = true)) {
                BWSApplication.showToast(getString(R.string.not_available), activity)
            } else {
                val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", binding.tvCodeCopy.text)
                manager.setPrimaryClip(clipData)
                BWSApplication.showToast("Promo Code Copied", activity)
                p = Properties()
                p!!.putValue("userReferCode", UserPromocode)
                BWSApplication.addToSegment("Promo Code Copied", p, CONSTANTS.track)
            }
        }
        binding.tvInviteRules.setOnClickListener {
            val i = Intent(ctx, TncActivity::class.java)
            i.putExtra(CONSTANTS.Web, "HowReferWorks")
            startActivity(i)
            p = Properties()
            p!!.putValue("invitationRuleUrl", "")
//            BWSApplication.addToSegment("Invitation Rules Viewed", p, CONSTANTS.screen)
        }
        binding.llShare.setOnClickListener {
            if (ReferLink.equals("", ignoreCase = true)) {
                BWSApplication.showToast(getString(R.string.not_available), activity)
            } else {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, """
     Hey, I am loving using the Brain Wellness App. You can develop yourself in the comfort of your home while you sleep and gain access to over 75 audio programs helping you to live inspired and improve your mental wellbeing. I would like to invite you to try it. Sign up using the link and get 30 days free trial
     $ReferLink
     """.trimIndent())
                startActivity(Intent.createChooser(shareIntent, "Share via"))
                p = Properties()
                p!!.putValue("referLink", ReferLink)
                p!!.putValue("userReferCode", UserPromocode)
                p!!.putValue("shareText", """
     Hey, I am loving using the Brain Wellness App. You can develop yourself in the comfort of your home while you sleep and gain access to over 75 audio programs helping you to live inspired and improve your mental wellbeing. I would like to invite you to try it. Sign up using the link and get 30 days free trial
     $ReferLink
     """.trimIndent())
//                BWSApplication.addToSegment("Share Clicked", p, CONSTANTS.track)
            }
        }
    }

    override fun onBackPressed() {
        myBackPress = true
        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {/*Fragment sessionsFragment = new ContactBookFragment();
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flfriend, sessionsFragment).commit();*/
                myBackPress = true
                val i = Intent(this@ReferFriendActivity, ContactBookActivity::class.java)
                startActivity(i)
            } else {
                myBackPress = true
                val buildermain = AlertDialog.Builder(ctx!!)
                buildermain.setMessage("Please Allow Contact Permission")
                buildermain.setCancelable(true)
                buildermain.setPositiveButton(getString(R.string.ok)) { dialogs: DialogInterface, _: Int ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    dialogs.dismiss()
                }
                val alert11 = buildermain.create()
                alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
                alert11.show()
                alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.blue))
            }
        }
    }

    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND") //app went to foreground
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
                Log.e("APPLICATION", "App is in BACKGROUND") // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BWSApplication.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 90
    }
}