package com.brainwellnessspa.userModuleTwo.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.analytics
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.dassAssSlider.activities.AssProcessActivity
import com.brainwellnessspa.manageModule.SleepTimeActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.userModuleTwo.models.AddedUserListModel
import com.brainwellnessspa.userModuleTwo.models.CoUserDetailsModel
import com.brainwellnessspa.userModuleTwo.models.SegmentUserList
import com.brainwellnessspa.userModuleTwo.models.VerifyPinModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityUserListBinding
import com.brainwellnessspa.databinding.ScreenUserListLayoutBinding
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
    var USERID: String? = null
    var CoUserID: String? = null
    var CoEMAIL: String? = null
    lateinit var adapter: UserListAdapter
    private lateinit var editTexts: Array<EditText>
    var tvSendOTPool: Boolean = true
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)
        activity = this@UserListActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        CoEMAIL = shared.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "")
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

    fun getUserDetails(userID: String, coUserId: String) {

    }

    class UserListAdapter(
        listModel: AddedUserListModel.ResponseData,
        private var activity: Activity,
        var binding: ActivityUserListBinding,
        var USERID: String,
        var CoUserID: String,
        var CoEMAIL: String
    ) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
        var userList = UserListActivity()
        private var selectedItem = -1
        private var coUsersModel: List<AddedUserListModel.ResponseData.CoUser>? =
            listModel.coUserList
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
                val Letter = name.substring(0, 1)
                holder.bindingAdapter.rlLetter.visibility = View.VISIBLE
                holder.bindingAdapter.tvLetter.text = Letter
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
                USERID = coUsersModel!![position].userID.toString()
                CoUserID = coUsersModel!![position].coUserId.toString()
                CoEMAIL = coUsersModel!![position].email.toString()

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
                        if (BWSApplication.isNetworkConnected(activity)) {
                            BWSApplication.showProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listCall: Call<VerifyPinModel> =
                                APINewClient.getClient().getVerifyPin(
                                    CoUserID,
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
                                        BWSApplication.hideProgressBar(
                                            binding.progressBar,
                                            binding.progressBarHolder,
                                            activity
                                        )
                                        val listModel: VerifyPinModel = response.body()!!
                                        if (listModel.responseCode.equals(
                                                activity.getString(R.string.ResponseCodesuccess),
                                                ignoreCase = true
                                            )
                                        ) {
                                            val UserID: String =
                                                listModel.responseData!!.userID.toString()
                                            val CoUserId: String =
                                                listModel.responseData!!.coUserId.toString()
                                            UserListActivity().getUserDetails(UserID, CoUserId)
                                            val listCall: Call<CoUserDetailsModel> =
                                                APINewClient.getClient()
                                                    .getCoUserDetails(UserID, CoUserId)
                                            listCall.enqueue(object : Callback<CoUserDetailsModel> {
                                                @SuppressLint("HardwareIds")
                                                override fun onResponse(
                                                    call: Call<CoUserDetailsModel>,
                                                    response: Response<CoUserDetailsModel>
                                                ) {
                                                    try {
                                                        val coUserDetailsModel: CoUserDetailsModel =
                                                            response.body()!!
                                                        val responseData: CoUserDetailsModel.ResponseData? =
                                                            coUserDetailsModel.responseData
                                                        if (responseData != null) {
                                                            if (responseData.isProfileCompleted.equals(
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
                                                                        "ProfileView"
                                                                )
                                                                activity.startActivity(intent)
                                                                activity.finish()
                                                            } else if (responseData.isAssessmentCompleted.equals(
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
                                                            } else if (responseData.avgSleepTime.equals(
                                                                            "", ignoreCase = true
                                                                    )
                                                            ) {
                                                                val intent = Intent(
                                                                        activity,
                                                                        SleepTimeActivity::class.java
                                                                )
                                                                activity.startActivity(intent)
                                                                activity.finish()
                                                            } else if (responseData.isProfileCompleted.equals(
                                                                            "1",
                                                                            ignoreCase = true
                                                                    ) &&
                                                                    responseData.isAssessmentCompleted.equals(
                                                                            "1",
                                                                            ignoreCase = true
                                                                    )
                                                            ) {
                                                                val intent = Intent(
                                                                        activity,
                                                                        BottomNavigationActivity::class.java
                                                                )
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
                                                                    CONSTANTS.PREFE_ACCESS_UserID,
                                                                    listModel.responseData!!.userID
                                                            )
                                                            editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_CoUserID,
                                                                    listModel.responseData!!.coUserId
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
                                                                    responseData.avgSleepTime
                                                            )
                                                            editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_INDEXSCORE,
                                                                    responseData.indexScore
                                                            )
                                                            editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_SCORELEVEL,
                                                                    responseData.scoreLevel
                                                            )
                                                            editor.putString(
                                                                    CONSTANTS.PREFE_ACCESS_IMAGE,
                                                                    responseData.image
                                                            )
                                                            editor.putString(
                                                                CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED,
                                                                coUserDetailsModel.responseData!!.isProfileCompleted
                                                            )
                                                            editor.putString(
                                                                CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED,
                                                                coUserDetailsModel.responseData!!.isAssessmentCompleted
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
                                                                    responseData.avgSleepTime
                                                            )
                                                            val selectedCategoriesTitle = arrayListOf<String>()
                                                            val selectedCategoriesName = arrayListOf<String>()
                                                            val gson = Gson()
                                                            for (i in listModel.responseData!!.areaOfFocus!!) {
                                                                selectedCategoriesTitle.add(i.mainCat!!)
                                                                selectedCategoriesName.add(i.recommendedCat!!)
                                                            }
                                                            edited.putString(
                                                                    CONSTANTS.selectedCategoriesTitle,
                                                                    gson.toJson(selectedCategoriesTitle)
                                                            ) //Friend
                                                            edited.putString(
                                                                    CONSTANTS.selectedCategoriesName,
                                                                    gson.toJson(selectedCategoriesName)
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
                                                                        listModel.responseData!!.coUserId
                                                                    )
                                                                    .putValue(
                                                                        "userId",
                                                                        listModel.responseData!!.userID
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
                                                                    .putValue("planExpiryDt", "")
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
                                                                    .putValue("scoreLevel", listModel.responseData!!.scoreLevel)
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
                                                            p1.putValue("CoUserID", listModel.responseData!!.coUserId)
                                                            p1.putValue("userID", listModel.responseData!!.userID)
                                                            p1.putValue("deviceId", Settings.Secure.getString(
                                                                activity.contentResolver,
                                                                Settings.Secure.ANDROID_ID
                                                            ))
                                                            p1.putValue("deviceType", "Android")
                                                            p1.putValue("name", listModel.responseData!!.name)
                                                            p1.putValue("countryCode", "")
                                                            p1.putValue("countryName", "")
                                                            p1.putValue("phone", listModel.responseData!!.mobile)
                                                            p1.putValue("email", listModel.responseData!!.email)
                                                            p1.putValue("plan", "")
                                                            p1.putValue("planStatus", "")
                                                            p1.putValue("planStartDt", "")
                                                            p1.putValue("planExpiryDt", "")
                                                            p1.putValue("clinikoId", "")
                                                            p1.putValue("isProfileCompleted", listModel.responseData!!.isProfileCompleted)
                                                            p1.putValue("isAssessmentCompleted", listModel.responseData!!.isAssessmentCompleted)
                                                            p1.putValue("indexScore", listModel.responseData!!.indexScore)
                                                            p1.putValue("scoreLevel", listModel.responseData!!.scoreLevel)
                                                            p1.putValue("areaOfFocus",  listModel.responseData!!.areaOfFocus)
                                                            p1.putValue("avgSleepTime", listModel.responseData!!.avgSleepTime)
                                                            BWSApplication.addToSegment("CoUser Login", p1, CONSTANTS.track)
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
                                            BWSApplication.showToast(
                                                listModel.responseMessage,
                                                activity
                                            )
                                        } else if (listModel.responseCode.equals(
                                                activity.getString(
                                                    R.string.ResponseCodefail
                                                ), ignoreCase = true
                                            )
                                        ) {
                                            txtError.visibility = View.VISIBLE
                                            txtError.text = listModel.responseMessage
                                        } else {
                                            txtError.visibility = View.VISIBLE
                                            txtError.text = listModel.responseMessage
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(call: Call<VerifyPinModel>, t: Throwable) {
                                    BWSApplication.hideProgressBar(
                                        binding.progressBar,
                                        binding.progressBarHolder,
                                        activity
                                    )
                                }
                            })
                        } else {
                            BWSApplication.showToast(
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
            }
        }

        override fun getItemCount(): Int {
            return coUsersModel!!.size
        }
    }

    class PinTextWatcher internal constructor(
        val activity: Activity, private val currentIndex: Int,
        var editTexts: Array<EditText>, val btnDone: Button,
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
            val OTP1: String = edtOTP1.text.toString().trim()
            val OTP2: String = edtOTP2.text.toString().trim()
            val OTP3: String = edtOTP3.text.toString().trim()
            val OTP4: String = edtOTP4.text.toString().trim()
            if (OTP1.isNotEmpty() && OTP2.isNotEmpty() && OTP3.isNotEmpty() && OTP4.isNotEmpty()) {
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
            private get() {
                for (editText in editTexts) if (editText.text.toString()
                        .trim { it <= ' ' }.isEmpty()
                ) return false
                return true
            }

        private fun hideKeyboard() {
            if (activity.getCurrentFocus() != null) {
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
        var editTexts: Array<EditText>
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
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<AddedUserListModel> = APINewClient.getClient().getUserList(USERID)
            listCall.enqueue(object : Callback<AddedUserListModel> {
                override fun onResponse(
                    call: Call<AddedUserListModel>,
                    response: Response<AddedUserListModel>
                ) {
                    try {
                        BWSApplication.hideProgressBar(
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
                                binding,
                                USERID.toString(),
                                CoUserID.toString(),
                                CoEMAIL.toString()
                            )
                            binding.rvUserList.adapter = adapter

                            if (listModel.responseData!!.coUserList!!.size == listModel.responseData!!.maxuseradd!!.toInt()) {
                                binding.llAddNewUser.visibility = View.GONE
                            } else {
                                binding.llAddNewUser.visibility = View.VISIBLE
                            }
                            val section = ArrayList<SegmentUserList>()
                            for (i in listModel.responseData!!.coUserList!!.indices) {
                                val e = SegmentUserList()
                                e.coUserId = listModel.responseData!!.coUserList!![i].coUserId
                                e.name = listModel.responseData!!.coUserList!![i].name
                                e.mobile = listModel.responseData!!.coUserList!![i].mobile
                                e.email = listModel.responseData!!.coUserList!![i].email
                                e.image = listModel.responseData!!.coUserList!![i].image
                                e.dob = listModel.responseData!!.coUserList!![i].dob
                                section.add(e)
                            }
                            val p = Properties()
                            val gson = Gson()
                            p.putValue("userID", USERID)
                            p.putValue("maxuseradd", listModel.responseData!!.maxuseradd)
                            p.putValue("coUserList", gson.toJson(section))
                            BWSApplication.addToSegment("Couser List Viewed", p, CONSTANTS.screen)
                        } else {
//                            BWSApplication.showToast(listModel.getResponseMessage(), applicationContext)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddedUserListModel>, t: Throwable) {
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
}


