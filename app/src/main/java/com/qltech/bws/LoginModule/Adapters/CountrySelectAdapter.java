package com.qltech.bws.LoginModule.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.CountryLayoutBinding;

import java.util.List;

public class CountrySelectAdapter extends RecyclerView.Adapter<CountrySelectAdapter.MyViewHolder> {
    private List<CountryListModel.ResponseData> listModelList;

    public CountrySelectAdapter(List<CountryListModel.ResponseData> listModelList) {
        this.listModelList = listModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CountryLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.country_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvCountryName.setText(listModelList.get(position).getName());
        holder.binding.tvCountryCode.setText(listModelList.get(position).getCode());
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CountryLayoutBinding binding;

        public MyViewHolder(CountryLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
