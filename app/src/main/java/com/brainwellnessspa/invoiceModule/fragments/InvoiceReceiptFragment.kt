package com.brainwellnessspa.invoiceModule.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.invoiceModule.activities.InvoiceActivity
import com.brainwellnessspa.invoiceModule.models.InvoiceDetailModel
import com.brainwellnessspa.R
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.FragmentInvoiceReceiptBinding
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvoiceReceiptFragment : DialogFragment() {
    lateinit var binding: FragmentInvoiceReceiptBinding
    var userID: String? = ""
    var invoiceID: String? = ""
    var flag: String? = ""
    var invoiceAmount: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_invoice_receipt, container, false)
        InvoiceActivity.invoiceToRecepit = 1
        val shared1 =
            requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userID = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog!!.window!!.setBackgroundDrawableResource(R.drawable.receipt_dialog_background_inset)
        }
        return binding.root
    }

    override fun onResume() {
        InvoiceActivity.invoiceToRecepit = 1
        requireView().setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
                InvoiceActivity.invoiceToRecepit = 1
                val i = Intent(activity, InvoiceActivity::class.java)
                i.putExtra("ComeFrom", "")
                startActivity(i)
                requireActivity().finish()
                return@setOnKeyListener true
            }
            false
        }
        prepareData()
        super.onResume()
    }

    fun setValues(InvoiceId: String?, flag: String?) {
        invoiceID = InvoiceId
        this.flag = flag
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(activity)) {
            BWSApplication.showProgressBar(
                binding.progressBar,
                binding.progressBarHolder,
                activity
            )
            val listCall = APIClient.getClient().getInvoiceDetailPlaylist(
                userID,
                invoiceID,
                "1"
            ) /*Flag = 0 Staging Flag = 1 Live*/
            listCall.enqueue(object : Callback<InvoiceDetailModel> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<InvoiceDetailModel>,
                    response: Response<InvoiceDetailModel>
                ) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            if (listModel.responseData != null){
                                invoiceAmount = "$" + listModel.responseData!!.totalAmount
                                binding.tvFromTitle.text = "From"
                                binding.tvDateTitle.text = "Order Date:"
                                binding.tvOrderIdTitle.text = "Order #:"
                                binding.tvTotalTitle.text = "Order Total:"
                                binding.tvItemsTitle.text = "Items:"
                                binding.tvGstTitle.text = "GST:"
                                binding.tvOrderTotalAmountTitle.text = "Order Total:"
                                try {
                                    val p = Properties()
                                    p.putValue("userId", userID)
                                    p.putValue("invoiceId", invoiceID)
                                    if (flag.equals("1", ignoreCase = true)) {
                                        p.putValue("invoiceType", "Memebrship")
                                    } else if (flag.equals("2", ignoreCase = true)) {
                                        p.putValue("invoiceType", "Appointment")
                                    }
                                    p.putValue("invoiceAmount", listModel.responseData!!.amount)
                                    p.putValue("invoiceDate", listModel.responseData!!.invoiceDate)
                                    p.putValue("invoiceCurrency", "")
                                    p.putValue("plan", "")
                                    p.putValue("planStartDt", "")
                                    p.putValue("planExpiryDt", "")
                                    BWSApplication.addToSegment("Invoice Clicked", p, CONSTANTS.track)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (flag.equals("1", ignoreCase = true)) {
                                    binding.tvSession.visibility = View.GONE
                                    if (listModel.responseData!!.amount.equals(
                                            "0.00",
                                            ignoreCase = true
                                        ) ||
                                        listModel.responseData!!.amount.equals("0", ignoreCase = true) ||
                                        listModel.responseData!!.amount.equals("", ignoreCase = true)
                                    ) {
                                        binding.tvText.visibility = View.GONE
                                        binding.views.visibility = View.GONE
                                        binding.tvPaymentDetails.visibility = View.GONE
                                    } else {
                                        binding.tvPaymentDetails.visibility = View.VISIBLE
                                        binding.tvText.visibility = View.VISIBLE
                                        binding.views.visibility = View.VISIBLE
                                        binding.tvPaymentDetails.text =
                                            """${listModel.responseData!!.cardBrand} ending **** ${listModel.responseData!!.cardDigit}
${listModel.responseData!!.email}"""
                                    }
                                } else if (flag.equals("2", ignoreCase = true)) {
                                    binding.tvSession.visibility = View.VISIBLE
                                    binding.tvText.visibility = View.GONE
                                    binding.views.visibility = View.GONE
                                    binding.tvPaymentDetails.visibility = View.GONE
                                }
                                binding.tvOrderId.text = listModel.responseData!!.invoiceNumber
                                binding.tvDate.text = listModel.responseData!!.invoiceDate
                                binding.tvTotal.text = "$" + listModel.responseData!!.totalAmount
                                binding.tvOrderTotal.text = "$" + listModel.responseData!!.amount
                                binding.tvTitle.text = listModel.responseData!!.name
                                binding.tvQty.text = "Qty: " + listModel.responseData!!.qty
                                binding.tvSession.text = "Session: " + listModel.responseData!!.session
                                binding.tvItems.text = "$" + listModel.responseData!!.amount
                                binding.tvFromAddress.text = listModel.responseData!!.invoiceFrom
                                if (listModel.responseData!!.invoiceTo.equals("", ignoreCase = true)) {
                                    binding.llBilledTo.visibility = View.GONE
                                } else {
                                    binding.llBilledTo.visibility = View.VISIBLE
                                    binding.tvBilledToTitle.text = "Billed to"
                                    binding.tvBilledTo.text = listModel.responseData!!.invoiceTo
                                }
                                binding.tvGst.text = "$" + listModel.responseData!!.gstAmount
                                if (listModel.responseData!!.totalAmount.equals(
                                        "0.00",
                                        ignoreCase = true
                                    )
                                ) {
                                    binding.views.visibility = View.GONE
                                    binding.tvOrderTotalAmount.text =
                                        "$" + listModel.responseData!!.totalAmount
                                } else {
                                    binding.views.visibility = View.VISIBLE
                                    binding.tvOrderTotalAmount.text =
                                        "$" + listModel.responseData!!.totalAmount
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<InvoiceDetailModel>, t: Throwable) {
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