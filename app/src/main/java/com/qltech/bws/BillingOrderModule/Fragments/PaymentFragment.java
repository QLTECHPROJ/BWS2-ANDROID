package com.qltech.bws.BillingOrderModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qltech.bws.AddPayment.AddPaymentActivity;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Adapters.AllCardAdapter;
import com.qltech.bws.BillingOrderModule.Models.CardListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentPaymentBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PaymentFragment extends Fragment {
    FragmentPaymentBinding binding;
    AllCardAdapter adapter;
    Context context;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        View view = binding.getRoot();

        context = getActivity();
        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        userId = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvCardList.setLayoutManager(mLayoutManager);
        binding.rvCardList.setItemAnimator(new DefaultItemAnimator());

        binding.llAddNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddPaymentActivity.class);
                startActivity(i);
            }
        });
        prepareCardList();
        return view;
    }

    private void prepareCardList() {

        if (BWSApplication.isNetworkConnected(context)) {
            showProgressBar();
            Call<CardListModel> listCall = APIClient.getClient().getCardLists(userId);
            listCall.enqueue(new Callback<CardListModel>() {
                @Override
                public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        CardListModel cardListModel = response.body();
                        if (cardListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {

                            if (cardListModel.getResponseData().size() == 0) {
                                binding.rvCardList.setAdapter(null);
                                binding.rvCardList.setVisibility(View.GONE);
                            } else {
                                binding.rvCardList.setVisibility(View.VISIBLE);
                                adapter = new AllCardAdapter(cardListModel.getResponseData(), getActivity(), userId, binding.ImgV, binding.progressBarHolder,binding.rvCardList);
                                binding.rvCardList.setAdapter(adapter);
                            }


                        } else {
                            Toast.makeText(context, cardListModel.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CardListModel> call, Throwable t) {

                }

            });
        } else {
            Toast.makeText(context, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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