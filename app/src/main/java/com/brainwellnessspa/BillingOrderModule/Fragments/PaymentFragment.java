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
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentPaymentBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {
    static Context context;
    FragmentPaymentBinding binding;
    AllCardAdapter adapter;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        View view = binding.getRoot();
        context = getActivity();
        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        userId = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvCardList.setLayoutManager(mLayoutManager);
        binding.rvCardList.setItemAnimator(new DefaultItemAnimator());

        binding.llAddNewCard.setOnClickListener(view1 -> {
            if (BWSApplication.isNetworkConnected(context)) {
                Intent i = new Intent(getActivity(), AddPaymentActivity.class);
                i.putExtra("ComePayment", "1");
                startActivity(i);
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
            }
        });

//        prepareCardList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareCardList();
    }

    private void prepareCardList() {
        try {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<CardListModel> listCall = APIClient.getClient().getCardLists(userId);
            listCall.enqueue(new Callback<CardListModel>() {
                @Override
                public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        try {
                            CardListModel cardListModel = response.body();
                            if (cardListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {

                                if (cardListModel.getResponseData().size() == 0) {
                                    binding.rvCardList.setAdapter(null);
                                    binding.rvCardList.setVisibility(View.GONE);
                                } else {
                                    binding.rvCardList.setVisibility(View.VISIBLE);
                                    adapter = new AllCardAdapter(cardListModel.getResponseData(), getActivity(), userId, binding.progressBar,
                                            binding.progressBarHolder, binding.rvCardList);
                                    binding.rvCardList.setAdapter(adapter);
                                }
                            } else {
                                BWSApplication.showToast(cardListModel.getResponseMessage(), context);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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