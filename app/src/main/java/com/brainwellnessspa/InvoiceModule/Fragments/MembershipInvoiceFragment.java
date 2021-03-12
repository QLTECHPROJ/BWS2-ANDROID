package com.brainwellnessspa.InvoiceModule.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceDetailModel;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentInvoiceBinding;
import com.brainwellnessspa.databinding.InvoiceListLayoutBinding;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.segment.analytics.Properties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipInvoiceFragment extends Fragment {
    FragmentInvoiceBinding binding;
    ArrayList<InvoiceListModel.MemberShip> memberShipList;
    private String downloadUrl = "", downloadFileName = "Invoice", UserID, file_name_path = "BWS", InvoiceAmount;
    private static final String TAG = "Download Task";
    private ProgressDialog progressDialog;
    int downloadIdInvoice = 0;
    String[] PERMISSIONS_ABOVE_Q = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission_group.STORAGE,
    };
    String[] PERMISSIONS_BELOW_Q = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    Dialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getArguments() != null) {
            memberShipList = getArguments().getParcelableArrayList("membershipInvoiceFragment");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvAIList.setLayoutManager(mLayoutManager);
        binding.rvAIList.setItemAnimator(new DefaultItemAnimator());
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("Your membership invoices will appear here");
        return view;
    }

    @Override
    public void onResume() {
        if (memberShipList.size() != 0) {
            getDataList(memberShipList);
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void getDataList(ArrayList<InvoiceListModel.MemberShip> historyList) {
        if (historyList.size() == 0) {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        } else {
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
            MembershipInvoiceAdapter adapter = new MembershipInvoiceAdapter(historyList);
            binding.rvAIList.setAdapter(adapter);
        }
    }

    public class MembershipInvoiceAdapter extends RecyclerView.Adapter<MembershipInvoiceAdapter.MyViewHolder> {
        private List<InvoiceListModel.MemberShip> listModelList;

        public MembershipInvoiceAdapter(List<InvoiceListModel.MemberShip> listModelList) {
            this.listModelList = listModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InvoiceListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.invoice_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            downloadUrl = listModelList.get(position).getInvoicePdf();
            holder.binding.tvStatus.setVisibility(View.VISIBLE);
            holder.binding.tvInvoiceID.setText("Invoice #" + listModelList.get(position).getInvoiceId());
            holder.binding.tvTitle.setText(listModelList.get(position).getName());
            holder.binding.tvDate.setText(listModelList.get(position).getDate());
            holder.binding.tvDoller.setText("$" + listModelList.get(position).getAmount());
            holder.binding.llViewReceipt.setOnClickListener(view -> {
                /*dialog = new Dialog(getActivity());
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
                    Call<InvoiceDetailModel> listCall = APIClient.getClient().getInvoiceDetailPlaylist(UserID, listModelList.get(position).getInvoiceId(), "1"); *//*Flag = 0 Stagging Flag = 1 Live*//*
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
                                        p.putValue("userId", UserID);
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
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                InvoiceReceiptFragment receiptFragment = new InvoiceReceiptFragment();
                receiptFragment.setCancelable(true);
                receiptFragment.setValues(listModelList.get(position).getInvoiceId(), "1");
                receiptFragment.show(fragmentManager, "receipt");
            });

            if (listModelList.get(position).getStatus().equalsIgnoreCase("paid")) {
                holder.binding.tvStatus.setText("Paid");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_background);
            } else if (listModelList.get(position).getStatus().equalsIgnoreCase("open")) {
                holder.binding.tvStatus.setText("Open");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.blue_background);
            }

            holder.binding.llDownloads.setOnClickListener(v -> {
                requestPermissionDownlaod();
                Properties p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("invoiceId", listModelList.get(position).getInvoiceId());
                p.putValue("invoiceType", "Appointment");
                p.putValue("invoiceAmount", listModelList.get(position).getAmount());
                p.putValue("invoiceDate", listModelList.get(position).getDate());
                p.putValue("invoiceCurrency", "");
                p.putValue("plan", "");
                p.putValue("planStartDt", "");
                p.putValue("planExpiryDt", "");
                BWSApplication.addToSegment("Invoice Downloaded", p, CONSTANTS.track);
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            InvoiceListLayoutBinding binding;

            public MyViewHolder(InvoiceListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public void requestPermissionDownlaod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_BELOW_Q[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_BELOW_Q, 1);
            } else {
                DownloadFile();
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_ABOVE_Q[0]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_ABOVE_Q[0]) == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder buildermain = new AlertDialog.Builder(getActivity());
                    buildermain.setMessage("To download invoice allow " + getActivity().getString(R.string.app_name) + " access to your device's files. " +
                            "\nTap Setting > permission, and turn \"Files and media\" on.");
                    buildermain.setCancelable(true);
                    buildermain.setPositiveButton(
                            getString(R.string.Settings),
                            (dialogmain, id1) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialogmain.dismiss();
                            });
                    buildermain.setNegativeButton(
                            getString(R.string.not_now),
                            (dialogmain, id1) -> {
                                dialogmain.dismiss();
                            });
                    AlertDialog alert11 = buildermain.create();
                    alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                    alert11.show();
                    alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                    alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_ABOVE_Q, 2);
                }
            } else {
                DownloadFile();
            }
        } else {
            DownloadFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadFile();
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

                } else {
                    callpermissionAlert();
                }
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadFile();
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                } else {
                    callpermissionAlert();
                }
                break;
            case 6:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    openfile();

                } else {
                }
                break;
        }
    }

    private void callpermissionAlert() {
        AlertDialog.Builder buildermain = new AlertDialog.Builder(getActivity());
        buildermain.setMessage("To download invoice allow " + getActivity().getString(R.string.app_name) + " access to your device's files. " +
                "\nTap Setting > permission, and turn \"Files and media\" on.");
        buildermain.setCancelable(true);
        buildermain.setPositiveButton(
                getString(R.string.ok),
                (dialogmain, id1) -> {
                    requestPermissionDownlaod();
                    dialogmain.dismiss();
                });
        AlertDialog alert11 = buildermain.create();
        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alert11.show();
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
    }

    private void openfile() {
        File pdfFile = new File(Environment.getExternalStorageState() + "/BWS" + downloadFileName + ".pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.setDataAndType(path, "application/pdf");
        try {
            startActivity(pdfIntent);
        } catch (Exception e) {
            BWSApplication.showToast("No Application available to viewPDF", getActivity());
        }
    }

    private void DownloadFile() {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download");
        File pdfFile = new File(docsFolder.getAbsolutePath(), file_name_path);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Downloading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        PRDownloader.initialize(getActivity());

        downloadIdInvoice = PRDownloader.download(downloadUrl, pdfFile.getAbsolutePath(), downloadFileName + System.currentTimeMillis() + ".pdf")
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressDialog.dismiss();
                        ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                        alertDialogBuilder.setTitle("Invoice Data Downloaded Successfully");
                        alertDialogBuilder.setMessage("Your invoice is in Download/BWS");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok", (dialog, id) -> dialog.dismiss());
                        AlertDialog alert11 = alertDialogBuilder.create();
                        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                        alert11.show();
                        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dark_blue_gray));
                    }

                    @Override
                    public void onError(Error error) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> progressDialog.dismiss(), 1000);
                        Log.e(TAG, "Download Failed");
                    }
                });
    }
}