package com.brainwellnessspa.DashboardTwoModule.home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.ManageModule.ManageActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReminderModule.Activities.ReminderActivity;
import com.brainwellnessspa.ReminderModule.Activities.ReminderDetailsActivity;
import com.brainwellnessspa.ReminderModule.Models.RemiderDetailsModel;
import com.brainwellnessspa.UserModuleTwo.Models.UserListModel;
import com.brainwellnessspa.databinding.FragmentHomeBinding;
import com.brainwellnessspa.databinding.MultipleProfileChangeLayoutBinding;
import com.brainwellnessspa.databinding.RemiderDetailsLayoutBinding;
import com.brainwellnessspa.databinding.UserListCustomLayoutBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    UserListAdapter adapter;
    ArrayList<UserListModel> userList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
//                textView.setText(s);
        });

        binding.llBottomView.setOnClickListener(v -> {
            UserListCustomLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity())
                    , R.layout.user_list_custom_layout, null, false);
            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BaseBottomSheetDialog);
            mBottomSheetDialog.setContentView(layoutBinding.getRoot());
            BottomSheetBehavior mBottomSheetBehavior = new BottomSheetBehavior();
            mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mBottomSheetDialog.show();
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            layoutBinding.rvUserList.setLayoutManager(mLayoutManager);
            layoutBinding.rvUserList.setItemAnimator(new DefaultItemAnimator());
            adapter = new UserListAdapter(userList);
            layoutBinding.rvUserList.setAdapter(adapter);
            prepareUserData();
        });


        binding.llClick.setOnClickListener(v -> {
//            TODO Mansi Dialog Hint This code is Audio Detail
            Intent i = new Intent(getActivity(), ManageActivity.class);
            startActivity(i);
           /* final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.open_playlist_detail_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialog.setOnKeyListener((v1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            dialog.show();
            dialog.setCancelable(false);*/
//            TODO Mansi Dialog Hint This code is Audio Detail
           /* final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.open_detail_page_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvReadMore = dialog.findViewById(R.id.tvReadMore);

            tvReadMore.setOnClickListener(v12 -> {
                final Dialog dialog1 = new Dialog(getActivity());
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setContentView(R.layout.full_desc_layout);
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final TextView tvDesc = dialog1.findViewById(R.id.tvDesc);
                final RelativeLayout tvClose = dialog1.findViewById(R.id.tvClose);
//                    tvDesc.setText(directionModel.getResponseData().get(0).getAudioDescription());

                dialog1.setOnKeyListener((v3, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog1.dismiss();
                        return true;
                    }
                    return false;
                });

                tvClose.setOnClickListener(v14 -> dialog1.dismiss());
                dialog1.show();
                dialog1.setCancelable(false);
            });
            dialog.setOnKeyListener((v1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            dialog.show();
            dialog.setCancelable(false);*/
        });
        return view;
    }

    public void prepareUserData() {
        UserListModel userAdd = new UserListModel("Jhon Smith");
        userList.add(userAdd);
        userAdd = new UserListModel("Jhon Smith");
        userList.add(userAdd);

//        adapter.notifyDataSetChanged();
    }

    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
        private List<UserListModel> model;

        public UserListAdapter(List<UserListModel> model) {
            this.model = model;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MultipleProfileChangeLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.multiple_profile_change_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bind.tvName.setText(model.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MultipleProfileChangeLayoutBinding bind;

            public MyViewHolder(MultipleProfileChangeLayoutBinding bind) {
                super(bind.getRoot());
                this.bind = bind;
            }
        }
    }
}