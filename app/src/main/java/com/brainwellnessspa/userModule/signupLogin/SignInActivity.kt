package com.brainwellnessspa.userModule.signupLogin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
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
import com.brainwellnessspa.databinding.ActivityCreateAccountBinding
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivitySignInBinding
import com.brainwellnessspa.databinding.CountryPopupLayoutBinding
import com.brainwellnessspa.userModule.activities.ForgotPswdActivity
import com.brainwellnessspa.userModule.activities.UserListActivity
import com.brainwellnessspa.userModule.models.CountryListModel
import com.brainwellnessspa.userModule.models.SignInModel
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

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    var fcmId: String = ""
    lateinit var adapter: CountrySelectAdapter
    var searchFilter: String = ""
    lateinit var activity: Activity
    private lateinit var dialog: Dialog
    lateinit var searchEditText: EditText

    private var userTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val number: String = binding.etNumber.text.toString().trim()
            when {
                number.equals("", ignoreCase = true) -> {
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(activity, R.color.white))
                    binding.btnSignIn.setBackgroundResource(R.drawable.light_green_rounded_filled)
//                    binding.btnSignIn.isEnabled = false
//                    binding.btnSignIn.setTextColor(ContextCompat.getColor(activity, R.color.white))
//                    binding.btnSignIn.setBackgroundResource(R.drawable.gray_round_cornor)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        activity = this@SignInActivity
        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.llBack.setOnClickListener {
            finish()
        }
        val p = Properties()
        BWSApplication.addToSegment("Login Screen Viewed", p, CONSTANTS.screen)
        binding.etEmail.addTextChangedListener(userTextWatcher)
        binding.etPassword.addTextChangedListener(userTextWatcher)

        binding.tvSignUp.setOnClickListener {
            val i = Intent(activity, SignUpActivity::class.java)
            startActivity(i)
            finish()
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

            prepareCountryData(
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

/*
        binding.tvForgotPswd.setOnClickListener {
            val i = Intent(this@SignInActivity, ForgotPswdActivity::class.java)
            startActivity(i)
        }
*/

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
            val i = Intent(activity, AuthOtpActivity::class.java)
            startActivity(i)
            finish()
//            prepareData()
        }
    }

    fun prepareCountryData(
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
                        rvCountryList.layoutManager = LinearLayoutManager(activity)
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

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val passwordPatterned = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPatterned)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    override fun onBackPressed() {
        finish()
    }

    @SuppressLint("HardwareIds")
    fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
            fcmId = sharedPreferences2.getString(CONSTANTS.Token, "")!!
            if (TextUtils.isEmpty(fcmId)) {
                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(
                    this
                ) { task: Task<InstallationTokenResult> ->
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
            if (binding.etEmail.text.toString() == "") {
                binding.txtEmailError.visibility = View.VISIBLE
                binding.txtEmailError.text = getString(R.string.please_provide_a_email_address)
                binding.txtPassowrdError.visibility = View.GONE
            } else if (binding.etEmail.text.toString() != ""
                && !BWSApplication.isEmailValid(binding.etEmail.text.toString())
            ) {
                binding.txtEmailError.visibility = View.VISIBLE
                binding.txtEmailError.text = getString(R.string.please_provide_a_email_address)
                binding.txtPassowrdError.visibility = View.GONE
            } else if (binding.etPassword.text.toString() == "") {
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.VISIBLE
                binding.txtPassowrdError.text = "Please provide a password"
            } else if (binding.etPassword.text.toString().length < 8
                || !isValidPassword(binding.etPassword.text.toString())
            ) {
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.VISIBLE
                binding.txtPassowrdError.text =
                    "Password should contain at least one uppercase, one lowercase, one special symbol and minimum 8 character long"
            } else {
                binding.txtEmailError.visibility = View.GONE
                binding.txtPassowrdError.visibility = View.GONE
                BWSApplication.showProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    this@SignInActivity
                )
                val listCall: Call<SignInModel> = APINewClient.getClient().getSignIn(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    CONSTANTS.FLAG_ONE,
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
                    fcmId
                )
                listCall.enqueue(object : Callback<SignInModel> {
                    override fun onResponse(
                        call: Call<SignInModel>,
                        response: Response<SignInModel>
                    ) {
                        try {
                            binding.txtEmailError.visibility = View.GONE
                            binding.txtPassowrdError.visibility = View.GONE
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                this@SignInActivity
                            )
                            val listModel: SignInModel = response.body()!!
                            if (listModel.getResponseCode().equals(
                                    getString(R.string.ResponseCodesuccess),
                                    ignoreCase = true
                                )
                            ) {
                                val shared = getSharedPreferences(
                                    CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
                                    MODE_PRIVATE
                                )
                                val editor = shared.edit()
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_mainAccountID,
                                    listModel.getResponseData()?.iD
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_NAME,
                                    listModel.getResponseData()?.name
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_USEREMAIL,
                                    listModel.getResponseData()?.email
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_DeviceType,
                                    CONSTANTS.FLAG_ONE
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_DeviceID,
                                    Settings.Secure.getString(
                                        contentResolver,
                                        Settings.Secure.ANDROID_ID
                                    )
                                )
                                editor.putString(
                                    CONSTANTS.PREFE_ACCESS_countryCode,
                                    listModel.getResponseData()?.email
                                )
                                editor.apply()
                                val i = Intent(this@SignInActivity, AuthOtpActivity::class.java)
                                startActivity(i)
                                finish()
                                BWSApplication.showToast(listModel.getResponseMessage(), activity)

                                val p = Properties()
                                p.putValue("userId", listModel.getResponseData()!!.iD)
                                p.putValue("name", listModel.getResponseData()!!.name)
                                p.putValue("mobileNo", listModel.getResponseData()!!.mobileNo)
                                p.putValue("email", listModel.getResponseData()!!.email)
                                BWSApplication.addToSegment("User Login", p, CONSTANTS.track)
                            } else {
                                BWSApplication.showToast(listModel.getResponseMessage(), activity)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<SignInModel>, t: Throwable) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            this@SignInActivity
                        )
                    }
                })

            }
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
        }
    }

    class CountrySelectAdapter(
        private var dialog: Dialog,
        private var searchFilter: String,
        private var binding: ActivitySignInBinding,
        private val modelList: List<CountryListModel.ResponseData>,
        private var rvCountryList: RecyclerView,
        private var tvFound: TextView
    ) : RecyclerView.Adapter<CountrySelectAdapter.MyViewHolder>(), Filterable {
        private var listFilterData: List<CountryListModel.ResponseData>
        var catList = SignUpActivity()
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
                            if (row.name!!.toLowerCase(Locale.getDefault())
                                    .contains(charString.toLowerCase(Locale.getDefault()))
                            ) {
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


