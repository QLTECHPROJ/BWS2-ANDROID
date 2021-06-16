package com.brainwellnessspa.userModuleTwo.activities

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
import android.provider.Settings
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
import com.brainwellnessspa.R
import com.brainwellnessspa.userModuleTwo.models.CountryListModel
import com.brainwellnessspa.userModuleTwo.models.NewSignUpModel
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.webView.TncActivity
import com.brainwellnessspa.databinding.ActivityCreateAccountBinding
import com.brainwellnessspa.databinding.CountryPopupLayoutBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var dialog: Dialog
    lateinit var adapter: CountrySelectAdapter
    var searchFilter: String = ""
    lateinit var ctx: Context
    lateinit var activity: Activity
    var fcm_id: String = ""
    var countryFullName: String = ""
    lateinit var searchEditText: EditText

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val ckName: String = binding.etUser.text.toString().trim()
            val ckNumber: String = binding.etNumber.text.toString().trim()
            val ckEmail: String = binding.etEmail.text.toString().trim()
            val ckPass: String = binding.etPassword.text.toString().trim()
            when {
                ckName.equals("", ignoreCase = true) -> {
                    binding.btnCreateAc.isEnabled = false
                    binding.btnCreateAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnCreateAc.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                ckNumber.equals("", ignoreCase = true) -> {
                    binding.btnCreateAc.isEnabled = false
                    binding.btnCreateAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnCreateAc.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                ckEmail.equals("", ignoreCase = true) -> {
                    binding.btnCreateAc.isEnabled = false
                    binding.btnCreateAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnCreateAc.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                ckPass.equals("", ignoreCase = true) -> {
                    binding.btnCreateAc.isEnabled = false
                    binding.btnCreateAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnCreateAc.setBackgroundResource(R.drawable.gray_round_cornor)
                }
                else -> {
                    binding.btnCreateAc.isEnabled = true
                    binding.btnCreateAc.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnCreateAc.setBackgroundResource(R.drawable.light_green_rounded_filled)
                }
            }
            if (ckPass.equals("", ignoreCase = true)) {
                binding.ivVisible.isClickable = false
                binding.ivVisible.isEnabled = false
                binding.ivVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.light_gray),
                    PorterDuff.Mode.SRC_IN
                )
                binding.ivInVisible.isClickable = false
                binding.ivInVisible.isEnabled = false
            } else {
                binding.ivVisible.isClickable = true
                binding.ivVisible.isEnabled = true
                binding.ivVisible.setColorFilter(
                    ContextCompat.getColor(activity, R.color.black),
                    PorterDuff.Mode.SRC_IN
                )
                binding.ivInVisible.isClickable = true
                binding.ivInVisible.isEnabled = true
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)
        ctx = this@CreateAccountActivity
        activity = this@CreateAccountActivity
        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.llBack.setOnClickListener {
            val i = Intent(activity, GetStartedActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.etUser.addTextChangedListener(userTextWatcher)
        binding.etNumber.addTextChangedListener(userTextWatcher)
        binding.etEmail.addTextChangedListener(userTextWatcher)
        binding.etPassword.addTextChangedListener(userTextWatcher)
        val p = Properties()
        BWSApplication.addToSegment("Sign up Screen Viewed", p, CONSTANTS.screen)

        if (binding.etPassword.text.toString().trim().equals("", ignoreCase = true)) {
            binding.ivVisible.isClickable = false
            binding.ivVisible.isEnabled = false
            binding.ivVisible.setColorFilter(
                ContextCompat.getColor(activity, R.color.light_gray),
                PorterDuff.Mode.SRC_IN
            )
            binding.ivInVisible.isClickable = false
            binding.ivInVisible.isEnabled = false
        }

        binding.btnCreateAc.setOnClickListener {
            if (binding.etUser.text.toString().equals("", ignoreCase = true)) {
                binding.txtNameError.text = "Please provide a Name"
                binding.txtNameError.visibility = View.VISIBLE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.GONE
            } else if (binding.etNumber.text.toString().equals("", ignoreCase = true)) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.VISIBLE
                binding.txtNumberError.text = "Please provide a mobile number"
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.GONE
            } else if (binding.etNumber.text.toString().length == 1 || binding.etNumber.text.toString().length < 8 ||
                binding.etNumber.text.toString().length > 10
            ) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.VISIBLE
                binding.txtNumberError.text = "Please provide a valid mobile number"
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.GONE
            } else if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.VISIBLE
                binding.txtEmailError.text = "Please provide a email address"
                binding.txtPassowrdError.visibility = View.GONE
            } else if (!binding.etEmail.text.toString().isEmailValid()) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.VISIBLE
                binding.txtEmailError.text = "Please provide a valid email address"
                binding.txtPassowrdError.visibility = View.GONE
            } else if (binding.etPassword.text.toString().equals("", ignoreCase = true)) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.VISIBLE
                binding.txtPassowrdError.text = "Please provide a password"
            } else if (binding.etPassword.text.toString().length < 8 || !isValidPassword(binding.etPassword.text.toString())
            ) {
                binding.txtNameError.visibility = View.GONE
                binding.txtNumberError.visibility = View.GONE
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.VISIBLE
                binding.txtPassowrdError.text = "Password should contain at least one uppercase, one lowercase, one special symbol and minimum 8 character long"
            } else {
                SignUpUser()
            }
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
            val p = Properties()
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

            prepareData(
                dialog,
                rvCountryList,
                tvFound,
                progressBar,
                progressBarHolder,
                searchFilter
            )
            dialog.show()
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
        }
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val passwordPattern = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    override fun onBackPressed() {
        val i = Intent(activity, GetStartedActivity::class.java)
        startActivity(i)
        finish()
    }

    fun prepareData(
        dialog: Dialog, rvCountryList: RecyclerView, tvFound: TextView, progressBar: ProgressBar,
        progressBarHolder: FrameLayout, searchFilter: String
    ) {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(progressBar, progressBarHolder, activity)
            val listCall: Call<CountryListModel> = APINewClient.getClient().countryLists
            listCall.enqueue(object : Callback<CountryListModel> {
                override fun onResponse(
                    call: Call<CountryListModel>,
                    response: Response<CountryListModel>
                ) {
                    try {
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity)
                        val listModel: CountryListModel = response.body()!!
                        rvCountryList.layoutManager = LinearLayoutManager(ctx)
                        adapter = CountrySelectAdapter(
                            dialog, searchFilter, binding, listModel.responseData!!, rvCountryList,
                            tvFound
                        )
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

    @SuppressLint("HardwareIds")
    fun SignUpUser() {
        if (BWSApplication.isNetworkConnected(this)) {
            val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
            fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "")!!
            if (TextUtils.isEmpty(fcm_id)) {
                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(
                    this,
                    OnCompleteListener { task: Task<InstallationTokenResult> ->
                        val newToken = task.result!!.token
                        Log.e("newToken", newToken)
                        val editor = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE).edit()
                        editor.putString(CONSTANTS.Token, newToken) //Friend
                        editor.apply()
                        editor.commit()
                    })
                val sharedPreferences3 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
                fcm_id = sharedPreferences3.getString(CONSTANTS.Token, "")!!
            }
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)

            val countryCode: String = binding.tvCountry.text.toString().replace("+", "")
            Log.e("countryCode", countryCode)
            Log.e("countryFullName", countryFullName)
            val listCall: Call<NewSignUpModel> = APINewClient.getClient().getSignUp(
                binding.etUser.text.toString(),
                binding.etEmail.text.toString(),
                countryCode,
                binding.etNumber.text.toString(),
                CONSTANTS.FLAG_ONE,
                binding.etPassword.text.toString(),
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
                fcm_id
            )
            listCall.enqueue(object : Callback<NewSignUpModel> {
                override fun onResponse(
                    call: Call<NewSignUpModel>,
                    response: Response<NewSignUpModel>
                ) {
                    try {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        val listModel: NewSignUpModel = response.body()!!
                        if (listModel.getResponseCode().equals("200")) {
                            val i = Intent(ctx, SignInActivity::class.java)
                            startActivity(i)
                            finish()
                            val p = Properties()
                            p.putValue("userId", listModel.getResponseData()!!.id)
                            p.putValue("name", listModel.getResponseData()!!.name)
                            p.putValue("mobileNo", listModel.getResponseData()!!.mobileNo)
                            p.putValue("countryCode", countryCode)
                            p.putValue("countryName", countryFullName)
                            p.putValue(
                                "countryShortName",
                                binding.tvCountryShortName.text.toString()
                            )
                            p.putValue("email", listModel.getResponseData()!!.email)
                            BWSApplication.addToSegment("User Sign up", p, CONSTANTS.track)
                            /*  val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                              val editor = shared.edit()
                              editor.putString(CONSTANTS.PREFE_ACCESS_UserID, listModel.getResponseData()?.id)
                              editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.getResponseData()?.name)
                              editor.putString(CONSTANTS.PREFE_ACCESS_USEREMAIL, listModel.getResponseData()?.email)
                              editor.putString(CONSTANTS.PREFE_ACCESS_DeviceType, CONSTANTS.FLAG_ONE)
                              editor.putString(CONSTANTS.PREFE_ACCESS_DeviceID, Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
                              editor.commit()*/
                        }
                        BWSApplication.showToast(listModel.getResponseMessage(), activity)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<NewSignUpModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, null, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    class CountrySelectAdapter(
        dialog: Dialog,
        searchFilter: String,
        private var binding: ActivityCreateAccountBinding,
        private val modelList: List<CountryListModel.ResponseData>,
        rvCountryList: RecyclerView,
        tvFound: TextView
    ) : RecyclerView.Adapter<CountrySelectAdapter.MyViewHolder>(), Filterable {
        private var listFilterData: List<CountryListModel.ResponseData>
        private var rvCountryList: RecyclerView
        private var tvFound: TextView
        private var searchFilter: String
        private var dialog: Dialog
        var catList = CreateAccountActivity()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: CountryPopupLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.country_popup_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        init {
            this.rvCountryList = rvCountryList
            this.tvFound = tvFound
            this.searchFilter = searchFilter
            this.dialog = dialog
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
                        val filteredList: MutableList<CountryListModel.ResponseData> =
                            ArrayList<CountryListModel.ResponseData>()
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
                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
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

        inner class MyViewHolder(var bindingAdapter: CountryPopupLayoutBinding) :
            RecyclerView.ViewHolder(bindingAdapter.root)
    }
}