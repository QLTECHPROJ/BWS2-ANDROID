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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.InvoiceModule.Activities.InvoiceActivity;
import com.qltech.bws.InvoiceModule.Models.InvoiceDetailModel;
import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentInvoiceReceiptBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceReceiptFragment extends DialogFragment {
    FragmentInvoiceReceiptBinding binding;
    String UserID,InvoiceID;

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

    public void setValues(String InvoiceId) {
        InvoiceID = InvoiceId;
    }

    private void prepareData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<InvoiceDetailModel> listCall = APIClient.getClient().getInvoiceDetailPlaylist(UserID,InvoiceID);
            listCall.enqueue(new Callback<InvoiceDetailModel>() {
                @Override
                public void onResponse(Call<InvoiceDetailModel> call, Response<InvoiceDetailModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        InvoiceDetailModel listModel = response.body();
                        binding.tvOrderId.setText(listModel.getResponseData().getInvoiceNumber());
                    }
                }

                @Override
                public void onFailure(Call<InvoiceDetailModel> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideProgressBar() {
        binding.progressBarHolder.setVisibility(View.GONE);
        binding.ImgV.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showProgressBar() {
        binding.progressBarHolder.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.ImgV.setVisibility(View.VISIBLE);
        binding.ImgV.invalidate();
    }
}