package com.qltech.bws.BillingOrderModule.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.BillingOrderModule.Models.AllCardModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.CardsListLayoutBinding;

import java.util.List;

public class AllCardAdapter extends RecyclerView.Adapter<AllCardAdapter.MyViewHolder>  {
    private List<AllCardModel> listModelList;

    public AllCardAdapter(List<AllCardModel> listModelList) {
        this.listModelList = listModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardsListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.cards_list_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AllCardModel listModel = listModelList.get(position);
        holder.binding.ivCheck.setImageResource(listModel.getIcon());
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardsListLayoutBinding binding;

        public MyViewHolder(CardsListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
