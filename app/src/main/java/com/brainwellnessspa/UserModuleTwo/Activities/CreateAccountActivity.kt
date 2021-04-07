package com.brainwellnessspa.UserModuleTwo.Activities

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
import android.text.TextUtils
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
import com.brainwellnessspa.LoginModule.Models.CountryListModel
import com.brainwellnessspa.LoginModule.Models.LoginModel
import com.brainwellnessspa.R
import com.brainwellnessspa.UserModuleTwo.Models.NewSignUpModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.WebView.TncActivity
import com.brainwellnessspa.databinding.ActivityCreateAccountBinding
import com.brainwellnessspa.databinding.CountryPopupLayoutBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var dialog: Dialog
    lateinit var adapter: CountrySelectAdapter
    var searchFilter: String = ""
    lateinit var ctx: Context
    lateinit var activity: Activity
    var fcm_id: String = ""
    lateinit var searchEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)
        ctx = this@CreateAccountActivity
        activity = this@CreateAccountActivity
        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.llBack.setOnClickListener {
            finish()
        }

        binding.btnCreateAc.setOnClickListener {
            if (binding.etUser.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = "Name is required"
                binding.flNumber.error = ""
                binding.flEmail.error = ""
                binding.flPassword.error = ""
            } else if (binding.etNumber.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flNumber.error = "Number is required"
                binding.flEmail.error = ""
                binding.flPassword.error = ""
            } else if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flNumber.error = ""
                binding.flEmail.error = "Email address is required"
                binding.flPassword.error = ""
            } else if (!binding.etEmail.text.toString().isEmailValid()) {
                binding.flUser.error = ""
                binding.flNumber.error = ""
                binding.flEmail.error = "Valid Email address is required"
                binding.flPassword.error = ""
            } else if (binding.etPassword.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flNumber.error = ""
                binding.flEmail.error = ""
                binding.flPassword.error = "Password is required"
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
        }
        binding.ivInVisible.setOnClickListener {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivVisible.visibility = View.VISIBLE
            binding.ivInVisible.visibility = View.GONE
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
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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
            tvClose.setOnClickListener { _: View? -> dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(false)
        }

        binding.tvCountry.setOnClickListener {
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

            searchView.onActionViewExpanded()
            searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            searchEditText.setTextColor(ContextCompat.getColor(activity, R.color.gray))
            searchEditText.setHintTextColor(ContextCompat.getColor(activity, R.color.gray))
            val closeButton: ImageView = searchView.findViewById(R.id.search_close_btn)
            searchView.clearFocus()

            closeButton.setOnClickListener { _: View? ->
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

            prepareData(dialog, rvCountryList, tvFound, progressBar, progressBarHolder)
            dialog.show()
            dialog.setCancelable(false)
        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun prepareData(dialog: Dialog, rvCountryList: RecyclerView, tvFound: TextView, progressBar: ProgressBar, progressBarHolder: FrameLayout) {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(progressBar, progressBarHolder, activity)
            val listCall: Call<CountryListModel> = APINewClient.getClient().countryLists
            listCall.enqueue(object : Callback<CountryListModel> {
                override fun onResponse(call: Call<CountryListModel>, response: Response<CountryListModel>) {
                    try {
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity)
                        val listModel: CountryListModel = response.body()!!
                        rvCountryList.layoutManager = LinearLayoutManager(ctx)
                        adapter = CountrySelectAdapter(dialog, searchFilter, binding, listModel.responseData, rvCountryList, tvFound)
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

    fun SignUpUser() {
        if (BWSApplication.isNetworkConnected(this)) {
            val sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, MODE_PRIVATE)
            fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "")!!
            if (TextUtils.isEmpty(fcm_id)) {
                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(this, OnCompleteListener { task: Task<InstallationTokenResult> ->
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

            val countryCode: String = binding.tvCountry.getText().toString().replace("+", "")
            Log.e("countryCode", countryCode);
            val listCall: Call<NewSignUpModel> = APINewClient.getClient().getSignUp(binding.etUser.text.toString(), binding.etEmail.text.toString(), countryCode, binding.etNumber.text.toString(), "1", binding.etPassword.text.toString(),
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID), fcm_id)
            listCall.enqueue(object : Callback<NewSignUpModel> {
                override fun onResponse(call: Call<NewSignUpModel>, response: Response<NewSignUpModel>) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: NewSignUpModel = response.body()!!
                        if (listModel.getResponseCode().equals("200")) {
                            val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREFE_ACCESS_UserID, listModel.getResponseData()?.id)
                            editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.getResponseData()?.name)
                            editor.commit()
                            val i = Intent(ctx, UserListActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                        BWSApplication.showToast(listModel.getResponseMessage(), ctx)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<NewSignUpModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, null, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx)
        }
    }

    class CountrySelectAdapter(dialog: Dialog, searchFilter: String, binding: ActivityCreateAccountBinding,
                               modelList: List<CountryListModel.ResponseData>, rvCountryList: RecyclerView, tvFound: TextView)
        : RecyclerView.Adapter<CountrySelectAdapter.MyViewHolder>(), Filterable {
        private val modelList: List<CountryListModel.ResponseData>
        private var listFilterData: List<CountryListModel.ResponseData>
        private var rvCountryList: RecyclerView
        private var tvFound: TextView
        private var binding: ActivityCreateAccountBinding
        private var searchFilter: String
        private var dialog: Dialog
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: CountryPopupLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.country_popup_layout, parent, false)
            return MyViewHolder(v)
        }

        init {
            this.binding = binding
            this.modelList = modelList
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
            holder.bindingAdapter.llMainLayout.setOnClickListener { _ ->
                binding.tvCountryShortName.text = mData.shortName
                binding.tvCountry.text = "+" + mData.code
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
                            if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }
                        filteredList
                    }
                    filterResults.values = listFilterData
                    return filterResults
                }

                override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                    if (listFilterData.size == 0) {
//                        tvFound.setVisibility(View.VISIBLE)
//                        tvFound.setText("Couldn't find $searchFilter. Try searching again")
//                        rvCountryList.setVisibility(View.GONE)
                    } else {
//                        tvFound.setVisibility(View.GONE)
//                        rvCountryList.setVisibility(View.VISIBLE)
                        listFilterData = filterResults.values as List<CountryListModel.ResponseData>
                        notifyDataSetChanged()
                    }
                }
            }
        }

        inner class MyViewHolder(bindingAdapter: CountryPopupLayoutBinding) : RecyclerView.ViewHolder(bindingAdapter.root) {
            var bindingAdapter: CountryPopupLayoutBinding

            init {
                this.bindingAdapter = bindingAdapter
            }
        }
    }
}