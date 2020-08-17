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

import com.qltech.bws.InvoiceModule.Models.MembershipModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentInvoiceBinding;
import com.qltech.bws.databinding.InvoiceListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MembershipInvoiceFragment extends Fragment {
    FragmentInvoiceBinding binding;
    List<MembershipModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false);
        View view = binding.getRoot();

        MembershipInvoiceAdapter adapter = new MembershipInvoiceAdapter(listModelList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvAIList.setLayoutManager(mLayoutManager);
        binding.rvAIList.setItemAnimator(new DefaultItemAnimator());
        binding.rvAIList.setAdapter(adapter);

        prepareMembershipData();
        return view;
    }
    private void prepareMembershipData() {
        MembershipModel list = new MembershipModel("Monthly subscription");
        listModelList.add(list);
        list = new MembershipModel("INITIAL CONSULTATION AND TREATMENT");
        listModelList.add(list);
        list = new MembershipModel("NEW STANDARD SESSION");
        listModelList.add(list);
        list = new MembershipModel("Monthly subscription");
        listModelList.add(list);
        list = new MembershipModel("INITIAL CONSULTATION AND TREATMENT");
        listModelList.add(list);
        list = new MembershipModel("NEW STANDARD SESSION");
        listModelList.add(list);
        list = new MembershipModel("Monthly subscription");
        listModelList.add(list);
        list = new MembershipModel("INITIAL CONSULTATION AND TREATMENT");
        listModelList.add(list);
        list = new MembershipModel("NEW STANDARD SESSION");
        listModelList.add(list);
        list = new MembershipModel("Monthly subscription");
        listModelList.add(list);
        list = new MembershipModel("INITIAL CONSULTATION AND TREATMENT");
        listModelList.add(list);
        list = new MembershipModel("NEW STANDARD SESSION");
        listModelList.add(list);
        list = new MembershipModel("Monthly subscription");
        listModelList.add(list);
        list = new MembershipModel("INITIAL CONSULTATION AND TREATMENT");
        listModelList.add(list);
        list = new MembershipModel("NEW STANDARD SESSION");
        listModelList.add(list);
    }

    public class MembershipInvoiceAdapter  extends RecyclerView.Adapter<MembershipInvoiceAdapter.MyViewHolder> {
        private List<MembershipModel> listModelList;
        Context ctx;

        public MembershipInvoiceAdapter(List<MembershipModel> listModelList, Context ctx) {
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
            MembershipModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());

            holder.binding.llViewReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    DialogFragment receiptFragment = new InvoiceReceiptFragment();
                    receiptFragment.setCancelable(true);
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