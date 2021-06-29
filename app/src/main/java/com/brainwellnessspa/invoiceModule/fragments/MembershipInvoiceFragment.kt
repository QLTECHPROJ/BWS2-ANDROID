package com.brainwellnessspa.invoiceModule.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.databinding.FragmentInvoiceBinding
import com.brainwellnessspa.databinding.InvoiceListLayoutBinding
import com.brainwellnessspa.invoiceModule.models.InvoiceListModel.MemberShip
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.segment.analytics.Properties
import java.io.File
import java.util.*

class MembershipInvoiceFragment : Fragment() {
    lateinit var binding: FragmentInvoiceBinding
    var memberShipList: ArrayList<MemberShip>? = null
    private var downloadUrl = ""
    private val downloadFileName = "Invoice"
    private var userId: String? = ""
    private val file_name_path = "BWS"
    private var progressDialog: ProgressDialog? = null
    var downloadIdInvoice = 0
    private var PERMISSIONS_ABOVE_Q =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE)
    private var PERMISSIONS_BELOW_Q = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE)
    var dialog: Dialog? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false)
        val view = binding.root
        val shared1 =
            requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        if (arguments != null) {
            memberShipList = requireArguments().getParcelableArrayList("membershipInvoiceFragment")
        }
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        binding.rvAIList.layoutManager = mLayoutManager
        binding.rvAIList.itemAnimator = DefaultItemAnimator()
        binding.llError.visibility = View.GONE
        binding.tvFound.text = "Your membership invoices will appear here"
        return view
    }

    override fun onResume() {
        if (memberShipList!!.size != 0) {
            getDataList(memberShipList)
            binding.llError.visibility = View.GONE
            binding.rvAIList.visibility = View.VISIBLE
        } else {
            binding.llError.visibility = View.VISIBLE
            binding.rvAIList.visibility = View.GONE
        }
        super.onResume()
    }

    private fun getDataList(historyList: ArrayList<MemberShip>?) {
        if (historyList!!.size == 0) {
            binding.llError.visibility = View.VISIBLE
            binding.rvAIList.visibility = View.GONE
        } else {
            binding.llError.visibility = View.GONE
            binding.rvAIList.visibility = View.VISIBLE
            val adapter = MembershipInvoiceAdapter(historyList)
            binding.rvAIList.adapter = adapter
        }
    }

    inner class MembershipInvoiceAdapter(private val listModelList: List<MemberShip>?) :
        RecyclerView.Adapter<MembershipInvoiceAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: InvoiceListLayoutBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.invoice_list_layout,
                    parent,
                    false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            downloadUrl = listModelList!![position].invoicePdf.toString()
            holder.binding.tvStatus.visibility = View.VISIBLE
            holder.binding.tvInvoiceID.text = "Invoice #" + listModelList[position].invoiceId
            holder.binding.tvTitle.text = listModelList[position].name
            holder.binding.tvDate.text = listModelList[position].date
            holder.binding.tvDoller.text = "$" + listModelList[position].amount
            holder.binding.llViewReceipt.setOnClickListener {/*dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.invoice_receipt);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.receipt_dialog_background_inset);

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
                    Call<InvoiceDetailModel> listCall = APIClient.getClient().getInvoiceDetailPlaylist(userId, listModelList.get(position).getInvoiceId(), "1"); *//*Flag = 0 Stagging Flag = 1 Live*/ /*
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
                                        p.putValue("invoiceType", "Memebrship");
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
                                    tvSession.setVisibility(View.GONE);
                                    if (listModel.getResponseData().getAmount().equalsIgnoreCase("0.00") ||
                                            listModel.getResponseData().getAmount().equalsIgnoreCase("0") ||
                                            listModel.getResponseData().getAmount().equalsIgnoreCase("")) {
                                        tvText.setVisibility(View.GONE);
                                        views.setVisibility(View.GONE);
                                        tvPaymentDetails.setVisibility(View.GONE);
                                    } else {
                                        tvPaymentDetails.setVisibility(View.VISIBLE);
                                        tvText.setVisibility(View.VISIBLE);
                                        views.setVisibility(View.VISIBLE);
                                        tvPaymentDetails.setText(listModel.getResponseData().getCardBrand() + " ending **** " +
                                                listModel.getResponseData().getCardDigit() + "\n" + listModel.getResponseData().getEmail());
                                    }

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
                receiptFragment.setValues(listModelList[position].invoiceId, "1")
                receiptFragment.show(fragmentManager, "receipt")
            }
            if (listModelList[position].status.equals("paid", ignoreCase = true)) {
                holder.binding.tvStatus.text = "Paid"
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_background)
            } else if (listModelList[position].status.equals("open", ignoreCase = true)) {
                holder.binding.tvStatus.text = "Open"
                holder.binding.tvStatus.setBackgroundResource(R.drawable.blue_background)
            }
            holder.binding.llDownloads.setOnClickListener {
                requestPermissionDownlaod()
                val p = Properties()
                p.putValue("userId", userId)
                p.putValue("invoiceId", listModelList[position].invoiceId)
                p.putValue("invoiceType", "Appointment")
                p.putValue("invoiceAmount", listModelList[position].amount)
                p.putValue("invoiceDate", listModelList[position].date)
                p.putValue("invoiceCurrency", "")
                p.putValue("plan", "")
                p.putValue("planStartDt", "")
                p.putValue("planExpiryDt", "")
                BWSApplication.addToSegment("Invoice Downloaded", p, CONSTANTS.track)
            }
        }

        override fun getItemCount(): Int {
            return listModelList!!.size
        }

        inner class MyViewHolder(var binding: InvoiceListLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    fun requestPermissionDownlaod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    PERMISSIONS_BELOW_Q[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS_BELOW_Q, 1)
            } else {
                DownloadFile()
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    PERMISSIONS_ABOVE_Q[0]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireActivity(),
                        PERMISSIONS_ABOVE_Q[0]) == PackageManager.PERMISSION_DENIED) {
                    val buildermain = AlertDialog.Builder(requireActivity())
                    buildermain.setMessage("""To download invoice allow ${
                        requireActivity().getString(R.string.app_name)
                    } access to your device's files. 
Tap Setting > permission, and turn "Files and media" on.""")
                    buildermain.setCancelable(true)
                    buildermain.setPositiveButton(getString(R.string.Settings)) { dialogs: DialogInterface, _: Int ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        dialogs.dismiss()
                    }
                    buildermain.setNegativeButton(getString(R.string.not_now)) { dialogs: DialogInterface, _: Int -> dialogs.dismiss() }
                    val alert11 = buildermain.create()
                    alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
                    alert11.show()
                    alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
                    alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS_ABOVE_Q, 2)
                }
            } else {
                DownloadFile()
            }
        } else {
            DownloadFile()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DownloadFile()
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            } else {
                callpermissionAlert()
            }
            2 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DownloadFile()
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            } else {
                callpermissionAlert()
            }
            6 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
                openfile()
            }
        }
    }

    private fun callpermissionAlert() {
        val buildermain = AlertDialog.Builder(requireActivity())
        buildermain.setMessage("""To download invoice allow ${requireActivity().getString(R.string.app_name)} access to your device's files. 
Tap Setting > permission, and turn "Files and media" on.""")
        buildermain.setCancelable(true)
        buildermain.setPositiveButton(getString(R.string.ok)) { dialogs: DialogInterface, _: Int ->
            requestPermissionDownlaod()
            dialogs.dismiss()
        }
        val alert11 = buildermain.create()
        alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
        alert11.show()
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
    }

    private fun openfile() {
        val pdfFile =
            File(Environment.getExternalStorageState() + "/BWS" + downloadFileName + ".pdf") // -> filename = maven.pdf
        val path = Uri.fromFile(pdfFile)
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        pdfIntent.setDataAndType(path, "application/pdf")
        try {
            startActivity(pdfIntent)
        } catch (e: Exception) {
            BWSApplication.showToast("No Application available to viewPDF", activity)
        }
    }

    private fun DownloadFile() {
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/Download")
        val pdfFile = File(docsFolder.absolutePath, file_name_path)
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Downloading...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
        PRDownloader.initialize(activity)
        downloadIdInvoice = PRDownloader.download(downloadUrl,
            pdfFile.absolutePath,
            downloadFileName + System.currentTimeMillis() + ".pdf").build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    progressDialog!!.dismiss()
                    val ctw = ContextThemeWrapper(activity, R.style.AppTheme)
                    val alertDialogBuilder = AlertDialog.Builder(ctw)
                    alertDialogBuilder.setTitle("Invoice Data Downloaded Successfully")
                    alertDialogBuilder.setMessage("Your invoice is in Download/BWS")
                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton("Ok") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                    val alert11 = alertDialogBuilder.create()
                    alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
                    alert11.show()
                    alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(requireActivity(),
                            R.color.dark_blue_gray))
                }

                override fun onError(error: Error) {
                    Handler(Looper.getMainLooper()).postDelayed({ progressDialog!!.dismiss() },
                        1000)
                    Log.e(TAG, "Download Failed")
                }
            })
    }

    companion object {
        private const val TAG = "Download Task"
    }
}