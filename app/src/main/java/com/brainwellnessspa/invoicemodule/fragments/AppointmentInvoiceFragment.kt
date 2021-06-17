package com.brainwellnessspa.invoicemodule.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.FragmentInvoiceBinding
import com.brainwellnessspa.databinding.InvoiceListLayoutBinding
import com.brainwellnessspa.invoicemodule.models.InvoiceListModel.Appointment
import java.util.*

class AppointmentInvoiceFragment : Fragment() {
    lateinit var binding: FragmentInvoiceBinding
    var appointmentList: ArrayList<Appointment>? = null
    var userId: String? = ""
    var dialog: Dialog? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false)
        val view = binding.getRoot()
        val shared1 =
            requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        if (arguments != null) {
            appointmentList = requireArguments().getParcelableArrayList("appointmentInvoiceFragment")
        }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        binding.rvAIList.layoutManager = mLayoutManager
        binding.rvAIList.itemAnimator = DefaultItemAnimator()
        binding.llError.visibility = View.GONE
        binding.tvFound.text = "Your appointment invoices will appear here"
        return view
    }

    override fun onResume() {
        if (appointmentList!!.size != 0) {
            getDataList(appointmentList)
            binding.llError.visibility = View.GONE
            binding.rvAIList.visibility = View.VISIBLE
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.rvAIList.visibility = View.GONE
        }
        super.onResume()
    }

    private fun getDataList(historyList: ArrayList<Appointment>?) {
        if (historyList!!.size == 0) {
            binding.llError.visibility = View.VISIBLE
            binding.rvAIList.visibility = View.GONE
        } else {
            binding.llError.visibility = View.GONE
            binding.rvAIList.visibility = View.VISIBLE
            val adapter = AppointmentInvoiceAdapter(historyList)
            binding.rvAIList.adapter = adapter
        }
    }

    inner class AppointmentInvoiceAdapter(private val listModelList: List<Appointment>?) :
        RecyclerView.Adapter<AppointmentInvoiceAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: InvoiceListLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.invoice_list_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvInvoiceID.text = "Invoice #" + listModelList!![position].invoiceNumber
            holder.binding.tvTitle.text = listModelList[position].name
            holder.binding.tvDate.text = listModelList[position].date
            holder.binding.tvDoller.text = "$" + listModelList[position].netAmount
            holder.binding.llViewReceipt.setOnClickListener { view: View? ->
                /* dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.invoice_receipt);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.receipt_dialog_background_inset);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
                final TextView tvFromTitle = dialog.findViewById(R.id.tvFromTitle);
                final TextView tvDateTitle = dialog.findViewById(R.id.tvDateTitle);
                final TextView tvOrderIdTitle = dialog.findViewById(R.id.tvOrderIdTitle);
                final TextView tvTotalTitle = dialog.findViewById(R.id.tvTotalTitle);
                final TextView tvItemsTitle = dialog.findViewById(R.id.tvItemsTitle);
                final TextView tvGstTitle = dialog.findViewById(R.id.tvGstTitle);
                final TextView tvOrderTotalAmountTitle = dialog.findViewById(R.id.tvOrderTotalAmountTitle);
                final TextView tvSession = dialog.findViewById(R.id.tvSession);
                final TextView tvText = dialog.findViewById(R.id.tvText);
                final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                final TextView tvOrderTotalAmount = dialog.findViewById(R.id.tvOrderTotalAmount);
                final TextView tvGst = dialog.findViewById(R.id.tvGst);
                final TextView tvBilledTo = dialog.findViewById(R.id.tvBilledTo);
                final TextView tvPaymentDetails = dialog.findViewById(R.id.tvPaymentDetails);
                final TextView tvOrderId = dialog.findViewById(R.id.tvOrderId);
                final TextView tvFromAddress = dialog.findViewById(R.id.tvFromAddress);
                final TextView tvDate = dialog.findViewById(R.id.tvDate);
                final TextView tvBilledToTitle = dialog.findViewById(R.id.tvBilledToTitle);
                final TextView tvTotal = dialog.findViewById(R.id.tvTotal);
                final TextView tvOrderTotal = dialog.findViewById(R.id.tvOrderTotal);
                final TextView tvItems = dialog.findViewById(R.id.tvItems);
                final TextView tvQty = dialog.findViewById(R.id.tvQty);
                final View views = dialog.findViewById(R.id.views);
                final LinearLayout llBilledTo = dialog.findViewById(R.id.llBilledTo);

                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                if (BWSApplication.isNetworkConnected(getActivity())) {
                    BWSApplication.showProgressBar(progressBar, progressBarHolder, getActivity());
                    Call<InvoiceDetailModel> listCall = APIClient.getClient().getInvoiceDetailPlaylist(userId, listModelList.get(position).getInvoiceId(), "1"); */
                /*Flag = 0 Stagging Flag = 1 Live*/ /*
                    listCall.enqueue(new Callback<InvoiceDetailModel>() {
                        @Override
                        public void onResponse(Call<InvoiceDetailModel> call, Response<InvoiceDetailModel> response) {
                            try {
                                InvoiceDetailModel listModel = response.body();
                                if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                                    tvFromTitle.setText("From");
                                    tvDateTitle.setText("Order Date:");
                                    tvOrderIdTitle.setText("Order #:");
                                    tvTotalTitle.setText("Order Total:");
                                    tvItemsTitle.setText("Items:");
                                    tvGstTitle.setText("GST:");
                                    tvOrderTotalAmountTitle.setText("Order Total:");
                                    try {
                                        Properties p = new Properties();
                                        p.putValue("userId", userId);
                                        p.putValue("invoiceId", listModelList.get(position).getInvoiceId());
                                        p.putValue("invoiceType", "Appointment");
                                        p.putValue("invoiceAmount", listModel.getResponseData().getAmount());
                                        p.putValue("invoiceDate", listModel.getResponseData().getInvoiceDate());
                                        p.putValue("invoiceCurrency", "");
                                        p.putValue("plan", "");
                                        p.putValue("planStartDt", "");
                                        p.putValue("planExpiryDt", "");
                                        BWSApplication.addToSegment("Invoice Clicked", p, CONSTANTS.track);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    tvSession.setVisibility(View.VISIBLE);
                                    tvText.setVisibility(View.GONE);
                                    views.setVisibility(View.GONE);
                                    tvPaymentDetails.setVisibility(View.GONE);

                                    tvOrderId.setText(listModel.getResponseData().getInvoiceNumber());
                                    tvDate.setText(listModel.getResponseData().getInvoiceDate());
                                    tvTotal.setText("$" + listModel.getResponseData().getTotalAmount());
                                    tvOrderTotal.setText("$" + listModel.getResponseData().getAmount());

                                    tvTitle.setText(listModel.getResponseData().getName());
                                    tvQty.setText("Qty: " + listModel.getResponseData().getQty());
                                    tvSession.setText("Session: " + listModel.getResponseData().getSession());
                                    tvItems.setText("$" + listModel.getResponseData().getAmount());
                                    tvFromAddress.setText(listModel.getResponseData().getInvoiceFrom());
                                    if (listModel.getResponseData().getInvoiceTo().equalsIgnoreCase("")) {
                                        llBilledTo.setVisibility(View.GONE);
                                    } else {
                                        llBilledTo.setVisibility(View.VISIBLE);
                                        tvBilledToTitle.setText("Billed to");
                                        tvBilledTo.setText(listModel.getResponseData().getInvoiceTo());
                                    }

                                    tvGst.setText("$" + listModel.getResponseData().getGstAmount());
                                    if (listModel.getResponseData().getTotalAmount().equalsIgnoreCase("0.00")) {
                                        views.setVisibility(View.GONE);
                                        tvOrderTotalAmount.setText("$" + listModel.getResponseData().getTotalAmount());
                                    } else {
                                        views.setVisibility(View.VISIBLE);
                                        tvOrderTotalAmount.setText("$" + listModel.getResponseData().getTotalAmount());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<InvoiceDetailModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(progressBar, progressBarHolder, getActivity());
                        }
                    });
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                }
                dialog.show();
                dialog.setCancelable(false);*/
                val fragmentManager = activity!!.supportFragmentManager
                val receiptFragment = InvoiceReceiptFragment()
                receiptFragment.isCancelable = true
                receiptFragment.setValues(listModelList[position].invoiceNumber, "2")
                receiptFragment.show(fragmentManager, "receipt")
            }
            holder.binding.llDownloads.visibility = View.GONE
            holder.binding.llDownloads.setOnClickListener { v: View? -> }
        }

        override fun getItemCount(): Int {
            return listModelList!!.size
        }

        inner class MyViewHolder(var binding: InvoiceListLayoutBinding) : RecyclerView.ViewHolder(
            binding.root
        )
    }

    companion object {
        private const val TAG = "Download Task"
    }
}