package com.qltech.bws.BillingOrderModule.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qltech.bws.AddPaymentModule.Activities.AddPaymentActivity;
import com.qltech.bws.BillingOrderModule.Adapters.AllCardAdapter;
import com.qltech.bws.BillingOrderModule.Models.AllCardModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentPaymentBinding;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends Fragment {
    FragmentPaymentBinding binding;
    List<AllCardModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        View view = binding.getRoot();

        AllCardAdapter adapter = new AllCardAdapter(listModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvCardList.setLayoutManager(mLayoutManager);
        binding.rvCardList.setItemAnimator(new DefaultItemAnimator());
        binding.rvCardList.setAdapter(adapter);

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
        AllCardModel list = new AllCardModel(R.drawable.ic_checked_icon);
        listModelList.add(list);
        list = new AllCardModel(R.drawable.ic_unchecked_icon);
        listModelList.add(list);
        list = new AllCardModel(R.drawable.ic_checked_icon);
        listModelList.add(list);
        list = new AllCardModel(R.drawable.ic_unchecked_icon);
        listModelList.add(list);
        list = new AllCardModel(R.drawable.ic_checked_icon);
        listModelList.add(list);
        list = new AllCardModel(R.drawable.ic_unchecked_icon);
        listModelList.add(list);
    }
}