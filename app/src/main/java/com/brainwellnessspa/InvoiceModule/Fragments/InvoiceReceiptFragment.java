package com.brainwellnessspa.InvoiceModule.Fragments;

import android.content.Context;
import android.content.Intent;
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
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceDetailModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentInvoiceReceiptBinding;
import com.segment.analytics.Properties;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToRecepit;

public class InvoiceReceiptFragment extends DialogFragment {
    FragmentInvoiceReceiptBinding binding;
    String UserID, InvoiceID, Flag, InvoiceAmount;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice_receipt, container, false);
        view = binding.getRoot();
        invoiceToRecepit = 1;
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.receipt_dialog_background_inset);
        }

        return view;
    }

    @Override
    public void onResume() {
        invoiceToRecepit = 1;
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
                invoiceToRecepit = 1;
                Intent i = new Intent(getActivity(), InvoiceActivity.class);
                i.putExtra("ComeFrom", "");
                startActivity(i);
                getActivity().finish();
                return true;
            }
            return false;
        });
        prepareData();
        super.onResume();
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
                    try {
                        InvoiceDetailModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            InvoiceAmount = "$" + listModel.getResponseData().getTotalAmount();
                            binding.tvFromTitle.setText("From");
                            binding.tvDateTitle.setText("Order Date:");
                            binding.tvOrderIdTitle.setText("Order #:");
                            binding.tvTotalTitle.setText("Order Total:");
                            binding.tvItemsTitle.setText("Items:");
                            binding.tvGstTitle.setText("GST:");
                            binding.tvOrderTotalAmountTitle.setText("Order Total:");
                            try {
                                Properties p = new Properties();
                                p.putValue("userId", UserID);
                                p.putValue("invoiceId", InvoiceID);
                                if (Flag.equalsIgnoreCase("1")) {
                                    p.putValue("invoiceType", "Memebrship");
                                } else if (Flag.equalsIgnoreCase("2")) {
                                    p.putValue("invoiceType", "Appointment");
                                }

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
                            if (Flag.equalsIgnoreCase("1")) {
                                binding.tvSession.setVisibility(View.GONE);
                                if (listModel.getResponseData().getAmount().equalsIgnoreCase("0.00") ||
                                        listModel.getResponseData().getAmount().equalsIgnoreCase("0") ||
                                        listModel.getResponseData().getAmount().equalsIgnoreCase("")) {
                                    binding.tvText.setVisibility(View.GONE);
                                    binding.views.setVisibility(View.GONE);
                                    binding.tvPaymentDetails.setVisibility(View.GONE);
                                } else {
                                    binding.tvPaymentDetails.setVisibility(View.VISIBLE);
                                    binding.tvText.setVisibility(View.VISIBLE);
                                    binding.views.setVisibility(View.VISIBLE);
                                    binding.tvPaymentDetails.setText(listModel.getResponseData().getCardBrand() + " ending **** " +
                                            listModel.getResponseData().getCardDigit() + "\n" + listModel.getResponseData().getEmail());
                                }
                            } else if (Flag.equalsIgnoreCase("2")) {
                                binding.tvSession.setVisibility(View.VISIBLE);
                                binding.tvText.setVisibility(View.GONE);
                                binding.views.setVisibility(View.GONE);
                                binding.tvPaymentDetails.setVisibility(View.GONE);
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
                            if (listModel.getResponseData().getTotalAmount().equalsIgnoreCase("0.00")) {
                                binding.views.setVisibility(View.GONE);
                                binding.tvOrderTotalAmount.setText("$" + listModel.getResponseData().getTotalAmount());
                            } else {
                                binding.views.setVisibility(View.VISIBLE);
                                binding.tvOrderTotalAmount.setText("$" + listModel.getResponseData().getTotalAmount());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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