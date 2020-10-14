package com.brainwellnessspa.InvoiceModule.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceDetailModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentInvoiceReceiptBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceReceiptFragment extends DialogFragment {
    FragmentInvoiceReceiptBinding binding;
    String UserID, InvoiceID, Flag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice_receipt, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.receipt_dialog_background_inset);
        }

        prepareData();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                return true;
            }
            return false;
        });
        return view;
    }

    public void setValues(String InvoiceId, String flag) {
        InvoiceID = InvoiceId;
        Flag = flag;
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<InvoiceDetailModel> listCall = APIClient.getClient().getInvoiceDetailPlaylist(UserID, InvoiceID, "1"); /*Flag = 0 Stagging Flag = 1 Live*/
            listCall.enqueue(new Callback<InvoiceDetailModel>() {
                @Override
                public void onResponse(Call<InvoiceDetailModel> call, Response<InvoiceDetailModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        InvoiceDetailModel listModel = response.body();
                        binding.tvFromTitle.setText("From");
                        binding.tvDateTitle.setText("Order Date:");
                        binding.tvOrderIdTitle.setText("Order #:");
                        binding.tvTotalTitle.setText("Order Total:");
                        binding.tvItemsTitle.setText("Items:");
                        binding.tvGstTitle.setText("GST:");
                        binding.tvOrderTotalAmountTitle.setText("Order Total:");
                        if (Flag.equalsIgnoreCase("1")) {
                            binding.tvSession.setVisibility(View.GONE);
                            binding.tvPaymentDetails.setText(listModel.getResponseData().getCardBrand() + " ending **** " +
                                    listModel.getResponseData().getCardDigit() + "\n" + listModel.getResponseData().getEmail());
                        } else if (Flag.equalsIgnoreCase("2")) {
                            binding.tvSession.setVisibility(View.VISIBLE);
                            binding.tvText.setVisibility(View.GONE);
                            binding.views.setVisibility(View.GONE);
                            binding.tvPaymentDetails.setVisibility(View.GONE);
                            binding.tvPaymentDetails.setText(listModel.getResponseData().getEmail());
                        }

                        binding.tvOrderId.setText(listModel.getResponseData().getInvoiceNumber());
                        binding.tvDate.setText(listModel.getResponseData().getInvoiceDate());
                        binding.tvTotal.setText("$" + listModel.getResponseData().getTotalAmount());
                        binding.tvOrderTotal.setText("$" + listModel.getResponseData().getAmount());

                        binding.tvTitle.setText(listModel.getResponseData().getName());
                        binding.tvQty.setText("Qty: " + listModel.getResponseData().getQty());
                        binding.tvSession.setText("Session: " + listModel.getResponseData().getSession());
                        binding.tvItems.setText("$" + listModel.getResponseData().getAmount());
                        binding.tvFromAddress.setText(listModel.getResponseData().getInvoiceFrom());
                        if (listModel.getResponseData().getInvoiceTo().equalsIgnoreCase("")) {
                            binding.llBilledTo.setVisibility(View.GONE);
                        } else {
                            binding.llBilledTo.setVisibility(View.VISIBLE);
                            binding.tvBilledToTitle.setText("Billed to");
                            binding.tvBilledTo.setText(listModel.getResponseData().getInvoiceTo());
                        }

                        binding.tvGst.setText("$" + listModel.getResponseData().getGstAmount());
                        if (listModel.getResponseData().getTotalAmount().equalsIgnoreCase("0.00")){
                            binding.tvText.setVisibility(View.GONE);
                            binding.views.setVisibility(View.GONE);
                            binding.tvPaymentDetails.setVisibility(View.GONE);
                            binding.tvOrderTotalAmount.setText("$" + listModel.getResponseData().getTotalAmount());
                        }else {
                            binding.tvText.setVisibility(View.VISIBLE);
                            binding.views.setVisibility(View.VISIBLE);
                            binding.tvPaymentDetails.setVisibility(View.VISIBLE);
                            binding.tvOrderTotalAmount.setText("$" + listModel.getResponseData().getTotalAmount());
                        }
                    }
                }

                @Override
                public void onFailure(Call<InvoiceDetailModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }
}