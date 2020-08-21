package com.qltech.bws.BillingOrderModule.Adapters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CardListModel;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.CardsListLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AllCardAdapter extends RecyclerView.Adapter<AllCardAdapter.MyViewHolder> {
    private List<CardListModel.ResponseData> listModelList;
    FragmentActivity activity;
    String card_id, userId;
    ImageView ImgV;
    FrameLayout progressBarHolder;
    RecyclerView rvCardList;
    AllCardAdapter adapter;
    public AllCardAdapter(List<CardListModel.ResponseData> listModelList, FragmentActivity activity, String userId, ImageView ImgV, FrameLayout progressBarHolder, RecyclerView rvCardList) {
        this.listModelList = listModelList;
        this.activity = activity;
        this.userId = userId;
        this.ImgV = ImgV;
        this.progressBarHolder = progressBarHolder;
        this.rvCardList = rvCardList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Glide.with(activity).load(R.drawable.loading).asGif().into(ImgV);

        CardsListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.cards_list_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CardListModel.ResponseData listModel = listModelList.get(position);
        holder.binding.tvCardNo.setText(activity.getString(R.string.first_card_chars) + listModel.getLast4());
        holder.binding.tvExpiryTime.setText("Valid: " + listModel.getExpMonth() + "/" +
                listModel.getExpYear());

        if (listModel.get_default().equalsIgnoreCase(CONSTANTS.FLAG_ONE)) {
            holder.binding.ivCheck.setImageResource(R.drawable.ic_checked_icon);
            card_id = listModel.getCustomer();
            SharedPreferences shared = activity.getSharedPreferences(CONSTANTS.PREF_KEY_CardID, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_CardID, card_id);
            editor.commit();
        } else {
            holder.binding.ivCheck.setImageResource(R.drawable.ic_unchecked_icon);
        }

        holder.binding.llAddNewCard.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(activity)) {
                showProgressBar();
                Call<CardModel> listCall = APIClient.getClient().getChangeCard(userId, listModel.getCustomer());
                listCall.enqueue(new Callback<CardModel>() {
                    @Override
                    public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                        hideProgressBar();
                        if (response.isSuccessful()) {
                            CardModel cardModel = response.body();
                            if (cardModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                                getCardList();
                            } else {
                                Toast.makeText(activity, cardModel.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<CardModel> call, Throwable t) {

                    }

                });
            } else {
                Toast.makeText(activity, activity.getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }

        });
        holder.binding.rlRemoveCard.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(activity)) {
                showProgressBar();
                Call<CardModel> listCall = APIClient.getClient().getRemoveCard(userId, listModel.getCustomer());
                listCall.enqueue(new Callback<CardModel>() {
                    @Override
                    public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                        hideProgressBar();
                        if (response.isSuccessful()) {
                            CardModel cardModel = response.body();
                            if (cardModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                                getCardList();
                            } else {
                                Toast.makeText(activity, cardModel.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CardModel> call, Throwable t) {

                    }

                });
            } else {
                Toast.makeText(activity, activity.getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }

        });

        Glide.with(activity).load(listModel.getImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.ivCardimg);
    }

    private void hideProgressBar() {
        progressBarHolder.setVisibility(View.GONE);
        ImgV.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showProgressBar() {
        progressBarHolder.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ImgV.setVisibility(View.VISIBLE);
        ImgV.invalidate();
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

    private void getCardList() {

        if (BWSApplication.isNetworkConnected(activity)) {
            showProgressBar();
            Call<CardListModel> listCall = APIClient.getClient().getCardLists(userId);
            listCall.enqueue(new Callback<CardListModel>() {
                @Override
                public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        CardListModel cardListModel = response.body();
                        if (cardListModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                            if (cardListModel.getResponseData().size() == 0) {
                                rvCardList.setAdapter(null);
                                rvCardList.setVisibility(View.GONE);
                            } else {
                                rvCardList.setVisibility(View.VISIBLE);
                                adapter = new AllCardAdapter(cardListModel.getResponseData(),activity, userId, ImgV, progressBarHolder,rvCardList);
                                rvCardList.setAdapter(adapter);
                            }

                        } else {
                        }
                    }
                }

                @Override
                public void onFailure(Call<CardListModel> call, Throwable t) {

                }

            });
        } else {
            Toast.makeText(activity, activity.getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }

    }
}

