package com.qltech.bws.BillingOrderModule.Adapters;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CardListModel;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.BillingOrderModule.Models.PayNowDetailsModel;
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
    Button btnCheckout;

    public AllCardAdapter(List<CardListModel.ResponseData> listModelList, FragmentActivity activity, String userId, ImageView ImgV,
                          FrameLayout progressBarHolder, RecyclerView rvCardList, Button btnCheckout) {
        this.listModelList = listModelList;
        this.activity = activity;
        this.userId = userId;
        this.ImgV = ImgV;
        this.progressBarHolder = progressBarHolder;
        this.rvCardList = rvCardList;
        this.btnCheckout = btnCheckout;
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
        Glide.with(activity).load(listModel.getImage())
                .thumbnail(0.05f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.ivCardimg);
        if (listModel.getIsDefault().equalsIgnoreCase(CONSTANTS.FLAG_ONE)) {
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
                BWSApplication.showProgressBar(ImgV, progressBarHolder, activity);
                Call<CardListModel> listCall = APIClient.getClient().getChangeCard(userId, listModel.getCustomer());
                listCall.enqueue(new Callback<CardListModel>() {
                    @Override
                    public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                        {
                            CardListModel cardListModel = response.body();
                            if (cardListModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                                if (cardListModel.getResponseData().size() == 0) {
                                    rvCardList.setAdapter(null);
                                    rvCardList.setVisibility(View.GONE);
                                } else {
                                    rvCardList.setVisibility(View.VISIBLE);
                                    adapter = new AllCardAdapter(cardListModel.getResponseData(), activity, userId, ImgV, progressBarHolder, rvCardList, btnCheckout);
                                    rvCardList.setAdapter(adapter);
                                }
                            } else {
                                BWSApplication.showToast(cardListModel.getResponseMessage(), activity);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<CardListModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                    }

                });
            } else {
                BWSApplication.showToast(activity.getString(R.string.no_server_found), activity);
                BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
            }

        });

        holder.binding.rlRemoveCard.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(activity)) {
                BWSApplication.showProgressBar(ImgV, progressBarHolder, activity);
                Call<CardModel> listCall = APIClient.getClient().getRemoveCard(userId, listModel.getCustomer());
                listCall.enqueue(new Callback<CardModel>() {
                    @Override
                    public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                        if (response.isSuccessful()) {
                            CardModel cardModel = response.body();
                            if (cardModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                                getCardList();
                            } else {
                                BWSApplication.showToast(cardModel.getResponseMessage(), activity);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CardModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(activity.getString(R.string.no_server_found), activity);
                BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
            }
        });

        btnCheckout.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(activity)) {
                BWSApplication.showProgressBar(ImgV, progressBarHolder, activity);
                Call<PayNowDetailsModel> listCall = APIClient.getClient().getPayNowDetails(userId, "", "","","","");
                listCall.enqueue(new Callback<PayNowDetailsModel>() {
                    @Override
                    public void onResponse(Call<PayNowDetailsModel> call, Response<PayNowDetailsModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(ImgV, progressBarHolder,activity);
                            PayNowDetailsModel listModel1 = response.body();
                            BWSApplication.showToast(listModel1.getResponseMessage(),activity);

                        }
                    }

                    @Override
                    public void onFailure(Call<PayNowDetailsModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(activity.getString(R.string.no_server_found), activity);
            }
        });
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
            BWSApplication.showProgressBar(ImgV, progressBarHolder, activity);
            Call<CardListModel> listCall = APIClient.getClient().getCardLists(userId);
            listCall.enqueue(new Callback<CardListModel>() {
                @Override
                public void onResponse(Call<CardListModel> call, Response<CardListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                        CardListModel cardListModel = response.body();
                        if (cardListModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                            if (cardListModel.getResponseData().size() == 0) {
                                rvCardList.setAdapter(null);
                                rvCardList.setVisibility(View.GONE);
                            } else {
                                rvCardList.setVisibility(View.VISIBLE);
                                adapter = new AllCardAdapter(cardListModel.getResponseData(), activity, userId, ImgV, progressBarHolder, rvCardList, btnCheckout);
                                rvCardList.setAdapter(adapter);
                            }

                        } else {
                        }
                    }
                }

                @Override
                public void onFailure(Call<CardListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                }

            });
        } else {
            BWSApplication.showToast(activity.getString(R.string.no_server_found), activity);
        }
    }
}

