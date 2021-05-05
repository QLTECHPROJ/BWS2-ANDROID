package com.brainwellnessspa.ReferralModule.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.DashboardModule.Appointment.SessionsFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReferralModule.Model.ContactlistModel;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.WebView.TncActivity;
import com.brainwellnessspa.databinding.ActivityReferFriendBinding;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class ReferFriendActivity extends AppCompatActivity {
    ActivityReferFriendBinding binding;
    Context ctx;
    Activity activity;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 90;
    String UserPromocode = "", ReferLink = "", UserID;
    Properties p;
    ArrayList<ContactlistModel> userList = new ArrayList<>();
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_friend);
        ctx = ReferFriendActivity.this;
        activity = ReferFriendActivity.this;
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shareded = getSharedPreferences(CONSTANTS.PREF_KEY_Referral, Context.MODE_PRIVATE);
        UserPromocode = (shareded.getString(CONSTANTS.PREF_KEY_UserPromocode, ""));
        ReferLink = (shareded.getString(CONSTANTS.PREF_KEY_ReferLink, ""));
        p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("referLink", ReferLink);
        p.putValue("userReferCode", UserPromocode);
        BWSApplication.addToSegment("Refer A Friend Screen Viewed", p, CONSTANTS.screen);
        binding.tvTitle.setText(getString(R.string.refer_title));
        binding.tvDesc.setText(getString(R.string.refer_desc));
        binding.tvInviteTitle.setText(getString(R.string.refer_invite_title));
        binding.tvInviteRules.setText(getString(R.string.refer_invite_rules_click));
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                5, 3, 1f, 40);
        binding.ivReferImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivReferImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivReferImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivReferImage.setImageResource(R.drawable.refer_friend_banner);
        binding.tvCodeCopy.setText(UserPromocode);
        binding.llBack.setOnClickListener(v -> {

            ComeScreenAccount = 1;
            myBackPress = true;
            finish();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        if (UserPromocode.equalsIgnoreCase("")) {
            binding.tvCodeCopy.setVisibility(View.INVISIBLE);
        } else {
            binding.tvCodeCopy.setVisibility(View.VISIBLE);
        }

        binding.btnReferred.setOnClickListener(v -> {

            myBackPress = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(ReferFriendActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(ReferFriendActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReferFriendActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                  /*  Intent intent = new Intent();
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

                    myBackPress = true;
                    // Permission has already been granted
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
                alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkorange));*/
                 /*   Fragment sessionsFragment = new ContactBookFragment();
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flfriend, sessionsFragment).commit();*/
                    Intent i = new Intent(ReferFriendActivity.this, ContactBookActivity.class);
                    startActivity(i);
                }
            }
        });

        binding.llCodeCopy.setOnClickListener(v -> {
            if (UserPromocode.equalsIgnoreCase("")) {
                BWSApplication.showToast(getString(R.string.not_available), activity);
            } else {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", binding.tvCodeCopy.getText());
                if (manager != null) {
                    manager.setPrimaryClip(clipData);
                }
                BWSApplication.showToast("Promo Code Copied", activity);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("userReferCode", UserPromocode);
                BWSApplication.addToSegment("Promo Code Copied", p, CONSTANTS.track);
            }
        });

        binding.tvInviteRules.setOnClickListener(v -> {
            Intent i = new Intent(ctx, TncActivity.class);
            i.putExtra(CONSTANTS.Web, "HowReferWorks");
            startActivity(i);
            p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("invitationRuleUrl", "");
            BWSApplication.addToSegment("Invitation Rules Viewed", p, CONSTANTS.screen);
        });

        binding.llShare.setOnClickListener(v -> {
            if (ReferLink.equalsIgnoreCase("")) {
                BWSApplication.showToast(getString(R.string.not_available), activity);
            } else {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, I am loving using the Brain Wellness App. You can develop yourself " +
                        "in the comfort of your home while you sleep and gain access to over 75 audio programs helping you " +
                        "to live inspired and improve your mental wellbeing. I would like to invite you to try it. " +
                        "Sign up using the link and get 30 days free trial\n" + ReferLink);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("referLink", ReferLink);
                p.putValue("userReferCode", UserPromocode);
                p.putValue("shareText", "Hey, I am loving using the Brain Wellness App. You can develop yourself " +
                        "in the comfort of your home while you sleep and gain access to over 75 audio programs helping you " +
                        "to live inspired and improve your mental wellbeing. I would like to invite you to try it. " +
                        "Sign up using the link and get 30 days free trial\n" + ReferLink);
                BWSApplication.addToSegment("Share Clicked", p, CONSTANTS.track);
            }
        });
    }

    @Override
    public void onBackPressed() {

        ComeScreenAccount = 1;
        myBackPress = true;
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*Fragment sessionsFragment = new ContactBookFragment();
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flfriend, sessionsFragment).commit();*/

                    myBackPress = true;
                    Intent i = new Intent(ReferFriendActivity.this, ContactBookActivity.class);
                    startActivity(i);
                } else {
                    myBackPress = true;
                    AlertDialog.Builder buildermain = new AlertDialog.Builder(ctx);
                    buildermain.setMessage("Please Allow Contact Permission");
                    buildermain.setCancelable(true);
                    buildermain.setPositiveButton(
                            getString(R.string.ok),
                            (dialogmain, id1) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialogmain.dismiss();
                            });
                    AlertDialog alert11 = buildermain.create();
                    alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                    alert11.show();
                    alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                }
                return;
            }
        }
    }


    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                if(!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                }else{
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            }else{
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }
}