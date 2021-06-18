package com.brainwellnessspa.userModule.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.manageModule.SleepTimeActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.userModule.models.AddedUserListModel
import com.brainwellnessspa.userModule.models.CoUserDetailsModel
import com.brainwellnessspa.userModule.models.SegmentUserList
import com.brainwellnessspa.userModule.models.VerifyPinModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityUserListBinding
import com.brainwellnessspa.databinding.ScreenUserListLayoutBinding
import com.brainwellnessspa.userModule.signup.WalkScreenActivity
import com.brainwellnessspa.userModule.splashscreen.SplashActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UserListActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserListBinding
    lateinit var dialog: Dialog
    var userId: String? = ""
    var coUserId: String? = ""
    var coEmail: String? = ""
    lateinit var adapter: UserListAdapter
    private lateinit var editTexts: Array<EditText>
    var tvSendOTPool: Boolean = true
    lateinit var activity: Activity
    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)
        activity = this@UserListActivity
        ctx = this@UserListActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        coEmail = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
        /* binding.llBack.setOnClickListener {
             finish()
         }*/


        binding.llAddNewUser.setOnClickListener {
            val i = Intent(applicationContext, AddProfileActivity::class.java)
            i.putExtra("AddProfile", "Add")
            i.putExtra("CoUserID", "")
            i.putExtra("CoEMAIL", "")
            i.putExtra("CoName", "")
            i.putExtra("CoNumber", "")
            startActivity(i)
        }

    }

    override fun onResume() {
        prepareUserData()
        binding.btnLogIn.setBackgroundResource(R.drawable.gray_round_cornor)
        binding.btnLogIn.isEnabled = false
        binding.tvForgotPin.isEnabled = false
        binding.tvForgotPin.setTextColor(ContextCompat.getColor(activity, R.color.gray))
        super.onResume()
    }


    override fun onBackPressed() {
        finishAffinity()
    }

    class UserListAdapter(
        listModel: AddedUserListModel.ResponseData,
        private var activity: Activity,
        private var ctx: Context,
        var binding: ActivityUserListBinding,
        var userId: String,
        var coUserId: String,
        var coEmail: String
    ) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
        var userList = UserListActivity()
        private var selectedItem = -1
        private var coUsersModel: List<AddedUserListModel.ResponseData.CoUser>? =
            listModel.userList
        lateinit var txtError: TextView

        inner class MyViewHolder(var bindingAdapter: ScreenUserListLayoutBinding) :
            RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: ScreenUserListLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.screen_user_list_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvName.text = coUsersModel!![position].name
            val name: String?

            if (coUsersModel!![position].image.equals("", true)) {
                holder.bindingAdapter.ivProfileImage.visibility = View.GONE
                name = if (coUsersModel!![position].name.equals("", ignoreCase = true)) {
                    "Guest"
                } else {
                    coUsersModel!![position].name.toString()
                }
                val letter = name.substring(0, 1)
                holder.bindingAdapter.rlLetter.visibility = View.VISIBLE
                holder.bindingAdapter.tvLetter.text = letter
            } else {
                holder.bindingAdapter.ivProfileImage.visibility = View.VISIBLE
                holder.bindingAdapter.rlLetter.visibility = View.GONE
                Glide.with(activity).load(coUsersModel!![position].image)
                    .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                    .into(holder.bindingAdapter.ivProfileImage)
            }
            holder.bindingAdapter.ivCheck.setImageResource(R.drawable.ic_user_checked_icon)
            holder.bindingAdapter.ivCheck.visibility = View.INVISIBLE
            if (selectedItem == position) {
                holder.bindingAdapter.ivCheck.visibility = View.VISIBLE
            }

            holder.bindingAdapter.rlAddNewCard.setOnClickListener {
                val previousItem = selectedItem
                selectedItem = position
                notifyItemChanged(previousItem)
                notifyItemChanged(position)
                binding.btnLogIn.setBackgroundResource(R.drawable.light_green_rounded_filled)
                binding.btnLogIn.isEnabled = true
                binding.tvForgotPin.isEnabled = true
                binding.tvForgotPin.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.app_theme_color
                    )
                )
                userId = coUsersModel!![position].userID.toString()
                coUserId = coUsersModel!![position].coUserId.toString()
                coEmail = coUsersModel!![position].email.toString()

            }

            binding.btnLogIn.setOnClickListener {
                userList.dialog = Dialog(activity)
                userList.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                userList.dialog.setContentView(R.layout.comfirm_pin_layout)
                userList.dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                userList.dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val btnDone: Button = userList.dialog.findViewById(R.id.btnDone)
                val txtError: TextView = userList.dialog.findViewById(R.id.txtError)
                val tvTitle: TextView = userList.dialog.findViewById(R.id.tvTitle)
                val edtOTP1: EditText = userList.dialog.findViewById(R.id.edtOTP1)
                val edtOTP2: EditText = userList.dialog.findViewById(R.id.edtOTP2)
                val edtOTP3: EditText = userList.dialog.findViewById(R.id.edtOTP3)
                val edtOTP4: EditText = userList.dialog.findViewById(R.id.edtOTP4)
                tvTitle.text = "Unlock the app"
                userList.editTexts = arrayOf(edtOTP1, edtOTP2, edtOTP3, edtOTP4)
                edtOTP1.addTextChangedListener(
                    PinTextWatcher(
                        activity,
                        0,
                        userList.editTexts,
                        btnDone,
                        edtOTP1,
                        edtOTP2,
                        edtOTP3,
                        edtOTP4,
                        userList.tvSendOTPool
                    )
                )
                edtOTP2.addTextChangedListener(
                    PinTextWatcher(
                        activity,
                        1,
                        userList.editTexts,
                        btnDone,
                        edtOTP1,
                        edtOTP2,
                        edtOTP3,
                        edtOTP4,
                        userList.tvSendOTPool
                    )
                )
                edtOTP3.addTextChangedListener(
                    PinTextWatcher(
                        activity,
                        2,
                        userList.editTexts,
                        btnDone,
                        edtOTP1,
                        edtOTP2,
                        edtOTP3,
                        edtOTP4,
                        userList.tvSendOTPool
                    )
                )
                edtOTP4.addTextChangedListener(
                    PinTextWatcher(
                        activity,
                        3,
                        userList.editTexts,
                        btnDone,
                        edtOTP1,
                        edtOTP2,
                        edtOTP3,
                        edtOTP4,
                        userList.tvSendOTPool
                    )
                )
                edtOTP1.setOnKeyListener(PinOnKeyListener(0, userList.editTexts))
                edtOTP2.setOnKeyListener(PinOnKeyListener(1, userList.editTexts))
                edtOTP3.setOnKeyListener(PinOnKeyListener(2, userList.editTexts))
                edtOTP4.setOnKeyListener(PinOnKeyListener(3, userList.editTexts))
                userList.dialog.setCancelable(true)
                userList.dialog.setCanceledOnTouchOutside(true)
                userList.dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        userList.dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                btnDone.setOnClickListener {
                    if (edtOTP1.text.toString().equals("", ignoreCase = true)
                        && edtOTP2.text.toString().equals("", ignoreCase = true)
                        && edtOTP3.text.toString().equals("", ignoreCase = true)
                        && edtOTP4.text.toString().equals("", ignoreCase = true)
                    ) {
                        txtError.visibility = View.VISIBLE
                        txtError.text = "Please enter OTP"
                    } else {
                        txtError.visibility = View.GONE
                        txtError.text = ""
                        if (isNetworkConnected(activity)) {
                            showProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listCall: Call<VerifyPinModel> =
                                APINewClient.getClient().getVerifyPin(
                                    userId,
                                    edtOTP1.text.toString() + "" +
                                            edtOTP2.text.toString() + "" +
                                            edtOTP3.text.toString() + "" +
                                            edtOTP4.text.toString()
                                )
                            listCall.enqueue(object : Callback<VerifyPinModel> {
                                override fun onResponse(
                                    call: Call<VerifyPinModel>,
                                    response: Response<VerifyPinModel>
                                ) {
                                    try {
                                        hideProgressBar(
                                            binding.progressBar,
                                            binding.progressBarHolder,
                                            activity
                                        )
                                        val listModel: VerifyPinModel = response.body()!!
                                        when {
                                            listModel.responseCode.equals(
                                                activity.getString(R.string.ResponseCodesuccess),
                                                ignoreCase = true
                                            ) -> {
                                                val shared1: SharedPreferences =
                                                    ctx.getSharedPreferences(
                                                        CONSTANTS.PREF_KEY_LOGOUT,
                                                        MODE_PRIVATE
                                                    )
                                                val Logout_UserID = shared1.getString(
                                                    CONSTANTS.PREF_KEY_LOGOUT_UserID,
                                                    ""
                                                )
                                                val Logout_CoUserId = shared1.getString(
                                                    CONSTANTS.PREF_KEY_LOGOUT_CoUserID,
                                                    ""
                                                )

                                                /*   if (!listModel.responseData!!.userID.equals(Logout_UserID, ignoreCase = true)
                                                                                           && !listModel.responseData!!.coUserId.equals(Logout_CoUserId, ignoreCase = true)) {
                                                                                       callObserve1(ctx)
                                                                                   } else {
                                                                                       callObserve2(ctx)
                                                                                   }*/
                                                Log.e(
                                                    "New UserId MobileNo",
                                                    listModel.responseData!!.mainAccountID + "....." + listModel.responseData!!.userId
                                                )
                                                Log.e(
                                                    "Old UserId MobileNo",
                                                    "$Logout_UserID.....$Logout_CoUserId"
                                                )
                                                logout = false
                                                val listCall: Call<CoUserDetailsModel> =
                                                    APINewClient.getClient()
                                                        .getCoUserDetails(listModel.responseData!!.userId)
                                                listCall.enqueue(object :
                                                    Callback<CoUserDetailsModel> {
                                                    @SuppressLint("HardwareIds")
                                                    override fun onResponse(
                                                        call: Call<CoUserDetailsModel>,
                                                        response: Response<CoUserDetailsModel>
                                                    ) {
                                                        try {
                                                            val coUserDetailsModel: CoUserDetailsModel =
                                                                response.body()!!
                                                            if (coUserDetailsModel.ResponseData != null) {
                                                                if (coUserDetailsModel.ResponseData.isProfileCompleted.equals(
                                                                        "0",
                                                                        ignoreCase = true
                                                                    )
                                                                ) {
                                                                    val intent = Intent(
                                                                        activity,
                                                                        WalkScreenActivity::class.java
                                                                    )
                                                                    intent.putExtra(
                                                                        CONSTANTS.ScreenView,
                                                                        "1"
                                                                    )
                                                                    activity.startActivity(intent)
                                                                    activity.finish()
                                                                } else if (coUserDetailsModel.ResponseData.isAssessmentCompleted.equals(
                                                                        "0",
                                                                        ignoreCase = true
                                                                    )
                                                                ) {
                                                                    val intent = Intent(
                                                                        activity,
                                                                        AssProcessActivity::class.java
                                                                    )
                                                                    intent.putExtra(
                                                                        CONSTANTS.ASSPROCESS,
                                                                        "0"
                                                                    )
                                                                    activity.startActivity(intent)
                                                                    activity.finish()
                                                                } else if (coUserDetailsModel.ResponseData.AvgSleepTime.equals(
                                                                        "", ignoreCase = true
                                                                    )
                                                                ) {
                                                                    val intent = Intent(
                                                                        activity,
                                                                        SleepTimeActivity::class.java
                                                                    )
                                                                    activity.startActivity(intent)
                                                                    activity.finish()
                                                                } else if (coUserDetailsModel.ResponseData.isProfileCompleted.equals(
                                                                        "1",
                                                                        ignoreCase = true
                                                                    ) &&
                                                                    coUserDetailsModel.ResponseData.isAssessmentCompleted.equals(
                                                                        "1",
                                                                        ignoreCase = true
                                                                    )
                                                                ) {
                                                                    val intent = Intent(
                                                                        activity,
                                                                        BottomNavigationActivity::class.java
                                                                    )
                                                                    intent.putExtra("IsFirst", "1")
                                                                    activity.startActivity(intent)
                                                                    activity.finish()
                                                                }
                                                                val shared =
                                                                    activity.getSharedPreferences(
                                                                        CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
                                                                        MODE_PRIVATE
                                                                    )
                                                                val editor = shared.edit()
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_mainAccountID,
                                                                    listModel.responseData!!.mainAccountID
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_UserId,
                                                                    listModel.responseData!!.userId
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_EMAIL,
                                                                    listModel.responseData!!.email
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_NAME,
                                                                    listModel.responseData!!.name
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_MOBILE,
                                                                    listModel.responseData!!.mobile
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_SLEEPTIME,
                                                                    coUserDetailsModel.ResponseData.AvgSleepTime
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_INDEXSCORE,
                                                                    coUserDetailsModel.ResponseData.indexScore
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_SCORELEVEL,
                                                                    coUserDetailsModel.ResponseData.ScoreLevel
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_IMAGE,
                                                                    coUserDetailsModel.ResponseData.Image
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED,
                                                                    coUserDetailsModel.ResponseData.isProfileCompleted
                                                                )
                                                                editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED,
                                                                    coUserDetailsModel.ResponseData.isAssessmentCompleted
                                                                )
                                                                editor.apply()
                                                                val sharded =
                                                                    activity.getSharedPreferences(
                                                                        CONSTANTS.RecommendedCatMain,
                                                                        Context.MODE_PRIVATE
                                                                    )
                                                                val edited = sharded.edit()
                                                                edited.putString(
                                                                    CONSTANTS.PREFE_ACCESS_SLEEPTIME,
                                                                    coUserDetailsModel.ResponseData.AvgSleepTime
                                                                )
                                                                val selectedCategoriesTitle =
                                                                    arrayListOf<String>()
                                                                val selectedCategoriesName =
                                                                    arrayListOf<String>()
                                                                val gson = Gson()
                                                                for (i in listModel.responseData!!.areaOfFocus!!) {
                                                                    selectedCategoriesTitle.add(i.mainCat!!)
                                                                    selectedCategoriesName.add(i.recommendedCat!!)
                                                                }
                                                                edited.putString(
                                                                    CONSTANTS.selectedCategoriesTitle,
                                                                    gson.toJson(
                                                                        selectedCategoriesTitle
                                                                    )
                                                                ) //Friend
                                                                edited.putString(
                                                                    CONSTANTS.selectedCategoriesName,
                                                                    gson.toJson(
                                                                        selectedCategoriesName
                                                                    )
                                                                ) //Friend
                                                                edited.apply()

                                                                val activity = SplashActivity()
                                                                activity.setAnalytics(
                                                                    activity.getString(
                                                                        R.string.segment_key_real
                                                                    )
                                                                )

                                                                analytics.identify(
                                                                    Traits()
                                                                        .putEmail(listModel.responseData!!.email)
                                                                        .putName(listModel.responseData!!.name)
                                                                        .putPhone(listModel.responseData!!.mobile)
                                                                        .putValue(
                                                                            "coUserId",
                                                                            listModel.responseData!!.userId
                                                                        )
                                                                        .putValue(
                                                                            "userId",
                                                                            listModel.responseData!!.mainAccountID
                                                                        )
                                                                        .putValue(
                                                                            "deviceId",
                                                                            Settings.Secure.getString(
                                                                                activity.contentResolver,
                                                                                Settings.Secure.ANDROID_ID
                                                                            )
                                                                        )
                                                                        .putValue(
                                                                            "deviceType",
                                                                            "Android"
                                                                        )
                                                                        .putValue(
                                                                            "name",
                                                                            listModel.responseData!!.name
                                                                        )
                                                                        .putValue("countryCode", "")
                                                                        .putValue("countryName", "")
                                                                        .putValue(
                                                                            "phone",
                                                                            listModel.responseData!!.mobile
                                                                        )
                                                                        .putValue(
                                                                            "email",
                                                                            listModel.responseData!!.email
                                                                        )
                                                                        .putValue(
                                                                            "DOB",
                                                                            listModel.responseData!!.dob
                                                                        )
                                                                        .putValue(
                                                                            "profileImage",
                                                                            listModel.responseData!!.image
                                                                        )
                                                                        .putValue("plan", "")
                                                                        .putValue("planStatus", "")
                                                                        .putValue("planStartDt", "")
                                                                        .putValue(
                                                                            "planExpiryDt",
                                                                            ""
                                                                        )
                                                                        .putValue("clinikoId", "")
                                                                        .putValue(
                                                                            "isProfileCompleted",
                                                                            listModel.responseData!!.isProfileCompleted
                                                                        )
                                                                        .putValue(
                                                                            "isAssessmentCompleted",
                                                                            listModel.responseData!!.isAssessmentCompleted
                                                                        )
                                                                        .putValue(
                                                                            "indexScore",
                                                                            listModel.responseData!!.indexScore
                                                                        )
                                                                        .putValue(
                                                                            "scoreLevel",
                                                                            listModel.responseData!!.scoreLevel
                                                                        )
                                                                        .putValue(
                                                                            "areaOfFocus",
                                                                            listModel.responseData!!.areaOfFocus
                                                                        )
                                                                        .putValue(
                                                                            "avgSleepTime",
                                                                            listModel.responseData!!.avgSleepTime
                                                                        )
                                                                )
                                                                val p1 = Properties()
                                                                p1.putValue(
                                                                    "CoUserID",
                                                                    listModel.responseData!!.userId
                                                                )
                                                                p1.putValue(
                                                                    "userID",
                                                                    listModel.responseData!!.mainAccountID
                                                                )
                                                                p1.putValue(
                                                                    "deviceId",
                                                                    Settings.Secure.getString(
                                                                        activity.contentResolver,
                                                                        Settings.Secure.ANDROID_ID
                                                                    )
                                                                )
                                                                p1.putValue("deviceType", "Android")
                                                                p1.putValue(
                                                                    "name",
                                                                    listModel.responseData!!.name
                                                                )
                                                                p1.putValue("countryCode", "")
                                                                p1.putValue("countryName", "")
                                                                p1.putValue(
                                                                    "phone",
                                                                    listModel.responseData!!.mobile
                                                                )
                                                                p1.putValue(
                                                                    "email",
                                                                    listModel.responseData!!.email
                                                                )
                                                                p1.putValue("plan", "")
                                                                p1.putValue("planStatus", "")
                                                                p1.putValue("planStartDt", "")
                                                                p1.putValue("planExpiryDt", "")
                                                                p1.putValue("clinikoId", "")
                                                                p1.putValue(
                                                                    "isProfileCompleted",
                                                                    listModel.responseData!!.isProfileCompleted
                                                                )
                                                                p1.putValue(
                                                                    "isAssessmentCompleted",
                                                                    listModel.responseData!!.isAssessmentCompleted
                                                                )
                                                                p1.putValue(
                                                                    "indexScore",
                                                                    listModel.responseData!!.indexScore
                                                                )
                                                                p1.putValue(
                                                                    "scoreLevel",
                                                                    listModel.responseData!!.scoreLevel
                                                                )
                                                                p1.putValue(
                                                                    "areaOfFocus",
                                                                    listModel.responseData!!.areaOfFocus
                                                                )
                                                                p1.putValue(
                                                                    "avgSleepTime",
                                                                    listModel.responseData!!.avgSleepTime
                                                                )
                                                                addToSegment(
                                                                    "CoUser Login",
                                                                    p1,
                                                                    CONSTANTS.track
                                                                )
                                                            }
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<CoUserDetailsModel>,
                                                        t: Throwable
                                                    ) {
                                                    }
                                                })

                                                userList.dialog.dismiss()
                                                //                                            showToast(
                                                //                                                listModel.responseMessage,
                                                //                                                activity
                                                //                                            )
                                            }
                                            listModel.responseCode.equals(
                                                activity.getString(
                                                    R.string.ResponseCodefail
                                                ), ignoreCase = true
                                            ) -> {
                                                txtError.visibility = View.VISIBLE
                                                txtError.text = listModel.responseMessage
                                            }
                                            else -> {
                                                txtError.visibility = View.VISIBLE
                                                txtError.text = listModel.responseMessage
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(call: Call<VerifyPinModel>, t: Throwable) {
                                    hideProgressBar(
                                        binding.progressBar,
                                        binding.progressBarHolder,
                                        activity
                                    )
                                }
                            })
                        } else {
                            showToast(
                                activity.getString(R.string.no_server_found),
                                activity
                            )
                        }
                    }
                }
                userList.dialog.show()
            }

            binding.tvForgotPin.setOnClickListener {
                val i = Intent(activity, AddProfileActivity::class.java)
                i.putExtra("AddProfile", "Forgot")
                i.putExtra("CoUserID", coUsersModel!![position].coUserId.toString())
                i.putExtra("CoEMAIL", coUsersModel!![position].email.toString())
                i.putExtra("CoName", coUsersModel!![position].name.toString())
                i.putExtra("CoNumber", coUsersModel!![position].mobile.toString())
                activity.startActivity(i)
                activity.finish()
            }
        }

        override fun getItemCount(): Int {
            return coUsersModel!!.size
        }
    }

    class PinTextWatcher internal constructor(
        val activity: Activity, private val currentIndex: Int,
        private var editTexts: Array<EditText>, val btnDone: Button,
        val edtOTP1: EditText, val edtOTP2: EditText,
        val edtOTP3: EditText, val edtOTP4: EditText,
        private var tvSendOTPbool: Boolean
    ) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
            val otp1: String = edtOTP1.text.toString().trim()
            val otp2: String = edtOTP2.text.toString().trim()
            val otp3: String = edtOTP3.text.toString().trim()
            val otp4: String = edtOTP4.text.toString().trim()
            if (otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() && otp4.isNotEmpty()) {
                btnDone.isEnabled = true
                btnDone.setTextColor(ContextCompat.getColor(activity, R.color.white))
                btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                btnDone.isEnabled = false
                btnDone.setTextColor(ContextCompat.getColor(activity, R.color.white))
                btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            Log.e("OTP VERIFICATION", "" + text)

            /* Detect paste event and set first char */if (text.length > 1) text =
                text[0].toString() // TODO: We can fill out other EditTexts
            editTexts[currentIndex].removeTextChangedListener(this)
            editTexts[currentIndex].setText(text)
            editTexts[currentIndex].setSelection(text.length)
            editTexts[currentIndex].addTextChangedListener(this)
            if (text.length == 1) {
                moveToNext()
            } else if (text.isEmpty()) {
                if (!tvSendOTPbool) {
                    editTexts[0].requestFocus()
                } else {
                    moveToPrevious()
                }
            }
        }

        private fun moveToNext() {
            if (!isLast) editTexts[currentIndex + 1].requestFocus()
            if (isAllEditTextsFilled && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts[currentIndex - 1].requestFocus()
        }

        private val isAllEditTextsFilled: Boolean
            get() {
                for (editText in editTexts) if (editText.text.toString()
                        .trim { it <= ' ' }.isEmpty()
                ) return false
                return true
            }

        private fun hideKeyboard() {
            if (activity.currentFocus != null) {
                val inputMethodManager =
                    activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0
                )
            }
        }

        init {
            if (currentIndex == 0) isFirst =
                true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    class PinOnKeyListener internal constructor(
        private val currentIndex: Int,
        private var editTexts: Array<EditText>
    ) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].text.toString()
                        .isEmpty() && currentIndex != 0
                ) editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }
    }

    private fun prepareUserData() {
        if (isNetworkConnected(this)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AddedUserListModel> =
                APINewClient.getClient().getUserList(userId)
            listCall.enqueue(object : Callback<AddedUserListModel> {
                override fun onResponse(
                    call: Call<AddedUserListModel>,
                    response: Response<AddedUserListModel>
                ) {
                    try {
                        hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: AddedUserListModel = response.body()!!
                        if (listModel.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            binding.rvUserList.layoutManager = LinearLayoutManager(activity)
                            adapter = UserListAdapter(
                                listModel.responseData!!,
                                activity,
                                ctx,
                                binding,
                                userId.toString(),
                                coUserId.toString(),
                                coEmail.toString()
                            )
                            binding.rvUserList.adapter = adapter

                            if (listModel.responseData!!.userList!!.size == listModel.responseData!!.maxuseradd!!.toInt()) {
                                binding.llAddNewUser.visibility = View.GONE
                            } else {
                                binding.llAddNewUser.visibility = View.VISIBLE
                            }
                            val section = ArrayList<SegmentUserList>()
                            for (i in listModel.responseData!!.userList!!.indices) {
                                val e = SegmentUserList()
                                e.coUserId = listModel.responseData!!.userList!![i].coUserId
                                e.name = listModel.responseData!!.userList!![i].name
                                e.mobile = listModel.responseData!!.userList!![i].mobile
                                e.email = listModel.responseData!!.userList!![i].email
                                e.image = listModel.responseData!!.userList!![i].image
                                e.dob = listModel.responseData!!.userList!![i].dob
                                section.add(e)
                            }
                            val p = Properties()
                            val gson = Gson()
                            p.putValue("userID", userId)
                            p.putValue("maxuseradd", listModel.responseData!!.maxuseradd)
                            p.putValue("coUserList", gson.toJson(section))
                            addToSegment("Couser List Viewed", p, CONSTANTS.screen)
                        } else {
//                            BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddedUserListModel>, t: Throwable) {
                    hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }
}


