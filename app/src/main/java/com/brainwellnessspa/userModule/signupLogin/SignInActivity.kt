package com.brainwellnessspa.userModule.signupLogin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.getKey
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivitySignInBinding
import com.brainwellnessspa.databinding.CountryPopupLayoutBinding
import com.brainwellnessspa.userModule.models.CountryListModel
import com.brainwellnessspa.userModule.models.UserAccessModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.webView.TncActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    var fcmId: String = ""
    lateinit var adapter: CountrySelectAdapter
    var searchFilter: String = ""
    lateinit var activity: Activity
    private lateinit var dialog: Dialog
    lateinit var searchEditText: EditText
    var mobileNo: String? = ""
    var countryCode: String? = ""
    var name: String? = ""
    var email: String? = ""
    var countryShortName: String? = ""
    var countryFullName: String? = ""
    var p: Properties? = null

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val number: String = binding.etNumber.text.toString().trim()
            when {
                number.equals("", ignoreCase = true) -> {
                    binding.btnSignIn.isEnabled = false
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnSignIn.setBackgroundResource(R.drawable.gray_round_cornor)
                }

                else -> {
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnSignIn.setBackgroundResource(R.drawable.light_green_rounded_filled)
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        activity = this@SignInActivity
        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        if (intent != null) {
            mobileNo = intent.getStringExtra("mobileNo")
            countryCode = intent.getStringExtra("countryCode")
            name = intent.getStringExtra("name")
            email = intent.getStringExtra("email")
            countryShortName = intent.getStringExtra("countryShortName")
        }


        binding.etNumber.addTextChangedListener(userTextWatcher)

        if (!mobileNo.equals("", ignoreCase = true)) {
            binding.etNumber.setText(mobileNo)
            binding.tvCountry.text = "+$countryCode"
            binding.tvCountryShortName.text = "$countryShortName"
        } else {
            binding.tvCountry.text = "+61"
            binding.tvCountryShortName.text = "AU"
        }

        binding.llBack.setOnClickListener {
            finishAffinity()
        }

        p = Properties()
        BWSApplication.addToSegment("Login Screen Viewed", p, CONSTANTS.screen)

        binding.tvSignUp.setOnClickListener {
            val i = Intent(activity, SignUpActivity::class.java)
            i.putExtra("mobileNo", "")
            i.putExtra("countryCode", "")
            i.putExtra("name", "")
            i.putExtra("email", "")
            i.putExtra("countryShortName", "")
            startActivity(i)
            finish()
        }

        binding.tvtncs.setOnClickListener {
            val i = Intent(this, TncActivity::class.java)
            i.putExtra(CONSTANTS.Web, "Tnc")
            startActivity(i)
        }

        binding.tvPrivacyPolicys.setOnClickListener {
            val i = Intent(this, TncActivity::class.java)
            i.putExtra(CONSTANTS.Web, "PrivacyPolicy")
            startActivity(i)
        }

        binding.tvDisclaimers.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.full_desc_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(660, ViewGroup.LayoutParams.WRAP_CONTENT)
            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
            val tvClose = dialog.findViewById<RelativeLayout>(R.id.tvClose)
            tvTitle.setText(R.string.Disclaimer)
            tvDesc.setText(R.string.Disclaimer_text)
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            tvClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(true)
        }

        binding.llCountryCode.setOnClickListener {
            dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.country_list_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val rvCountryList: RecyclerView = dialog.findViewById(R.id.rvCountryList)
            val searchView: SearchView = dialog.findViewById(R.id.searchView)
            val tvFound: TextView = dialog.findViewById(R.id.tvFound)
            val progressBar: ProgressBar = dialog.findViewById(R.id.progressBar)
            val progressBarHolder: FrameLayout = dialog.findViewById(R.id.progressBarHolder)
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                }
                false
            }

            p = Properties()
            BWSApplication.addToSegment("Country List Viewed", p, CONSTANTS.screen)

            searchView.onActionViewExpanded()
            searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            searchEditText.setTextColor(ContextCompat.getColor(activity, R.color.gray))
            searchEditText.setHintTextColor(ContextCompat.getColor(activity, R.color.gray))
            val closeButton: ImageView = searchView.findViewById(R.id.search_close_btn)
            searchView.clearFocus()

            closeButton.setOnClickListener {
                searchView.clearFocus()
                searchEditText.setText("")
                searchView.setQuery("", false)
            }

            searchEditText.hint = "Search for country"

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(search: String): Boolean {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                    return false
                }

                override fun onQueryTextChange(search: String): Boolean {
                    try {
                        adapter.filter.filter(search)
                        searchFilter = search
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    return false
                }
            })

            prepareCountryData(dialog, rvCountryList, tvFound, progressBar, progressBarHolder)
            dialog.show()
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
        }

/*
        binding.tvForgotPswd.setOnClickListener {
            val i = Intent(this@SignInActivity, ForgotPswdActivity::class.java)
            startActivity(i)
        }
*/

        if (binding.etPassword.text.toString().trim().equals("", ignoreCase = true)) {
            binding.ivVisible.isClickable = false
            binding.ivVisible.isEnabled = false
            binding.ivVisible.setColorFilter(ContextCompat.getColor(activity, R.color.light_gray), PorterDuff.Mode.SRC_IN)
            binding.ivInVisible.isClickable = false
            binding.ivInVisible.isEnabled = false
        }

        binding.ivVisible.visibility = View.VISIBLE
        binding.ivInVisible.visibility = View.GONE
        binding.ivVisible.setOnClickListener {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivVisible.visibility = View.GONE
            binding.ivInVisible.visibility = View.VISIBLE
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
        }
        binding.ivInVisible.setOnClickListener {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivVisible.visibility = View.VISIBLE
            binding.ivInVisible.visibility = View.GONE
            binding.etPassword.setSelection(binding.etPassword.text.toString().length)
        }

        binding.btnSignIn.setOnClickListener {
            prepareData()
        }
    }

    fun prepareCountryData(dialog: Dialog, rvCountryList: RecyclerView, tvFound: TextView, progressBar: ProgressBar, progressBarHolder: FrameLayout) {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(progressBar, progressBarHolder, activity)
            val listCall: Call<CountryListModel> = APINewClient.client.countryLists
            listCall.enqueue(object : Callback<CountryListModel> {
                override fun onResponse(call: Call<CountryListModel>, response: Response<CountryListModel>) {
                    try {
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity)
                        val listModel: CountryListModel = response.body()!!
                        rvCountryList.layoutManager = LinearLayoutManager(activity)
                        adapter = CountrySelectAdapter(dialog, binding, listModel.responseData!!, rvCountryList, tvFound)
                        rvCountryList.adapter = adapter
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CountryListModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(progressBar, null, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    @SuppressLint("HardwareIds", "SetTextI18n")
    fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            val countryCode: String = binding.tvCountry.text.toString().replace("+", "")
            val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
            fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")!!
            if (TextUtils.isEmpty(fcmId)) {
                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(this) { task: Task<InstallationTokenResult> ->
                    val newToken = task.result!!.token
                    Log.e("newToken", newToken)
                    val editor = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit()
                    editor.putString(CONSTANTS.Token, newToken) //Friend
                    editor.apply()
                    editor.commit()
                }
                val sharedPreferences3 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
                fcmId = sharedPreferences3.getString(CONSTANTS.Token, "")!!
            }
            if (binding.etNumber.text.toString().equals("", ignoreCase = true)) {
                binding.txtNumberError.visibility = View.VISIBLE
                binding.txtNumberError.text = "Please provide a mobile number"
            } else if (binding.etNumber.text.toString().length == 1 || binding.etNumber.text.toString().length < 8 || binding.etNumber.text.toString().length > 10) {
                binding.txtNumberError.visibility = View.VISIBLE
                binding.txtNumberError.text = getString(R.string.valid_mobile_number)
            } else {
                val shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE)
                var key: String = shared1.getString(CONSTANTS.PREF_KEY_SplashKey, "").toString()
                if (key.equals("", ignoreCase = true)) {
                    key = getKey(applicationContext)
                }

                binding.txtNumberError.visibility = View.GONE
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, this@SignInActivity)
                val listCall: Call<UserAccessModel> = APINewClient.client.getUserAccess(binding.etNumber.text.toString(), countryCode, CONSTANTS.FLAG_ONE, CONSTANTS.FLAG_ZERO, key)
                listCall.enqueue(object : Callback<UserAccessModel> {
                    override fun onResponse(call: Call<UserAccessModel>, response: Response<UserAccessModel>) {
                        try {
                            binding.txtNumberError.visibility = View.GONE
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@SignInActivity)
                            val listModel: UserAccessModel = response.body()!!
                            if (listModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                val i = Intent(this@SignInActivity, AuthOtpActivity::class.java)
                                i.putExtra(CONSTANTS.mobileNumber, binding.etNumber.text.toString())
                                i.putExtra(CONSTANTS.countryCode, countryCode)
                                i.putExtra(CONSTANTS.signupFlag, CONSTANTS.FLAG_ZERO)
                                i.putExtra(CONSTANTS.name, "")
                                i.putExtra(CONSTANTS.email, "")
                                i.putExtra(CONSTANTS.countryShortName, binding.tvCountryShortName.text.toString())
                                i.putExtra(CONSTANTS.countryName,countryFullName)
                                startActivity(i)
                                BWSApplication.showToast(listModel.ResponseMessage, activity)

                                val p = Properties()
                                p.putValue("name", name)
                                p.putValue("mobileNo", listModel.ResponseData.MobileNo)
                                p.putValue("countryCode", countryCode)
                                p.putValue("countryName", countryFullName)
                                p.putValue("countryShortName",countryShortName)
                                p.putValue("email", email)
                                p.putValue("source","Login")
                                BWSApplication.addToSegment("Send OTP Clicked", p, CONSTANTS.track)
                            } else {
                                BWSApplication.showToast(listModel.ResponseMessage, activity)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<UserAccessModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, this@SignInActivity)
                    }
                })

            }
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    class CountrySelectAdapter(private var dialog: Dialog, private var binding: ActivitySignInBinding, private val modelList: List<CountryListModel.ResponseData>, private var rvCountryList: RecyclerView, private var tvFound: TextView) : RecyclerView.Adapter<CountrySelectAdapter.MyViewHolder>(), Filterable {
        private var listFilterData: List<CountryListModel.ResponseData>
        var catList = SignInActivity()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: CountryPopupLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.country_popup_layout, parent, false)
            return MyViewHolder(v)
        }

        init {
            listFilterData = modelList
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val mData: CountryListModel.ResponseData = listFilterData[position]
            holder.bindingAdapter.tvCountryName.text = mData.name
            holder.bindingAdapter.tvCountryCode.text = "+" + mData.code
            holder.bindingAdapter.llMainLayout.setOnClickListener {
                binding.tvCountryShortName.text = mData.shortName
                binding.tvCountry.text = "+" + mData.code
                catList.countryFullName = mData.name!!

                dialog.dismiss()
            }
        }

        override fun getItemCount(): Int {
            return listFilterData.size
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val filterResults = FilterResults()
                    val charString = charSequence.toString()
                    listFilterData = if (charString.isEmpty()) {
                        modelList
                    } else {
                        val filteredList: MutableList<CountryListModel.ResponseData> = ArrayList<CountryListModel.ResponseData>()
                        for (row in modelList) {
                            if (row.name!!.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                                filteredList.add(row)
                            }
                        }
                        filteredList
                    }
                    filterResults.values = listFilterData
                    return filterResults
                }

                @SuppressLint("SetTextI18n")
                override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                    if (listFilterData.isEmpty()) {
                        tvFound.visibility = View.VISIBLE
                        tvFound.text = "Sorry we are not available in this country yet"
                        rvCountryList.visibility = View.GONE
                    } else {
                        tvFound.visibility = View.GONE
                        rvCountryList.visibility = View.VISIBLE
                        listFilterData = filterResults.values as List<CountryListModel.ResponseData>
                        notifyDataSetChanged()
                    }
                }
            }
        }

        inner class MyViewHolder(var bindingAdapter: CountryPopupLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root)
    }

}


