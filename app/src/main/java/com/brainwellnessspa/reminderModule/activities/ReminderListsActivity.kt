package com.brainwellnessspa.reminderModule.activities

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.IsLock
import com.brainwellnessspa.BWSApplication.callEnhanceActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityReminderDetailsBinding
import com.brainwellnessspa.databinding.RemiderDetailsLayoutBinding
import com.brainwellnessspa.reminderModule.models.DeleteRemiderModel
import com.brainwellnessspa.reminderModule.models.ReminderListModel
import com.brainwellnessspa.reminderModule.models.ReminderStatusModel
import com.brainwellnessspa.reminderModule.models.SegmentReminder
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.gson.Gson
import com.segment.analytics.Properties
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import me.toptas.fancyshowcase.listener.OnViewInflateListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/* This is the reminder set listing activity */

class ReminderListsActivity : AppCompatActivity() {
    lateinit var binding: ActivityReminderDetailsBinding
    var userId: String? = ""
    var coUserId: String? = ""
    var reminderFirstLogin: String? = "0"
    lateinit var ctx: Context
    lateinit var activity: Activity
    var remiderIds = ArrayList<String?>()
    var adapter: RemiderDetailsAdapter? = null
    var fancyShowCaseView1: FancyShowCaseView? = null
    var fancyShowCaseView2: FancyShowCaseView? = null
    private var queue: FancyShowCaseQueue? = null
    var listReminderModel: ReminderListModel? = null
    var p: Properties? = null
    private var numStarted = 0
    var stackStatus = 0
    var myBackPress = false
    var notificationStatus = false
    private val listener1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyReminder")) {
                prepareData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder_details)
        ctx = this@ReminderListsActivity
        activity = this@ReminderListsActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.rvReminderDetails.layoutManager = mLayoutManager
        binding.rvReminderDetails.itemAnimator = DefaultItemAnimator()
        notificationStatus = false

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(binding.rvReminderDetails);

        binding.llBack.setOnClickListener {
            myBackPress = true
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }/*   binding.btnAddReminder.setOnClickListener(view -> {
            notificationStatus = true;
            myBackPress = false;
            if (BWSApplication.isNetworkConnected(ctx)) {
                Intent i = new Intent(ctx, ReminderActivity.class);
                i.putExtra("ComeFrom", "");
                i.putExtra("ReminderId", "");
                i.putExtra("PlaylistID", "");
                i.putExtra("PlaylistName", "");
                i.putExtra("Time", "");
                i.putExtra("Day", "");
                i.putExtra("ReminderDay", "");
                i.putExtra("IsCheck", "");
                startActivity(i);
                finish();
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
            }
        });*/
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    override fun onBackPressed() {
        myBackPress = true
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener1)
        finish()
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getReminderList(coUserId)
            listCall.enqueue(object : Callback<ReminderListModel?> {
                override fun onResponse(call: Call<ReminderListModel?>, response: Response<ReminderListModel?>) {
                    try {
                        val listModel = response.body()
                        listReminderModel = listModel
                        if (listModel != null) {
                            if (listModel.responseCode.equals(ctx.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                adapter = RemiderDetailsAdapter(listModel.responseData)
                                binding.rvReminderDetails.adapter = adapter
                                binding.btnAddReminder.visibility = View.GONE
                                showTooltips()
                                LocalBroadcastManager.getInstance(ctx).registerReceiver(listener1, IntentFilter("Reminder"))
                                p = Properties()
                                if (listModel.responseData!!.isEmpty()) {
                                    binding.llError.visibility = View.VISIBLE
                                    binding.rvReminderDetails.visibility = View.GONE
                                    p!!.putValue("reminders ", "")
                                } else {
                                    binding.llError.visibility = View.GONE
                                    binding.rvReminderDetails.visibility = View.VISIBLE
                                    val section1 = ArrayList<SegmentReminder>()
                                    val e = SegmentReminder()
                                    val gson = Gson()
                                    for (i in listModel.responseData!!.indices) {
                                        e.reminderId = listModel.responseData!![i]!!.reminderId
                                        e.playlistId = listModel.responseData!![i]!!.playlistId
                                        e.playlistName = listModel.responseData!![i]!!.playlistName
                                        if (listModel.responseData!![i]!!.created.equals("1")) {
                                            e.playlistType = "created"
                                        } else if (listModel.responseData!![i]!!.created.equals("0")) {
                                            e.playlistType = "Default"
                                        } else if (listModel.responseData!![i]!!.created.equals("2")) {
                                            e.playlistType = "Suggested"
                                        }
                                        if (listModel.responseData!![i]!!.isCheck.equals("1", ignoreCase = true)) {
                                            e.reminderStatus = "on"
                                        } else {
                                            e.reminderStatus = "off"
                                        }
                                        e.reminderTime = listModel.responseData!![i]!!.reminderTime
                                        e.reminderDay = listModel.responseData!![i]!!.reminderDay
                                        section1.add(e)
                                    }
                                    p!!.putValue("reminders ", gson.toJson(section1))
                                }
                                BWSApplication.addToSegment("Reminder Screen Viewed", p, CONSTANTS.screen)
                                if (remiderIds.size == 0) {
                                    binding.llSelectAll.visibility = View.GONE
                                    binding.btnAddReminder.visibility = View.GONE
                                    binding.btnDeleteReminder.visibility = View.GONE
                                } else {
                                    binding.llSelectAll.visibility = View.VISIBLE
                                    binding.btnAddReminder.visibility = View.GONE
                                    binding.btnDeleteReminder.visibility = View.VISIBLE
                                }
                            } else if (listModel.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                BWSApplication.deleteCall(activity)
                                BWSApplication.showToast(listModel.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ReminderListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
        binding.btnDeleteReminder.setOnClickListener {
            notificationStatus = true
            myBackPress = false
            val dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.delete_reminder)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.dark_blue_gray)))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
            val tvconfirm = dialog.findViewById<RelativeLayout>(R.id.tvconfirm)
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            tvconfirm.setOnClickListener {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    val listCall = APINewClient.client.getDeleteRemider(coUserId, TextUtils.join(",", remiderIds))
                    listCall.enqueue(object : Callback<DeleteRemiderModel?> {
                        override fun onResponse(call: Call<DeleteRemiderModel?>, response: Response<DeleteRemiderModel?>) {
                            try {
                                val model = response.body()
                                if (model!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                    remiderIds.clear()
                                    BWSApplication.showToast(model.responseMessage, activity)
                                    dialog.dismiss()
                                    prepareData()
                                } else if (model.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                    BWSApplication.deleteCall(activity)
                                    BWSApplication.showToast(model.responseMessage, activity)
                                    val i = Intent(activity, SignInActivity::class.java)
                                    i.putExtra("mobileNo", "")
                                    i.putExtra("countryCode", "")
                                    i.putExtra("name", "")
                                    i.putExtra("email", "")
                                    i.putExtra("countryShortName", "")
                                    startActivity(i)
                                    finish()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<DeleteRemiderModel?>, t: Throwable) {}
                    })
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), activity)
                }
            }
            tvGoBack.setOnClickListener { dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(false)
        }
    }

    private fun showTooltips() {
        val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        reminderFirstLogin = shared1.getString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "0")
        if (reminderFirstLogin.equals("1", ignoreCase = true)) {
            val enterAnimation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_top)
            val exitAnimation = AnimationUtils.loadAnimation(ctx, R.anim.slide_out_bottom)
            fancyShowCaseView1 = FancyShowCaseView.Builder(activity).customView(R.layout.layout_reminder_status, object : OnViewInflateListener {
                override fun onViewInflated(view: View) {
                    val rlNext = view.findViewById<RelativeLayout>(R.id.rlNext)
                    val ivLibraryImage = view.findViewById<ImageView>(R.id.ivLibraryImage)
                    val anim = ValueAnimator.ofFloat(0.9f, 1f)
                    anim.duration = 1500
                    anim.addUpdateListener { animation: ValueAnimator ->
                        ivLibraryImage.scaleX = (animation.animatedValue as Float)
                        ivLibraryImage.scaleY = (animation.animatedValue as Float)
                    }
                    anim.repeatCount = ValueAnimator.INFINITE
                    anim.repeatMode = ValueAnimator.REVERSE
                    anim.start()
                    rlNext.setOnClickListener { fancyShowCaseView1!!.hide() }
                }
            }).focusShape(FocusShape.ROUNDED_RECTANGLE).enterAnimation(enterAnimation).exitAnimation(exitAnimation).closeOnTouch(false).build()
            fancyShowCaseView2 = FancyShowCaseView.Builder(activity).customView(R.layout.layout_reminder_remove, object : OnViewInflateListener {
                override fun onViewInflated(view: View) {
                    val rlDone = view.findViewById<RelativeLayout>(R.id.rlDone)
                    val ivLibraryImage = view.findViewById<ImageView>(R.id.ivLibraryImage)
                    val anim = ValueAnimator.ofFloat(0.9f, 1f)
                    anim.duration = 1500
                    anim.addUpdateListener { animation: ValueAnimator ->
                        ivLibraryImage.scaleX = (animation.animatedValue as Float)
                        ivLibraryImage.scaleY = (animation.animatedValue as Float)
                    }
                    anim.repeatCount = ValueAnimator.INFINITE
                    anim.repeatMode = ValueAnimator.REVERSE
                    anim.start()
                    rlDone.setOnClickListener {
                        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "0")
                        editor.apply()
                        fancyShowCaseView2!!.hide()
                    }
                }
            }).focusShape(FocusShape.ROUNDED_RECTANGLE).enterAnimation(enterAnimation).exitAnimation(exitAnimation).closeOnTouch(false).build()
            queue = FancyShowCaseQueue().add(fancyShowCaseView1!!).add(fancyShowCaseView2!!)
            queue!!.show()
        }
    }

    inner class RemiderDetailsAdapter(private val model: List<ReminderListModel.ResponseData?>?) : RecyclerView.Adapter<RemiderDetailsAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: RemiderDetailsLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.remider_details_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind.tvName.text = model!![position]!!.playlistName
            holder.bind.tvDate.text = model[position]!!.reminderDay
            holder.bind.tvTime.text = model[position]!!.reminderTime
            holder.bind.view.isClickable = false
            holder.bind.view.isEnabled = false
            holder.bind.cbChecked.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
                if (holder.bind.cbChecked.isChecked) { //                    notifyDataSetChanged();
                    if (!remiderIds.contains(model[position]!!.reminderId)) {
                        remiderIds.add(model[position]!!.reminderId)
                        binding.tvSelectAll.text = remiderIds.size.toString() + " selected"
                    }
                } else {
                    remiderIds.remove(model[position]!!.reminderId)
                    binding.tvSelectAll.text = remiderIds.size.toString() + " selected"
                }
                if (remiderIds.size == 0) {
                    binding.llSelectAll.visibility = View.GONE
                    binding.btnAddReminder.visibility = View.GONE
                    binding.btnDeleteReminder.visibility = View.GONE
                } else {
                    binding.llSelectAll.visibility = View.VISIBLE
                    binding.btnAddReminder.visibility = View.GONE
                    binding.btnDeleteReminder.visibility = View.VISIBLE
                }
                if (remiderIds.size == model.size) {
                    binding.cbChecked.isChecked = true
                    binding.tvSelectAll.text = remiderIds.size.toString() + " selected"
                }
            }
            binding.llClose.setOnClickListener {
                remiderIds.clear()
                binding.llSelectAll.visibility = View.GONE
                binding.cbChecked.isChecked = false
                notifyDataSetChanged()
            }
            binding.cbChecked.setOnClickListener {
                if (binding.cbChecked.isChecked) {
                    remiderIds.clear()
                    for (i in model.indices) {
                        remiderIds.add(model[i]!!.reminderId)
                    }
                } else {
                    binding.llSelectAll.visibility = View.GONE
                    remiderIds.clear()
                } //                Log.e("remiderIds", TextUtils.join(",", remiderIds));
                notifyDataSetChanged()
            }
            if (remiderIds.contains(model[position]!!.reminderId)) {
                holder.bind.cbChecked.isChecked = true
                binding.tvSelectAll.text = remiderIds.size.toString() + " selected"
            } else {
                holder.bind.cbChecked.isChecked = false
                binding.tvSelectAll.text = remiderIds.size.toString() + " selected"
            }
            if (remiderIds.size == model.size) {
                binding.cbChecked.isChecked = true
                binding.tvSelectAll.text = remiderIds.size.toString() + " selected"
            }
            holder.bind.switchStatus.isChecked = model[position]!!.isCheck.equals("1", ignoreCase = true)

            if (IsLock.equals("1")) {
                holder.bind.switchStatus.isClickable = false
                holder.bind.switchStatus.isEnabled = false
                holder.bind.llSwitchStatus.isClickable = true
                holder.bind.llSwitchStatus.isEnabled = true
                holder.bind.llSwitchStatus.setOnClickListener {
                    callEnhanceActivity(ctx)
                }
            } else if (IsLock.equals("0")) {
                holder.bind.switchStatus.isClickable = true
                holder.bind.switchStatus.isEnabled = true
                holder.bind.llSwitchStatus.isClickable = false
                holder.bind.llSwitchStatus.isEnabled = false
                holder.bind.switchStatus.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
                    if (checked) {
                        val areNotificationEnabled = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
                        Log.e("areNotificationEnabled", areNotificationEnabled.toString())
                        if (!areNotificationEnabled) {
                            val dialogNotification = Dialog(ctx)
                            dialogNotification.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialogNotification.setContentView(R.layout.custom_popup_layout)
                            dialogNotification.window!!.setBackgroundDrawable(ColorDrawable(ctx.resources.getColor(R.color.dark_blue_gray)))
                            dialogNotification.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            val tvGoBack = dialogNotification.findViewById<TextView>(R.id.tvGoBack)
                            val tvHeader = dialogNotification.findViewById<TextView>(R.id.tvHeader)
                            val tvTitle1 = dialogNotification.findViewById<TextView>(R.id.tvTitle)
                            val btn = dialogNotification.findViewById<Button>(R.id.Btn)
                            tvTitle1.text = ctx.getString(R.string.unable_to_use_notification_title)
                            tvHeader.text = ctx.getString(R.string.unable_to_use_notification_content)
                            btn.text = "Settings"
                            tvGoBack.text = "Cancel"
                            dialogNotification.setOnKeyListener { dialog1: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialogNotification.dismiss()
                                }
                                false
                            }
                            btn.setOnClickListener { v: View? ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.data = Uri.parse("package:" + ctx.packageName)
                                ctx.startActivity(intent)
                                dialogNotification.dismiss()
                            }
                            tvGoBack.setOnClickListener { v: View? -> dialogNotification.dismiss() }
                            dialogNotification.show()
                            dialogNotification.setCancelable(false)
                        } else prepareSwitchStatus("1", model[position]!!.playlistId, model[position]!!)
                    } else {
                        prepareSwitchStatus("0", model[position]!!.playlistId, model[position]!!)
                    }
                }
                holder.bind.llMainLayout.setOnClickListener {
                    if (IsLock.equals("1")) {
                        callEnhanceActivity(ctx)
                    } else if (IsLock.equals("0")) {
                        if (BWSApplication.isNetworkConnected(activity)) {
                            notificationStatus = true
                            myBackPress = false
                            BWSApplication.getReminderDay(ctx, activity, coUserId, model[position]!!.playlistId, model[position]!!.playlistName, activity as FragmentActivity?, model[position]!!.reminderTime, model[position]!!.rDay, "0", model[position]!!.reminderId, model[position]!!.isCheck, model[position]!!.created)
                        } else {
                            BWSApplication.showToast(getString(R.string.no_server_found), activity)
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return model!!.size
        }

        inner class MyViewHolder(var bind: RemiderDetailsLayoutBinding) : RecyclerView.ViewHolder(bind.root)
    }

    private fun prepareSwitchStatus(reminderStatus: String, PlaylistID: String?, model: ReminderListModel.ResponseData) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getReminderStatus(coUserId, PlaylistID, reminderStatus) /*set 1 or not 0 */
            listCall.enqueue(object : Callback<ReminderStatusModel?> {
                override fun onResponse(call: Call<ReminderStatusModel?>, response: Response<ReminderStatusModel?>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel = response.body()
                        if (listModel != null) {
                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                /*  p = Properties()
                                p!!.putValue("reminderId ", model.reminderId)
                                p!!.putValue("playlistId ", model.reminderId)
                                p!!.putValue("playlistName ", model.reminderId)
                                when {
                                    model.created.equals("1") -> {
                                        p!!.putValue("playlistType ", "Created")
                                    }
                                    model.created.equals("0") -> {
                                        p!!.putValue("playlistType ", "Default")
                                    }
                                    model.created.equals("2") -> {
                                        p!!.putValue("playlistType ", "Suggested")
                                    }
                                }
                                if (model.isCheck.equals("1", ignoreCase = true)) {
                                    p!!.putValue("reminderStatus ","on")
                                } else {
                                    p!!.putValue("reminderStatus ", "off")
                                }
                                p!!.putValue("reminderTime ", model.reminderId)
                                p!!.putValue("reminderDay ", model.reminderId)

                                if(reminderStatus.equals("1"))
                                    BWSApplication.addToSegment("Playlist Reminder Set On", p, CONSTANTS.screen)
                                else
                                    BWSApplication.addToSegment("Playlist Reminder Set Off", p, CONSTANTS.screen)*/
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            } else if (listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                BWSApplication.deleteCall(activity)
                                BWSApplication.showToast(listModel.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ReminderStatusModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
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
                    notificationStatus = false
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
                if (!notificationStatus) {
                    if (BWSApplication.player != null) {
                        Log.e("Destroy", "Activity Destoryed")
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(BWSApplication.notificationId)
                        GlobalInitExoPlayer.relesePlayer(ctx)
                    }
                }
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        @SuppressLint("SetTextI18n")
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            if (direction == ItemTouchHelper.LEFT) {
                val dialog = Dialog(ctx)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.reminder_layout)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.dark_blue_gray)))
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                val tvSubTitle = dialog.findViewById<TextView>(R.id.tvSubTitle)
                val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                val tvconfirm = dialog.findViewById<RelativeLayout>(R.id.tvconfirm)
                tvTitle.text = "Remove Reminder"
                tvSubTitle.text = "Are you sure you want to remove the reminder?"
                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                tvconfirm.setOnClickListener {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        val listCall = APINewClient.client.getDeleteRemider(coUserId, listReminderModel!!.responseData!![position]!!.reminderId)
                        listCall.enqueue(object : Callback<DeleteRemiderModel?> {
                            override fun onResponse(call: Call<DeleteRemiderModel?>, response: Response<DeleteRemiderModel?>) {
                                try {
                                    val model = response.body()
                                    if (model != null) {
                                        if (model.responseCode.equals(ctx.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                            BWSApplication.showToast(model.responseMessage, activity)
                                            prepareData()
                                            dialog.dismiss()
                                        } else if (model.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                            BWSApplication.deleteCall(activity)
                                            BWSApplication.showToast(model.responseMessage, activity)
                                            val i = Intent(activity, SignInActivity::class.java)
                                            i.putExtra("mobileNo", "")
                                            i.putExtra("countryCode", "")
                                            i.putExtra("name", "")
                                            i.putExtra("email", "")
                                            i.putExtra("countryShortName", "")
                                            startActivity(i)
                                            finish()
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onFailure(call: Call<DeleteRemiderModel?>, t: Throwable) {
                            }
                        })
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), activity)
                    }
                }
                tvGoBack.setOnClickListener {
                    dialog.dismiss()
                    prepareData()
                }
                dialog.show()
                dialog.setCancelable(false)
            }
        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val position = viewHolder.absoluteAdapterPosition
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
    }
}