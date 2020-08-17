package com.qltech.bws.LoginModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.LoginModule.Adapters.CountrySelectAdapter;
import com.qltech.bws.LoginModule.Models.CountryListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.AppUtils;
import com.qltech.bws.databinding.ActivityCountrySelectBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountrySelectActivity extends AppCompatActivity {

    ActivityCountrySelectBinding binding;
    CountrySelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_country_select);


        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//        Test
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvCountryList.setLayoutManager(mLayoutManager);
        binding.rvCountryList.setItemAnimator(new DefaultItemAnimator());

        if (BWSApplication.isNetworkConnected(this)) {
            Call<CountryListModel> listCall = APIClient.getClient().getCountryLists();
            listCall.enqueue(new Callback<CountryListModel>() {
                @Override
                public void onResponse(Call<CountryListModel> call, Response<CountryListModel> response) {
                    if (response.isSuccessful()) {
                        CountryListModel listModel = response.body();
                        adapter = new CountrySelectAdapter(listModel.getResponseData());
                        binding.rvCountryList.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<CountryListModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }
}