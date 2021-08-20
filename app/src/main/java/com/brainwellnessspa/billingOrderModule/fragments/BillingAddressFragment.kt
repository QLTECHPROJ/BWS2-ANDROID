package com.brainwellnessspa.billingOrderModule.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.BillingOrderActivity
import com.brainwellnessspa.billingOrderModule.models.BillingAddressSaveModel
import com.brainwellnessspa.billingOrderModule.models.BillingAddressViewModel
import com.brainwellnessspa.databinding.FragmentBillingAddressBinding
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BillingAddressFragment : Fragment() {
    lateinit var binding: FragmentBillingAddressBinding
    var userId: String? = null
    var coUserId: String? = null
    var userName: String? = null
    var userEmail: String? = null
    var userMobileNumber: String? = null
    var userCountry: String? = null
    var userAddressLine1: String? = null
    var userAddressLine2: String? = null
    var userCity: String? = null
    var userState: String? = null
    var userPostCode: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_billing_address, container, false)
        val view = binding.root
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val shared1: SharedPreferences = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        binding.etName.addTextChangedListener(billingTextWatcher)
        binding.etEmail.addTextChangedListener(billingTextWatcher)
        binding.etMobileNumber.addTextChangedListener(billingTextWatcher)
        binding.etCountry.addTextChangedListener(billingTextWatcher)
        binding.etAddressLine1.addTextChangedListener(billingTextWatcher)
        binding.etAddressLine2.addTextChangedListener(billingTextWatcher)
        binding.etCity.addTextChangedListener(billingTextWatcher)
        binding.etState.addTextChangedListener(billingTextWatcher)
        binding.etPostCode.addTextChangedListener(billingTextWatcher)
        binding.btnSave.isEnabled = false
        binding.btnSave.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
        binding.btnSave.setOnClickListener {
            if (BWSApplication.isNetworkConnected(activity)) {
                binding.tlName.error = ""
                binding.tlEmail.error = ""
                binding.tlMobileNumber.error = ""
                binding.tlCountry.error = ""
                binding.tlAddressLine1.error = ""
                binding.tlCity.error = ""
                binding.tlState.error = ""
                binding.tlPostCode.error = ""
                if (binding.etName.text.toString().equals("", ignoreCase = true)) {
                    binding.tlName.error = "Name is required"
                } else if (binding.etEmail.text.toString().equals("", ignoreCase = true)) {
                    binding.tlEmail.error = "Email address is required"
                } else if (!binding.etEmail.text.toString().equals("", ignoreCase = true) && !BWSApplication.isEmailValid(binding.etEmail.text.toString())) {
                    binding.tlEmail.error = "Please enter a valid email address"
                } else if (binding.etMobileNumber.text.toString().equals("", ignoreCase = true)) {
                    binding.tlMobileNumber.error = "please enter mobile number"
                } else if (binding.etCountry.text.toString().equals("", ignoreCase = true)) {
                    binding.tlCountry.error = "Country is required"
                } else if (binding.etAddressLine1.text.toString().equals("", ignoreCase = true)) {
                    binding.tlAddressLine1.error = "Address Line is required"
                } else if (binding.etCity.text.toString().equals("", ignoreCase = true)) {
                    binding.tlCity.error = "Suburb / Town / City is required"
                } else if (binding.etState.text.toString().equals("", ignoreCase = true)) {
                    binding.tlState.error = "State is required"
                } else if (binding.etPostCode.text.toString().equals("", ignoreCase = true)) {
                    binding.tlPostCode.error = "Postcode is required"
                } else {
                    BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    val listCall = APIClient.client.getBillingAddressSave(userId, binding.etName.text.toString(), binding.etEmail.text.toString(), binding.etCountry.text.toString(), binding.etAddressLine1.text.toString(), binding.etAddressLine2.text.toString(), binding.etCity.text.toString(), binding.etState.text.toString(), binding.etPostCode.text.toString())
                    listCall!!.enqueue(object : Callback<BillingAddressSaveModel?> {
                        override fun onResponse(call: Call<BillingAddressSaveModel?>, response: Response<BillingAddressSaveModel?>) {
                            val listModel = response.body()
                            if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                try {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                    BWSApplication.showToast(listModel.responseMessage, activity)
                                    /*Properties p = new Properties();
                                    BWSApplication.addToSegment("Billing Address Updated", p, CONSTANTS.track);*/BillingOrderActivity.myBackPressbill = true
                                    activity!!.finish()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        override fun onFailure(call: Call<BillingAddressSaveModel?>, t: Throwable) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        }
                    })
                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        }
        return view
    }

    override fun onResume() {
        prepareData
        super.onResume()
    }

    private val prepareData: Unit
        get() {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APIClient.client.getBillingAddressView(userId)
            listCall!!.enqueue(object : Callback<BillingAddressViewModel?> {
                override fun onResponse(call: Call<BillingAddressViewModel?>, response: Response<BillingAddressViewModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (listModel.responseData!!.name.equals("", ignoreCase = true) || listModel.responseData!!.name.equals(" ", ignoreCase = true) || listModel.responseData!!.name == null) {
                                binding.etName.setText("")
                            } else {
                                binding.etName.setText(listModel.responseData!!.name)
                            }
                            userName = listModel.responseData!!.name
                            userEmail = listModel.responseData!!.email
                            userMobileNumber = listModel.responseData!!.phoneNumber
                            userCountry = listModel.responseData!!.country
                            userAddressLine1 = listModel.responseData!!.address1
                            userAddressLine2 = listModel.responseData!!.address2
                            userCity = listModel.responseData!!.suburb
                            userState = listModel.responseData!!.state
                            userPostCode = listModel.responseData!!.postcode
                            binding.etEmail.setText(listModel.responseData!!.email)
                            binding.etMobileNumber.setText(listModel.responseData!!.phoneNumber)
                            binding.etMobileNumber.isEnabled = false
                            binding.etMobileNumber.isClickable = false
                            binding.etCountry.setText(listModel.responseData!!.country)
                            binding.etAddressLine1.setText(listModel.responseData!!.address1)
                            binding.etAddressLine2.setText(listModel.responseData!!.address2)
                            binding.etCity.setText(listModel.responseData!!.suburb)
                            binding.etState.setText(listModel.responseData!!.state)
                            binding.etPostCode.setText(listModel.responseData!!.postcode)
                            val p = Properties()
                            p.putValue("fullName", listModel.responseData!!.name)
                            p.putValue("emailId", listModel.responseData!!.email)
                            p.putValue("mobile", listModel.responseData!!.phoneNumber)
                            p.putValue("country", listModel.responseData!!.country)
                            p.putValue("address1", listModel.responseData!!.address1)
                            p.putValue("address2", listModel.responseData!!.address2)
                            p.putValue("suburb", listModel.responseData!!.suburb)
                            p.putValue("state", listModel.responseData!!.state)
                            p.putValue("postcode", listModel.responseData!!.postcode)
                            BWSApplication.addToSegment("Billing Address Screen Viewed", p, CONSTANTS.screen)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<BillingAddressViewModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        }
    private val billingTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val mobileNumber = binding.etMobileNumber.text.toString()
            val country = binding.etCountry.text.toString()
            val addressLine1 = binding.etAddressLine1.text.toString()
            val addressLine2 = binding.etAddressLine2.text.toString()
            val city = binding.etCity.text.toString()
            val state = binding.etState.text.toString()
            val postCode = binding.etPostCode.text.toString()
            if (name.equals(userName, ignoreCase = true) && email.equals(userEmail, ignoreCase = true) && mobileNumber.equals(userMobileNumber, ignoreCase = true) && country.equals(userCountry, ignoreCase = true) && addressLine1.equals(userAddressLine1, ignoreCase = true) && addressLine2.equals(userAddressLine2, ignoreCase = true) && city.equals(userCity, ignoreCase = true) && state.equals(userState, ignoreCase = true) && postCode.equals(userPostCode, ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (!name.equals(userName, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!email.equals(userEmail, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!mobileNumber.equals(userMobileNumber, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!country.equals(userCountry, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!addressLine1.equals(userAddressLine1, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!addressLine2.equals(userAddressLine2, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!city.equals(userCity, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!state.equals(userState, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (!postCode.equals(userPostCode, ignoreCase = true)) {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else if (name.equals(userName, ignoreCase = true) && email.equals(userEmail, ignoreCase = true) && mobileNumber.equals(userMobileNumber, ignoreCase = true) && country.equals(userCountry, ignoreCase = true) && addressLine1.equals(userAddressLine1, ignoreCase = true) && addressLine2.equals("", ignoreCase = true) && userAddressLine2.equals("", ignoreCase = true) && city.equals(userCity, ignoreCase = true) && state.equals(userState, ignoreCase = true) && postCode.equals(userPostCode, ignoreCase = true)) {
                binding.btnSave.isEnabled = false
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                binding.btnSave.isEnabled = true
                binding.btnSave.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnSave.setBackgroundResource(R.drawable.light_green_rounded_filled)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
}