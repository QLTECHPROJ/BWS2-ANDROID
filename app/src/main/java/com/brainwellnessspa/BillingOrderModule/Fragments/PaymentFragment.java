package com.brainwellnessspa.BillingOrderModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.AddPayment.AddPaymentActivity;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Adapters.AllCardAdapter;
import com.brainwellnessspa.BillingOrderModule.Models.CardListModel;
import com.brainwellnessspa.BillingOrderModule.Models.SegmentPayment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentPaymentBinding;
import com.google.gson.Gson;
import com.segment.analytics.Properties;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {
    FragmentPaymentBinding binding;
    AllCardAdapter adapter;
    String UserID;
    Properties p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        View view = binding.getRoot();
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvCardList.setLayoutManager(mLayoutManager);
        binding.rvCardList.setItemAnimator(new DefaultItemAnimator());

        binding.llAddNewCard.setOnClickListener(view1 -> {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                Intent i = new Intent(getActivity(), AddPaymentActivity.class);
                i.putExtra("ComePayment", "1");
                startActivity(i);
                /*Properties p1 = new Properties();
                p1.putValue("userId", UserID);
                BWSApplication.addToSegment("Payment Card Add Clicked", p1, CONSTANTS.track);*/
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        prepareCardList();
        super.onResume();
    }

    private void prepareCardList() {
        try {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<CardListModel> listCall = APIClient.getClient().getCardLists(UserID);
            listCall.enqueue(new Callback<CardListModel>() {
                @Override
                public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    CardListModel cardListModel = response.body();
                    if (cardListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                        p = new Properties();
                        p.putValue("UserID", UserID);

                        if (cardListModel.getResponseData().size() == 0) {
                            binding.rvCardList.setAdapter(null);
                            binding.rvCardList.setVisibility(View.GONE);
                            p.putValue("paymentCards", "");
                        } else {
                            binding.rvCardList.setVisibility(View.VISIBLE);
                            adapter = new AllCardAdapter(cardListModel.getResponseData(), getActivity(), UserID, binding.progressBar,
                                    binding.progressBarHolder, binding.rvCardList);
                            binding.rvCardList.setAdapter(adapter);
                            ArrayList<SegmentPayment> section1 = new ArrayList<>();
                            SegmentPayment e = new SegmentPayment();
                            Gson gson = new Gson();
                            for (int i = 0; i < cardListModel.getResponseData().size(); i++) {
                                e.setCardId(cardListModel.getResponseData().get(i).getCustomer());
                                e.setCardNumber(getActivity().getString(R.string.first_card_chars) + " " + cardListModel.getResponseData().get(i).getLast4());
                                e.setCardHolderName("");
                                e.setCardExpiry("Valid: " + cardListModel.getResponseData().get(i).getExpMonth() + "/" + cardListModel.getResponseData().get(i).getExpYear());
                                section1.add(e);
                            }
                            p.putValue("paymentCards", gson.toJson(section1));
                        }
                        BWSApplication.addToSegment("Payment Screen Viewed", p, CONSTANTS.screen);
                    } else {
                        BWSApplication.showToast(cardListModel.getResponseMessage(), getActivity());
                    }
                }

                @Override
                public void onFailure(Call<CardListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}