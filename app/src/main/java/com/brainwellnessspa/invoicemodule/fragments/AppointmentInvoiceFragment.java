package com.brainwellnessspa.invoicemodule.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.invoicemodule.models.InvoiceListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentInvoiceBinding;
import com.brainwellnessspa.databinding.InvoiceListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class AppointmentInvoiceFragment extends Fragment {
    FragmentInvoiceBinding binding;
    ArrayList<InvoiceListModel.Appointment> appointmentList;
    private static final String TAG = "Download Task";
    String UserID;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getArguments() != null) {
            appointmentList = getArguments().getParcelableArrayList("appointmentInvoiceFragment");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvAIList.setLayoutManager(mLayoutManager);
        binding.rvAIList.setItemAnimator(new DefaultItemAnimator());
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("Your appointment invoices will appear here");

        return view;
    }

    @Override
    public void onResume() {
        if (appointmentList.size() != 0) {
            getDataList(appointmentList);
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void getDataList(ArrayList<InvoiceListModel.Appointment> historyList) {
        if (historyList.size() == 0) {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        } else {
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
            AppointmentInvoiceAdapter adapter = new AppointmentInvoiceAdapter(historyList);
            binding.rvAIList.setAdapter(adapter);
        }
    }


    public class AppointmentInvoiceAdapter extends RecyclerView.Adapter<AppointmentInvoiceAdapter.MyViewHolder> {
        private List<InvoiceListModel.Appointment> listModelList;

        public AppointmentInvoiceAdapter(List<InvoiceListModel.Appointment> listModelList) {
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
            holder.binding.tvInvoiceID.setText("Invoice #" + listModelList.get(position).getInvoiceNumber());
            holder.binding.tvTitle.setText(listModelList.get(position).getName());
            holder.binding.tvDate.setText(listModelList.get(position).getDate());
            holder.binding.tvDoller.setText("$" + listModelList.get(position).getNetAmount());

            holder.binding.llViewReceipt.setOnClickListener(view -> {
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
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                InvoiceReceiptFragment receiptFragment = new InvoiceReceiptFragment();
                receiptFragment.setCancelable(true);
                receiptFragment.setValues(listModelList.get(position).getInvoiceNumber(), "2");
                receiptFragment.show(fragmentManager, "receipt");
            });

            holder.binding.llDownloads.setVisibility(View.GONE);
            holder.binding.llDownloads.setOnClickListener(v -> {
//                downloadUrl = listModelList.get(position).getInvoicePdf();
//                Properties p = new Properties();
//                p.putValue("userId", UserID);
//                p.putValue("invoiceId", listModelList.get(position).getInvoiceId());
//                p.putValue("invoiceType", "Appointment");
//                p.putValue("invoiceAmount", listModelList.get(position).getAmount());
//                p.putValue("invoiceDate", listModelList.get(position).getDate());
//                p.putValue("invoiceCurrency", "");
//                p.putValue("plan", "");
//                p.putValue("planStartDt", "");
//                p.putValue("planExpiryDt", "");
//                BWSApplication.addToSegment("Invoice Downloaded", p, CONSTANTS.track);
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

}