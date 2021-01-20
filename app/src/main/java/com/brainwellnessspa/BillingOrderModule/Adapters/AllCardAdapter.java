package com.brainwellnessspa.BillingOrderModule.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Models.CardListModel;
import com.brainwellnessspa.BillingOrderModule.Models.CardModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.CardsListLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AllCardAdapter extends RecyclerView.Adapter<AllCardAdapter.MyViewHolder> {
    private List<CardListModel.ResponseData> listModelList;
    FragmentActivity activity;
    String card_id, userId;
    ProgressBar ImgV;
    FrameLayout progressBarHolder;
    RecyclerView rvCardList;
    AllCardAdapter adapter;

    public AllCardAdapter(List<CardListModel.ResponseData> listModelList, FragmentActivity activity, String userId, ProgressBar ImgV,
                          FrameLayout progressBarHolder, RecyclerView rvCardList) {
        this.listModelList = listModelList;
        this.activity = activity;
        this.userId = userId;
        this.ImgV = ImgV;
        this.progressBarHolder = progressBarHolder;
        this.rvCardList = rvCardList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardsListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.cards_list_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CardListModel.ResponseData listModel = listModelList.get(position);
        holder.binding.tvCardNo.setText(activity.getString(R.string.first_card_chars) + " " + listModel.getLast4());
        holder.binding.tvExpiryTime.setText("Valid: " + listModel.getExpMonth() + "/" +
                listModel.getExpYear());
        Glide.with(activity).load(listModel.getImage()).thumbnail(0.05f)
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
                        try {
                            BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                            CardListModel cardListModel = response.body();
                            if (cardListModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                                if (cardListModel.getResponseData().size() == 0) {
                                    rvCardList.setAdapter(null);
                                    rvCardList.setVisibility(View.GONE);
                                } else {
                                    rvCardList.setVisibility(View.VISIBLE);
                                    adapter = new AllCardAdapter(cardListModel.getResponseData(), activity, userId, ImgV, progressBarHolder, rvCardList);
                                    rvCardList.setAdapter(adapter);
                                }
                                BWSApplication.showToast(cardListModel.getResponseMessage(), activity);
                            } else {
                                BWSApplication.showToast(cardListModel.getResponseMessage(), activity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.delete_payment_card);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
            final Button Btn = dialog.findViewById(R.id.Btn);

            Btn.setText("DELETE");
            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });
            Btn.setOnTouchListener((view1, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Button views = (Button) view1;
                        views.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view1.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        if (BWSApplication.isNetworkConnected(activity)) {
                            BWSApplication.showProgressBar(ImgV, progressBarHolder, activity);
                            Call<CardModel> listCall = APIClient.getClient().getRemoveCard(userId, listModel.getCustomer());
                            listCall.enqueue(new Callback<CardModel>() {
                                @Override
                                public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                                    try {
                                        BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                                        if (response.isSuccessful()) {
                                            CardModel cardModel = response.body();
                                            if (cardModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                                                getCardList();
                                                dialog.dismiss();
                                                BWSApplication.showToast(cardModel.getResponseMessage(), activity);
                                            } else {
                                                BWSApplication.showToast(cardModel.getResponseMessage(), activity);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                    case MotionEvent.ACTION_CANCEL: {
                        Button views = (Button) view1;
                        views.getBackground().clearColorFilter();
                        views.invalidate();
                        break;
                    }
                }
                return true;
            });

            tvGoBack.setOnClickListener(v -> {
                dialog.dismiss();
            });
            dialog.show();
            dialog.setCancelable(false);
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
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(ImgV, progressBarHolder, activity);
                            CardListModel cardListModel = response.body();
                            if (cardListModel.getResponseCode().equalsIgnoreCase(activity.getString(R.string.ResponseCodesuccess))) {
                                if (cardListModel.getResponseData().size() == 0) {
                                    rvCardList.setAdapter(null);
                                    rvCardList.setVisibility(View.GONE);
                                } else {
                                    rvCardList.setVisibility(View.VISIBLE);
                                    adapter = new AllCardAdapter(cardListModel.getResponseData(), activity, userId, ImgV, progressBarHolder, rvCardList);
                                    rvCardList.setAdapter(adapter);
                                }

                            } else {
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

