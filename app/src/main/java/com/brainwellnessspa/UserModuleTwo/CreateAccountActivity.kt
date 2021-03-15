package com.brainwellnessspa.UserModuleTwo

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.LoginModule.Models.CountryListModel
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.APIClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.WebView.TncActivity
import com.brainwellnessspa.databinding.ActivityCreateAccountBinding
import com.brainwellnessspa.databinding.CountryPopupLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var dialog: Dialog
    lateinit var adapter: CountrySelectAdapter
    var searchFilter: String = ""
    lateinit var searchEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)
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
            } else if (binding.etPassword.text.toString().equals("", ignoreCase = true)) {
                binding.flUser.error = ""
                binding.flNumber.error = ""
                binding.flEmail.error = ""
                binding.flPassword.error = "Password is required"
            } else {
                val i = Intent(this@CreateAccountActivity, UserListActivity::class.java)
                i.putExtra(CONSTANTS.PopUp,"0")
                startActivity(i)
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
            dialog.setOnKeyListener { v: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            tvClose.setOnClickListener { v: View? -> dialog.dismiss() }
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
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                }
                false
            }

            searchView.onActionViewExpanded()
            searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            searchEditText.setTextColor(resources.getColor(R.color.gray))
            searchEditText.setHintTextColor(resources.getColor(R.color.gray))
            val closeButton: ImageView = searchView.findViewById(R.id.search_close_btn)
            searchView.clearFocus()

            closeButton.setOnClickListener { view: View? ->
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

    fun prepareData(dialog: Dialog, rvCountryList: RecyclerView, tvFound: TextView, progressBar: ProgressBar, progressBarHolder: FrameLayout) {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(progressBar, progressBarHolder, this@CreateAccountActivity)
            val listCall: Call<CountryListModel> = APIClient.getClient().countryLists
            listCall.enqueue(object : Callback<CountryListModel> {
                override fun onResponse(call: Call<CountryListModel>, response: Response<CountryListModel>) {
                    try {
                        BWSApplication.hideProgressBar(progressBar, progressBarHolder, this@CreateAccountActivity)
                        val listModel: CountryListModel = response.body()!!
                        rvCountryList.layoutManager = LinearLayoutManager(this@CreateAccountActivity)
                        adapter = CountrySelectAdapter(dialog, searchFilter, binding, listModel.responseData, rvCountryList, tvFound)
                        rvCountryList.adapter = adapter
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CountryListModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(progressBar, null, this@CreateAccountActivity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this)
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

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val mData: CountryListModel.ResponseData = listFilterData[position]
            holder.bindingAdapter.tvCountryName.text = mData.name
            holder.bindingAdapter.tvCountryCode.text = "+" + mData.code
            holder.bindingAdapter.llMainLayout.setOnClickListener { _ ->
                var conutry = "+" + mData.code
                var cyname = mData.name.substring(0, 2)
                binding.tvCountry.text = "$cyname $conutry"
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