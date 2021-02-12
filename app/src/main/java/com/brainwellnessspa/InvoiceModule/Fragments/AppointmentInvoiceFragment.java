package com.brainwellnessspa.InvoiceModule.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentInvoiceBinding;
import com.brainwellnessspa.databinding.InvoiceListLayoutBinding;
import java.util.ArrayList;
import java.util.List;

public class AppointmentInvoiceFragment extends Fragment {
    FragmentInvoiceBinding binding;
    ArrayList<InvoiceListModel.Appointment> appointmentList;
    private static final String TAG = "Download Task";
    String UserID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getArguments() != null) {
            appointmentList = getArguments().getParcelableArrayList("appointmentInvoiceFragment");
        }

      /*  Properties p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Appointment Invoice  Screen Viewed", p, CONSTANTS.screen);*/

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvAIList.setLayoutManager(mLayoutManager);
        binding.rvAIList.setItemAnimator(new DefaultItemAnimator());
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("Your appointment invoices will appear here");

        return view;
    }

    @Override
    public void onResume() {
        if (appointmentList.size() != 0) {
            getDataList(appointmentList);
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void getDataList(ArrayList<InvoiceListModel.Appointment> historyList) {
        if (historyList.size() == 0) {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        } else {
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
            AppointmentInvoiceAdapter adapter = new AppointmentInvoiceAdapter(historyList);
            binding.rvAIList.setAdapter(adapter);
        }
    }


    public class AppointmentInvoiceAdapter extends RecyclerView.Adapter<AppointmentInvoiceAdapter.MyViewHolder> {
        private List<InvoiceListModel.Appointment> listModelList;

        public AppointmentInvoiceAdapter(List<InvoiceListModel.Appointment> listModelList) {
            this.listModelList = listModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InvoiceListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.invoice_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvInvoiceID.setText("Invoice #" + listModelList.get(position).getInvoiceNumber());
            holder.binding.tvTitle.setText(listModelList.get(position).getName());
            holder.binding.tvDate.setText(listModelList.get(position).getDate());
            holder.binding.tvDoller.setText("$" + listModelList.get(position).getNetAmount());

            holder.binding.llViewReceipt.setOnClickListener(view -> {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                InvoiceReceiptFragment receiptFragment = new InvoiceReceiptFragment();
                receiptFragment.setCancelable(true);
                receiptFragment.setValues(listModelList.get(position).getInvoiceNumber(), "2");
                receiptFragment.show(fragmentManager, "receipt");
            });

            holder.binding.llDownloads.setVisibility(View.GONE);
            holder.binding.llDownloads.setOnClickListener(v -> {
//                downloadUrl = listModelList.get(position).getInvoicePdf();
//                Properties p = new Properties();
//                p.putValue("userId", UserID);
//                p.putValue("invoiceId", listModelList.get(position).getInvoiceId());
//                p.putValue("invoiceType", "Appointment");
//                p.putValue("invoiceAmount", listModelList.get(position).getAmount());
//                p.putValue("invoiceDate", listModelList.get(position).getDate());
//                p.putValue("invoiceCurrency", "");
//                p.putValue("plan", "");
//                p.putValue("planStartDt", "");
//                p.putValue("planExpiryDt", "");
//                BWSApplication.addToSegment("Invoice Downloaded", p, CONSTANTS.track);
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