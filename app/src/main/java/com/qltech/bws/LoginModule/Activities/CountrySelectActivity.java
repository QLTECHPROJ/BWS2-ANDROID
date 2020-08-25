package com.qltech.bws.LoginModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityCountrySelectBinding;
import com.qltech.bws.databinding.CountryLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountrySelectActivity extends AppCompatActivity {
    String Check;
    ActivityCountrySelectBinding binding;
    CountrySelectAdapter adapter;
    String MobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_country_select);

        if (getIntent().getExtras() != null) {
            Check = getIntent().getStringExtra(CONSTANTS.Check);
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
        }
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CountrySelectActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        Glide.with(getApplicationContext()).load(R.drawable.loading).asGif().into(binding.ImgV);
        binding.searchView.onActionViewExpanded();
        EditText searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();

        closeButton.setOnClickListener(view -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });
        searchEditText.setHint("Search for country");

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                try {
                    adapter.getFilter().filter(search);
                    Log.e("searchsearch", "" + search);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvCountryList.setLayoutManager(mLayoutManager);
        binding.rvCountryList.setItemAnimator(new DefaultItemAnimator());
        showProgressBar();
        if (BWSApplication.isNetworkConnected(this)) {
            Call<CountryListModel> listCall = APIClient.getClient().getCountryLists();
            listCall.enqueue(new Callback<CountryListModel>() {
                @Override
                public void onResponse(Call<CountryListModel> call, Response<CountryListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        CountryListModel listModel = response.body();
                        if (listModel != null) {
                            adapter = new CountrySelectAdapter(listModel.getResponseData());
                        }
                        binding.rvCountryList.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<CountryListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(CountrySelectActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public class CountrySelectAdapter extends RecyclerView.Adapter<CountrySelectAdapter.MyViewHolder> implements Filterable {
        private List<CountryListModel.ResponseData> modelList;
        private List<CountryListModel.ResponseData> listFilterData;

        public CountrySelectAdapter(List<CountryListModel.ResponseData> modelList) {
            this.modelList = modelList;
            this.listFilterData = modelList;
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
            holder.binding.tvCountryCode.setText("+" + mData.getCode());
            holder.binding.llMainLayout.setOnClickListener(view -> {
                String conutry = "+" + mData.getCode();
                Intent i = new Intent(CountrySelectActivity.this, LoginActivity.class);
                i.putExtra("Name", mData.getName());
                i.putExtra("Code", conutry);
                i.putExtra("MobileNo", MobileNo);
                startActivity(i);
                finish();
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
                        binding.tvFound.setVisibility(View.VISIBLE);
                        binding.rvCountryList.setVisibility(View.GONE);
                    } else {
                        binding.tvFound.setVisibility(View.GONE);
                        binding.rvCountryList.setVisibility(View.VISIBLE);
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

    private void hideProgressBar() {
        binding.progressBarHolder.setVisibility(View.GONE);
        binding.ImgV.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showProgressBar() {
        binding.progressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.ImgV.setVisibility(View.VISIBLE);
        binding.ImgV.invalidate();
    }
}