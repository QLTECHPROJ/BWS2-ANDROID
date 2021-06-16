package com.brainwellnessspa.reminderModule.activities

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.Services.GlobalInitExoPlayer
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityReminderDetailsBinding
import com.brainwellnessspa.databinding.RemiderDetailsLayoutBinding
import com.brainwellnessspa.reminderModule.models.DeleteRemiderModel
import com.brainwellnessspa.reminderModule.models.ReminderListModel
import com.brainwellnessspa.reminderModule.models.ReminderStatusModel
import com.brainwellnessspa.reminderModule.models.SegmentReminder
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

class ReminderDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityReminderDetailsBinding
    var USERID: String? = null
    var CoUSERID: String? = null
    var ReminderFirstLogin: String? = "0"
    var ctx: Context? = null
    lateinit var activity: Activity
    var remiderIds = ArrayList<String?>()
    var adapter: RemiderDetailsAdapter? = null
    var fancyShowCaseView1: FancyShowCaseView? = null
    var fancyShowCaseView2: FancyShowCaseView? = null
    var queue: FancyShowCaseQueue? = null
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
        ctx = this@ReminderDetailsActivity
        activity = this@ReminderDetailsActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        binding.rvReminderDetails.layoutManager = mLayoutManager
        binding.rvReminderDetails.itemAnimator = DefaultItemAnimator()
        notificationStatus = false
        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.rvReminderDetails);*/binding.llBack.setOnClickListener {
            myBackPress = true
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        /*   binding.btnAddReminder.setOnClickListener(view -> {
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
        LocalBroadcastManager.getInstance(ctx!!).unregisterReceiver(listener1)
        finish()
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(
                binding.progressBar,
                binding.progressBarHolder,
                activity
            )
            val listCall = APINewClient.getClient().getReminderList(CoUSERID)
            listCall.enqueue(object : Callback<ReminderListModel?> {
                override fun onResponse(
                    call: Call<ReminderListModel?>,
                    response: Response<ReminderListModel?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listModel = response.body()
                            listReminderModel = listModel
                            adapter = RemiderDetailsAdapter(listModel!!.responseData)
                            binding.rvReminderDetails.adapter = adapter
                            binding.btnAddReminder.visibility = View.GONE
                            showTooltips()
                            LocalBroadcastManager.getInstance(ctx!!)
                                .registerReceiver(listener1, IntentFilter("Reminder"))
                            p = Properties()
                            p!!.putValue("coUserId", CoUSERID)
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
                                    e.playlistType = ""
                                    if (listModel.responseData!![i]!!.isCheck.equals(
                                            "1",
                                            ignoreCase = true
                                        )
                                    ) {
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
                            BWSApplication.addToSegment(
                                "Reminder Screen Viewed",
                                p,
                                CONSTANTS.screen
                            )
                            if (remiderIds.size == 0) {
                                binding.llSelectAll.visibility = View.GONE
                                binding.btnAddReminder.visibility = View.GONE
                                binding.btnDeleteReminder.visibility = View.GONE
                            } else {
                                binding.llSelectAll.visibility = View.VISIBLE
                                binding.btnAddReminder.visibility = View.GONE
                                binding.btnDeleteReminder.visibility = View.VISIBLE
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ReminderListModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
        binding.btnDeleteReminder.setOnClickListener {
            notificationStatus = true
            myBackPress = false
            val dialog = Dialog(ctx!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.delete_reminder)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        activity,
                        R.color.dark_blue_gray
                    )
                )
            )
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
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
                    val listCall = APINewClient.getClient().getDeleteRemider(
                        CoUSERID,
                        TextUtils.join(",", remiderIds)
                    )
                    listCall.enqueue(object : Callback<DeleteRemiderModel?> {
                        override fun onResponse(
                            call: Call<DeleteRemiderModel?>,
                            response: Response<DeleteRemiderModel?>
                        ) {
                            try {
                                val model = response.body()
                                if (model!!.responseCode.equals(
                                        getString(R.string.ResponseCodesuccess),
                                        ignoreCase = true
                                    )
                                ) {
                                    remiderIds.clear()
                                    BWSApplication.showToast(model.responseMessage, activity)
                                    dialog.dismiss()
                                    prepareData()
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
        ReminderFirstLogin = shared1.getString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "0")
        if (ReminderFirstLogin.equals("1", ignoreCase = true)) {
            val enterAnimation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_top)
            val exitAnimation = AnimationUtils.loadAnimation(ctx, R.anim.slide_out_bottom)
            fancyShowCaseView1 = FancyShowCaseView.Builder(activity)
                .customView(R.layout.layout_reminder_status, object : OnViewInflateListener {
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
                }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation).exitAnimation(exitAnimation).closeOnTouch(false)
                .build()
            fancyShowCaseView2 = FancyShowCaseView.Builder(activity)
                .customView(R.layout.layout_reminder_remove, object : OnViewInflateListener {
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
                            val shared =
                                getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "0")
                            editor.apply()
                            fancyShowCaseView2!!.hide()
                        }
                    }
                })
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation).exitAnimation(exitAnimation).closeOnTouch(false)
                .build()
            queue = FancyShowCaseQueue().add(fancyShowCaseView1!!).add(fancyShowCaseView2!!)
            queue!!.show()
        }
    }

    inner class RemiderDetailsAdapter(private val model: List<ReminderListModel.ResponseData?>?) :
        RecyclerView.Adapter<RemiderDetailsAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: RemiderDetailsLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.remider_details_layout, parent, false
            )
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
                if (holder.bind.cbChecked.isChecked) {
//                    notifyDataSetChanged();
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
                }
                //                Log.e("remiderIds", TextUtils.join(",", remiderIds));
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
            if (model[position]!!.isCheck.equals("1", ignoreCase = true)) {
                holder.bind.switchStatus.isChecked = true
            } else {
                holder.bind.switchStatus.isChecked = false
            }
            //            if (model.get(position).getIsLock().equalsIgnoreCase("1")) {
//                holder.bind.switchStatus.setClickable(false);
//                holder.bind.switchStatus.setEnabled(false);
//                holder.bind.llSwitchStatus.setClickable(true);
//                holder.bind.llSwitchStatus.setEnabled(true);
//                holder.bind.llSwitchStatus.setOnClickListener(view -> {
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                });
//            } else if (model.get(position).getIsLock().equalsIgnoreCase("2")) {
//                holder.bind.switchStatus.setClickable(false);
//                holder.bind.switchStatus.setEnabled(false);
//                holder.bind.llSwitchStatus.setClickable(true);
//                holder.bind.llSwitchStatus.setEnabled(true);
//                holder.bind.llSwitchStatus.setOnClickListener(view -> {
//                    BWSApplication.showToast(getString(R.string.reactive_plan), activity);
//                });
//            } else if (model.get(position).getIsLock().equalsIgnoreCase("0") || model.get(position).getIsLock().equalsIgnoreCase("")) {
            holder.bind.switchStatus.isClickable = true
            holder.bind.switchStatus.isEnabled = true
            holder.bind.llSwitchStatus.isClickable = false
            holder.bind.llSwitchStatus.isEnabled = false
            holder.bind.switchStatus.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
                if (checked) {
                    prepareSwitchStatus("1", model[position]!!.playlistId)
                } else {
                    prepareSwitchStatus("0", model[position]!!.playlistId)
                }
            }
            //            }
            holder.bind.llMainLayout.setOnClickListener {
                if (BWSApplication.isNetworkConnected(activity)) {
                    notificationStatus = true
                    myBackPress = false
                    BWSApplication.getReminderDay(
                        ctx, activity, CoUSERID, model[position]!!.playlistId,
                        model[position]!!.playlistName, activity as FragmentActivity?,
                        model[position]!!.reminderTime, model[position]!!.rDay
                    )
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), activity)
                }
            }
        }

        override fun getItemCount(): Int {
            return model!!.size
        }

        inner class MyViewHolder(var bind: RemiderDetailsLayoutBinding) : RecyclerView.ViewHolder(
            bind.root
        )
    }

    private fun prepareSwitchStatus(reminderStatus: String, PlaylistID: String?) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(
                binding.progressBar,
                binding.progressBarHolder,
                activity
            )
            val listCall = APINewClient.getClient()
                .getReminderStatus(CoUSERID, PlaylistID, reminderStatus) /*set 1 or not 0 */
            listCall.enqueue(object : Callback<ReminderStatusModel?> {
                override fun onResponse(
                    call: Call<ReminderStatusModel?>,
                    response: Response<ReminderStatusModel?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listModel = response.body()
                            BWSApplication.showToast(listModel!!.responseMessage, activity)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ReminderStatusModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
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
                    notificationStatus = false
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
                if (!notificationStatus) {
                    if (GlobalInitExoPlayer.player != null) {
                        Log.e("Destroy", "Activity Destoryed")
                        val notificationManager =
                            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(GlobalInitExoPlayer.notificationId)
                        GlobalInitExoPlayer.relesePlayer(ctx)
                    }
                }
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    var simpleCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("SetTextI18n")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    val dialog = Dialog(ctx!!)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.reminder_layout)
                    dialog.window!!.setBackgroundDrawable(
                        ColorDrawable(
                            ContextCompat.getColor(
                                activity,
                                R.color.dark_blue_gray
                            )
                        )
                    )
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
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
                            val listCall = APINewClient.getClient().getDeleteRemider(
                                CoUSERID,
                                listReminderModel!!.responseData!![position]!!.reminderId
                            )
                            listCall.enqueue(object : Callback<DeleteRemiderModel?> {
                                override fun onResponse(
                                    call: Call<DeleteRemiderModel?>,
                                    response: Response<DeleteRemiderModel?>
                                ) {
                                    try {
                                        if (response.isSuccessful) {
                                            val model = response.body()
                                            BWSApplication.showToast(
                                                model!!.responseMessage,
                                                activity
                                            )
                                            prepareData()
                                            dialog.dismiss()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<DeleteRemiderModel?>,
                                    t: Throwable
                                ) {
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

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.absoluteAdapterPosition
                return super.getSwipeDirs(recyclerView, viewHolder)
            }
        }
}