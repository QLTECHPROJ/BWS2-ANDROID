package com.qltech.bws.InvoiceModule.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentInvoiceBinding;
import com.qltech.bws.databinding.InvoiceListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MembershipInvoiceFragment extends Fragment {
    FragmentInvoiceBinding binding;
    ArrayList<InvoiceListModel.MemberShip> memberShipList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            memberShipList = getArguments().getParcelableArrayList("membershipInvoiceFragment");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvAIList.setLayoutManager(mLayoutManager);
        binding.rvAIList.setItemAnimator(new DefaultItemAnimator());

        if (memberShipList.size() != 0) {
            getDataList(memberShipList);
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);

        }
        return view;
    }

    private void getDataList(ArrayList<InvoiceListModel.MemberShip> historyList) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            MembershipInvoiceAdapter adapter = new MembershipInvoiceAdapter(historyList, getActivity());
            binding.rvAIList.setAdapter(adapter);
        }
    }

    public class MembershipInvoiceAdapter  extends RecyclerView.Adapter<MembershipInvoiceAdapter.MyViewHolder> {
        private List<InvoiceListModel.MemberShip> listModelList;
        Context ctx;

        public MembershipInvoiceAdapter(List<InvoiceListModel.MemberShip> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InvoiceListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.invoice_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvInvoiceID.setText("Invoice #"+listModelList.get(position).getInvoiceId());
            holder.binding.tvTitle.setText(listModelList.get(position).getName());
            holder.binding.tvDate.setText(listModelList.get(position).getDate());
            holder.binding.tvDoller.setText("$"+listModelList.get(position).getAmount());
            holder.binding.llViewReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    InvoiceReceiptFragment receiptFragment = new InvoiceReceiptFragment();
                    receiptFragment.setCancelable(true);
                    receiptFragment.setValues(listModelList.get(position).getInvoiceId());
                    receiptFragment.show(fragmentManager,"receipt");
                }
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