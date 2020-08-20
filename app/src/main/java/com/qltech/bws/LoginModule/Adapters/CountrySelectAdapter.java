package com.qltech.bws.LoginModule.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.LoginModule.Activities.LoginActivity;
import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.MembershipModule.Activities.CheckoutGetCodeActivity;
import com.qltech.bws.R;
import com.qltech.bws.databinding.CountryLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class CountrySelectAdapter extends RecyclerView.Adapter<CountrySelectAdapter.MyViewHolder> implements Filterable {
    private List<CountryListModel.ResponseData> modelList;
    private List<CountryListModel.ResponseData> listFilterData;
    Context ctx;
    String Check;
    RecyclerView rvCountryList;
    TextView tvFound;

    public CountrySelectAdapter(List<CountryListModel.ResponseData> modelList, Context ctx, RecyclerView rvCountryList, TextView tvFound, String Check) {
        this.modelList = modelList;
        this.listFilterData = modelList;
        this.ctx = ctx;
        this.rvCountryList = rvCountryList;
        this.tvFound = tvFound;
        this.Check = Check;
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
        final CountryListModel.ResponseData mData = listFilterData.get(position);
        holder.binding.tvCountryName.setText(mData.getName());
        holder.binding.tvCountryCode.setText(mData.getCode());
        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Check.equalsIgnoreCase("0")){
                    Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
                    i.putExtra("Name",modelList.get(position).getName());
                    i.putExtra("Code",modelList.get(position).getCode());
                    ctx.startActivity(i);
                }else if (Check.equalsIgnoreCase("1")){
                    Intent i = new Intent(ctx, LoginActivity.class);
                    i.putExtra("Name",modelList.get(position).getName());
                    i.putExtra("Code",modelList.get(position).getCode());
                    ctx.startActivity(i);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return listFilterData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults filterResults = new FilterResults();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFilterData = modelList;
                } else {
                    List<CountryListModel.ResponseData> filteredList = new ArrayList<>();
                    for (CountryListModel.ResponseData row : modelList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    listFilterData = filteredList;
                }
                filterResults.values = listFilterData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (listFilterData.size() == 0) {
                    tvFound.setVisibility(View.VISIBLE);
                    rvCountryList.setVisibility(View.GONE);
                } else {
                    tvFound.setVisibility(View.GONE);
                    rvCountryList.setVisibility(View.VISIBLE);
                    listFilterData = (List<CountryListModel.ResponseData>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CountryLayoutBinding binding;

        public MyViewHolder(CountryLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
