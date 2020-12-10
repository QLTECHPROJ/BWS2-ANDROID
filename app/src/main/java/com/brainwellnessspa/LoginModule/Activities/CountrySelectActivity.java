package com.brainwellnessspa.LoginModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.LoginModule.Models.CountryListModel;
import com.brainwellnessspa.MembershipModule.Activities.CheckoutGetCodeActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityCountrySelectBinding;
import com.brainwellnessspa.databinding.CountryLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountrySelectActivity extends AppCompatActivity {
    ActivityCountrySelectBinding binding;
    CountrySelectAdapter adapter;
    //    String TrialPeriod;
//    private ArrayList<MembershipPlanListModel.Plan> listModelList;
//    int position;
    Context ctx;
    Activity activity;
    String Name, Code, MobileNo, Check, searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_country_select);
        ctx = CountrySelectActivity.this;
        activity = CountrySelectActivity.this;

        if (getIntent().getExtras() != null) {
            MobileNo = getIntent().getStringExtra(CONSTANTS.MobileNo);
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Code = getIntent().getStringExtra(CONSTANTS.Code);
            Check = getIntent().getStringExtra(CONSTANTS.Check);
//            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
//            listModelList = getIntent().getParcelableArrayListExtra("PlanData");
//            position = getIntent().getIntExtra("position", 0);
        }
        binding.llBack.setOnClickListener(view -> {
            if (Check.equalsIgnoreCase("0")) {
                Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
                i.putExtra("Name", Name);
                i.putExtra("Code", Code);
                i.putExtra("MobileNo", MobileNo);
                startActivity(i);
                finish();
            } else if (Check.equalsIgnoreCase("1")) {
                Intent i = new Intent(ctx, LoginActivity.class);
                i.putExtra("Name", Name);
                i.putExtra("Code", Code);
                i.putExtra("MobileNo", MobileNo);
                startActivity(i);
                finish();
            }
        });

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
                    searchFilter = search;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvCountryList.setLayoutManager(mLayoutManager);
        binding.rvCountryList.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onResume() {
        super.onResume();
        PrepareData();
    }

    public void PrepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<CountryListModel> listCall = APIClient.getClient().getCountryLists();
            listCall.enqueue(new Callback<CountryListModel>() {
                @Override
                public void onResponse(Call<CountryListModel> call, Response<CountryListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            CountryListModel listModel = response.body();
                            if (listModel != null) {
                                adapter = new CountrySelectAdapter(listModel.getResponseData());
                            }
                            binding.rvCountryList.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CountryListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }
    }

    @Override
    public void onBackPressed() {
        if (Check.equalsIgnoreCase("0")) {
            Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
            i.putExtra("Name", Name);
            i.putExtra("Code", Code);
            i.putExtra("MobileNo", MobileNo);
            startActivity(i);
            finish();
        } else if (Check.equalsIgnoreCase("1")) {
            Intent i = new Intent(ctx, LoginActivity.class);
            i.putExtra("Name", Name);
            i.putExtra("Code", Code);
            i.putExtra("MobileNo", MobileNo);
            startActivity(i);
            finish();
        }
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
                if (Check.equalsIgnoreCase("0")) {
                    Intent i = new Intent(ctx, CheckoutGetCodeActivity.class);
                    i.putExtra("Name", mData.getName());
                    i.putExtra("Code", conutry);
                    i.putExtra("MobileNo", MobileNo);
                    ctx.startActivity(i);
                    finish();
                } else if (Check.equalsIgnoreCase("1")) {
                    Intent i = new Intent(ctx, LoginActivity.class);
                    i.putExtra("Name", mData.getName());
                    i.putExtra("Code", conutry);
                    i.putExtra("MobileNo", MobileNo);
                    ctx.startActivity(i);
                    finish();
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
                        binding.tvFound.setVisibility(View.VISIBLE);
                        binding.tvFound.setText("Couldn't find " + searchFilter + ". Try searching again");
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
}