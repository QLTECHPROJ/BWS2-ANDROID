package com.qltech.bws.InvoiceModule.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.InvoiceModule.Models.InvoiceDetailModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentInvoiceReceiptBinding;

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

        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
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
            showProgressBar();
            Call<InvoiceDetailModel> listCall = APIClient.getClient().getInvoiceDetailPlaylist(UserID, InvoiceID, "1"); /*Flag = 0 Stagging Flag = 1 Live*/
            listCall.enqueue(new Callback<InvoiceDetailModel>() {
                @Override
                public void onResponse(Call<InvoiceDetailModel> call, Response<InvoiceDetailModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        InvoiceDetailModel listModel = response.body();
                        binding.tvFromTitle.setText("From");
                        binding.tvBilledToTitle.setText("Billed to");
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
                        binding.tvBilledTo.setText(listModel.getResponseData().getInvoiceTo());
                        binding.tvGst.setText("$" + listModel.getResponseData().getGstAmount());
                        binding.tvOrderTotalAmount.setText("$" + listModel.getResponseData().getTotalAmount());
                    }
                }

                @Override
                public void onFailure(Call<InvoiceDetailModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}