package com.brainwellnessspa.DashboardTwoModule.home

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.BottomNavigationActivity
import com.brainwellnessspa.DashboardTwoModule.Model.HomeScreenModel
import com.brainwellnessspa.DashboardTwoModule.manage.ManageFragment
import com.brainwellnessspa.DassAssSliderTwo.Activity.AssProcessActivity
import com.brainwellnessspa.ManageModule.ManageAudioPlaylistActivity
import com.brainwellnessspa.ManageModule.RecommendedCategoryActivity
import com.brainwellnessspa.ManageModule.SleepTimeActivity
import com.brainwellnessspa.NotificationTwoModule.NotificationListActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.ReminderModule.Models.ReminderSelectionModel
import com.brainwellnessspa.UserModuleTwo.Activities.AddProfileActivity
import com.brainwellnessspa.UserModuleTwo.Activities.WalkScreenActivity
import com.brainwellnessspa.UserModuleTwo.Models.AddedUserListModel
import com.brainwellnessspa.UserModuleTwo.Models.VerifyPinModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var homeViewModel: HomeViewModel? = null
    lateinit var ctx: Context
    lateinit var act: Activity
    var adapter: UserListAdapter? = null
    var CoUSERID: String? = null
    var USERID: String? = null
    var UserName: String? = null
    var UserIMAGE: String? = null
    var PlaylistImage = ""
    var PlaylistType = ""
    var UserID: String? = null
    var PlaylistID = ""
    var Download = ""
    var Liked = ""
    var SLEEPTIME: String? = null
    var selectedCategoriesName = arrayListOf<String>()
    var PlaylistDesc = ""
    var PlaylistName = ""
    var ScreenView = ""
    var TotalAudio = ""
    var Totalhour = ""
    var Totalminute = ""
    lateinit var editTexts: Array<EditText>
    var tvSendOTPbool = true
    var myBackPress = false
    var gson: Gson = Gson()
    var AudioId: String? = null
    var SongListSize = 0
    private val mLastClickTime: Long = 0
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    var mBottomSheetDialog: BottomSheetDialog? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.getRoot()
        ctx = requireActivity()
        act = requireActivity()
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        UserIMAGE = shared1.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "")

        val shared = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        SLEEPTIME = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }

        binding.tvName.text = UserName
        if (UserIMAGE.equals("", true)) {
            binding.ivUser.setImageResource(R.drawable.ic_gray_user)
        } else {
            Glide.with(ctx).load(UserIMAGE)
                    .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                    .into(binding.ivUser)
        }
        homeViewModel!!.text.observe(viewLifecycleOwner, { s: String? -> })

        binding.rvAreaOfFocusCategory.layoutManager = GridLayoutManager(ctx, 3)
        val adapter = AreaOfFocusAdapter(binding, ctx, selectedCategoriesName)
        binding.rvAreaOfFocusCategory.adapter = adapter


        binding.llBottomView.setOnClickListener { v: View? ->
            val layoutBinding: UserListCustomLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.user_list_custom_layout, null, false)
            mBottomSheetDialog = BottomSheetDialog(ctx, R.style.BaseBottomSheetDialog)
            mBottomSheetDialog!!.setContentView(layoutBinding.root)
            mBottomSheetBehavior = BottomSheetBehavior<View>()
            mBottomSheetBehavior!!.isHideable = true
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            mBottomSheetDialog!!.show()
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            layoutBinding.rvUserList.layoutManager = mLayoutManager
            layoutBinding.rvUserList.itemAnimator = DefaultItemAnimator()
            prepareUserData(layoutBinding.rvUserList, layoutBinding.progressBar)
            layoutBinding.llAddNewUser.setOnClickListener { v1: View? ->
                val i = Intent(activity, AddProfileActivity::class.java)
                startActivity(i)
                mBottomSheetDialog!!.hide()
            }
        }
        binding.tvReminder.setOnClickListener {
            BWSApplication.getReminderDay(activity, activity, CoUSERID, "15", "Ultimate Anger Relief Bundle")
        }

        binding.ivEditCategory.setOnClickListener {
            val i = Intent(activity, SleepTimeActivity::class.java)
            startActivity(i)
        }

        binding.llClick.setOnClickListener {
            val i = Intent(activity, NotificationListActivity::class.java)
            startActivity(i)
        }

        binding.llPlayerView.setOnClickListener { v: View? ->
            val i = Intent(activity, ManageAudioPlaylistActivity::class.java)
            startActivity(i)
        }
        return view
    }

    fun prepareUserData(rvUserList: RecyclerView, progressBar: ProgressBar) {
        if (BWSApplication.isNetworkConnected(activity)) {
            progressBar.visibility = View.VISIBLE
            progressBar.invalidate()
            val listCall = APINewClient.getClient().getUserList(USERID)
            listCall.enqueue(object : Callback<AddedUserListModel> {
                override fun onResponse(call: Call<AddedUserListModel>, response: Response<AddedUserListModel>) {
                    try {
                        progressBar.visibility = View.GONE
                        var listModel: AddedUserListModel = response.body()!!
                        adapter = UserListAdapter(listModel.responseData!!)
                        rvUserList.adapter = adapter
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddedUserListModel>, t: Throwable) {
                    progressBar.visibility = View.GONE
                }
            })
        }
    }

    fun prepareHomeData() {
        if (BWSApplication.isNetworkConnected(activity)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.getClient().getHomeScreenData(USERID)
            listCall.enqueue(object : Callback<HomeScreenModel?> {
                override fun onResponse(call: Call<HomeScreenModel?>, response: Response<HomeScreenModel?>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel = response.body()
                        binding.tvPlaylistName.text = listModel!!.responseData!!.suggestedPlaylist!!.playlistName
                        binding.tvTime.text = listModel.responseData!!.suggestedPlaylist!!.totalhour.toString() + ":" + listModel.responseData!!.suggestedPlaylist!!.totalminute.toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<HomeScreenModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding!!.progressBar, binding!!.progressBarHolder, activity)
                }
            })
        }
    }

    class AreaOfFocusAdapter(var binding: FragmentHomeBinding, var ctx: Context, var selectedCategoriesName: ArrayList<String>) : RecyclerView.Adapter<AreaOfFocusAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SelectedCategoryRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SelectedCategoryRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.selected_category_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvCategory.text = selectedCategoriesName[position]
            holder.bindingAdapter.tvhours.text = (position + 1).toString()
        }

        override fun getItemCount(): Int {
            return selectedCategoriesName.size
        }
    }

    inner class UserListAdapter(private val model: AddedUserListModel.ResponseData) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
        var selectedItem = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MultipleProfileChangeLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.multiple_profile_change_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val modelList = model.coUserList
            holder.bind.tvName.text = modelList!![position].name
            if (modelList[position].image.equals("", true)) {
                holder.bind.ivProfileImage.setImageResource(R.drawable.ic_user_default_icon)
            } else {
                Glide.with(activity!!).load(modelList[position].image)
                        .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                        .into(holder.bind.ivProfileImage)
            }
            holder.bind.ivCheck.setImageResource(R.drawable.ic_user_checked_icon)
            holder.bind.ivCheck.visibility = View.INVISIBLE
            if (selectedItem == position) {
                holder.bind.ivCheck.visibility = View.VISIBLE
            }
            holder.bind.llAddNewCard.setOnClickListener { v: View? ->
                val previousItem = selectedItem
                selectedItem = position
                notifyItemChanged(previousItem)
                notifyItemChanged(position)
                val dialog = Dialog(activity!!)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.comfirm_pin_layout)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.setLayout(700, ViewGroup.LayoutParams.WRAP_CONTENT)
                val btnDone = dialog.findViewById<Button>(R.id.btnDone)
                val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                val txtError = dialog.findViewById<TextView>(R.id.txtError)
                val edtOTP1 = dialog.findViewById<EditText>(R.id.edtOTP1)
                val edtOTP2 = dialog.findViewById<EditText>(R.id.edtOTP2)
                val edtOTP3 = dialog.findViewById<EditText>(R.id.edtOTP3)
                val edtOTP4 = dialog.findViewById<EditText>(R.id.edtOTP4)
                val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
                tvTitle.text = "Unlock"
                editTexts = arrayOf(edtOTP1, edtOTP2, edtOTP3, edtOTP4)
                edtOTP1.addTextChangedListener(PinTextWatcher(0, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                edtOTP2.addTextChangedListener(PinTextWatcher(1, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                edtOTP3.addTextChangedListener(PinTextWatcher(2, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                edtOTP4.addTextChangedListener(PinTextWatcher(3, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                edtOTP1.setOnKeyListener(PinOnKeyListener(0))
                edtOTP2.setOnKeyListener(PinOnKeyListener(1))
                edtOTP3.setOnKeyListener(PinOnKeyListener(2))
                edtOTP4.setOnKeyListener(PinOnKeyListener(3))
                dialog.setOnKeyListener { v11: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                btnDone.setOnClickListener { v1: View? ->
                    if (edtOTP1.text.toString().equals("", ignoreCase = true)
                            && edtOTP2.text.toString().equals("", ignoreCase = true)
                            && edtOTP3.text.toString().equals("", ignoreCase = true)
                            && edtOTP4.text.toString().equals("", ignoreCase = true)) {
                        txtError.visibility = View.VISIBLE
                        txtError.text = "Please enter OTP"
                    } else {
                        if (BWSApplication.isNetworkConnected(activity)) {
                            txtError.visibility = View.GONE
                            txtError.text = ""
                            progressBar.visibility = View.VISIBLE
                            progressBar.invalidate()
                            val listCall = APINewClient.getClient().getVerifyPin(modelList[position].coUserId,
                                    edtOTP1.text.toString() + "" +
                                            edtOTP2.text.toString() + "" +
                                            edtOTP3.text.toString() + "" +
                                            edtOTP4.text.toString())
                            listCall.enqueue(object : Callback<VerifyPinModel?> {
                                override fun onResponse(call: Call<VerifyPinModel?>, response: Response<VerifyPinModel?>) {
                                    try {
                                        progressBar.visibility = View.GONE
                                        val listModel = response.body()
                                        val responseData: VerifyPinModel.ResponseData? = listModel!!.responseData
                                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                            if (responseData!!.isProfileCompleted.equals("0", ignoreCase = true)) {
                                                val intent = Intent(activity, WalkScreenActivity::class.java)
                                                intent.putExtra(CONSTANTS.ScreenView, "ProfileView")
                                                act.startActivity(intent)
                                                act.finish()
                                            } else if (responseData.isAssessmentCompleted.equals("0", ignoreCase = true)) {
                                                val intent = Intent(activity, AssProcessActivity::class.java)
                                                intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                                act.startActivity(intent)
                                                act.finish()
                                            } else if (responseData.isProfileCompleted.equals("1", ignoreCase = true) &&
                                                    responseData.isAssessmentCompleted.equals("1", ignoreCase = true)) {
                                                val intent = Intent(activity, BottomNavigationActivity::class.java)
                                                act.startActivity(intent)
                                                act.finish()
                                            }
                                            val shared = act.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE)
                                            val editor = shared.edit()
                                            editor.putString(CONSTANTS.PREFE_ACCESS_UserID, listModel.responseData!!.userID)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_CoUserID, listModel.responseData!!.coUserId)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, listModel.responseData!!.email)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.responseData!!.name)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.responseData!!.avgSleepTime)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.responseData!!.indexScore)
                                            editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, responseData.image)
                                            editor.commit()
                                            BWSApplication.showToast(listModel.responseMessage, activity)
                                            dialog.dismiss()
                                            mBottomSheetDialog!!.hide()
                                        } else if (listModel.responseCode.equals(getString(R.string.ResponseCodefail), ignoreCase = true)) {
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

                                override fun onFailure(call: Call<VerifyPinModel?>, t: Throwable) {
                                    progressBar.visibility = View.GONE
                                }
                            })
                        }
                    }
                }
                dialog.show()
                dialog.setCanceledOnTouchOutside(true)
                dialog.setCancelable(true)
            }
        }

        override fun getItemCount(): Int {
            return model.getCoUser()!!.size
        }

        inner class MyViewHolder(var bind: MultipleProfileChangeLayoutBinding) : RecyclerView.ViewHolder(bind.root)
    }

    inner class PinTextWatcher internal constructor(private val currentIndex: Int, var edtOTP1: EditText, var edtOTP2: EditText, var edtOTP3: EditText, var edtOTP4: EditText, var btnDone: Button) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
            val OTP1 = edtOTP1.text.toString().trim { it <= ' ' }
            val OTP2 = edtOTP2.text.toString().trim { it <= ' ' }
            val OTP3 = edtOTP3.text.toString().trim { it <= ' ' }
            val OTP4 = edtOTP4.text.toString().trim { it <= ' ' }
            if (!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty()) {
                btnDone.isEnabled = true
                btnDone.setTextColor(ContextCompat.getColor(act, R.color.white))
                btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                btnDone.isEnabled = false
                btnDone.setTextColor(ContextCompat.getColor(act, R.color.white))
                btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            Log.e("OTP VERIFICATION", "" + text)

            /* Detect paste event and set first char */if (text.length > 1) text = text[0].toString() // TODO: We can fill out other EditTexts
            editTexts[currentIndex].removeTextChangedListener(this)
            editTexts[currentIndex].setText(text)
            editTexts[currentIndex].setSelection(text.length)
            editTexts[currentIndex].addTextChangedListener(this)
            if (text.length == 1) {
                moveToNext()
            } else if (text.length == 0) {
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
                for (editText in editTexts) if (editText.text.toString().trim { it <= ' ' }.length == 0) return false
                return true
            }

        private fun hideKeyboard() {
            if (activity!!.currentFocus != null) {
                val inputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
            }
        }

        init {
            if (currentIndex == 0) isFirst = true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    inner class PinOnKeyListener internal constructor(private val currentIndex: Int) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].text.toString().isEmpty() && currentIndex != 0) editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }
    }

    inner class PopupTextWatcher(var edtCreate: EditText, var btnSendCode: Button) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val number = edtCreate.text.toString().trim { it <= ' ' }
            if (number.equals(PlaylistName, ignoreCase = true)) {
                btnSendCode.isEnabled = false
                btnSendCode.setTextColor(resources.getColor(R.color.white))
                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (number.isEmpty()) {
                btnSendCode.isEnabled = false
                btnSendCode.setTextColor(resources.getColor(R.color.white))
                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                btnSendCode.isEnabled = true
                btnSendCode.setTextColor(resources.getColor(R.color.light_black))
                btnSendCode.setBackgroundResource(R.drawable.white_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
}