package com.brainwellnessspa.DashboardTwoModule.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.UserModuleTwo.Activities.AddProfileActivity;
import com.brainwellnessspa.UserModuleTwo.Models.AddedUserListModel;
import com.brainwellnessspa.UserModuleTwo.Models.VerifyPinModel;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentHomeBinding;
import com.brainwellnessspa.databinding.MultipleProfileChangeLayoutBinding;
import com.brainwellnessspa.databinding.UserListCustomLayoutBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    UserListAdapter adapter;
    String USERID, UserName;
    private EditText[] editTexts;
    boolean tvSendOTPbool = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        UserName = (shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, ""));
        binding.tvName.setText(UserName);
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


            prepareUserData(layoutBinding.rvUserList, layoutBinding.progressBar);

            layoutBinding.llAddNewUser.setOnClickListener(v1 -> {
                Intent i = new Intent(getActivity(), AddProfileActivity.class);
                startActivity(i);
            });
        });


        binding.llClick.setOnClickListener(v -> {
//            TODO Mansi Dialog Hint This code is Audio Detail
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

    public void prepareUserData(RecyclerView rvUserList, ProgressBar progressBar) {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.invalidate();
            Call<AddedUserListModel> listCall = APINewClient.getClient().getUserList(USERID);
            listCall.enqueue(new Callback<AddedUserListModel>() {
                @Override
                public void onResponse(Call<AddedUserListModel> call, Response<AddedUserListModel> response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        AddedUserListModel listModel = response.body();
                        if (listModel != null) {
                            adapter = new UserListAdapter(listModel.getResponseData());
                        }
                        rvUserList.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddedUserListModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
        private AddedUserListModel.ResponseData model;
        int selectedItem = -1;

        public UserListAdapter(AddedUserListModel.ResponseData model) {
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
            List<AddedUserListModel.ResponseData.CoUser> modelList = model.getCoUserList();
            holder.bind.tvName.setText(modelList.get(position).getName());
            holder.bind.ivCheck.setImageResource(R.drawable.ic_checked_icon);
            holder.bind.ivCheck.setVisibility(View.INVISIBLE);
            if (selectedItem == position) {
                holder.bind.ivCheck.setVisibility(View.VISIBLE);
            }
            holder.bind.rlCheckedUser.setOnClickListener(v -> {
                int previousItem = selectedItem;
                selectedItem = position;
                notifyItemChanged(previousItem);
                notifyItemChanged(position);
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.comfirm_pin_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                Button btnDone = dialog.findViewById(R.id.btnDone);
                TextView txtError = dialog.findViewById(R.id.txtError);
                EditText edtOTP1 = dialog.findViewById(R.id.edtOTP1);
                EditText edtOTP2 = dialog.findViewById(R.id.edtOTP2);
                EditText edtOTP3 = dialog.findViewById(R.id.edtOTP3);
                EditText edtOTP4 = dialog.findViewById(R.id.edtOTP4);
                ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

                editTexts = new EditText[]{edtOTP1, edtOTP2, edtOTP3, edtOTP4};
                edtOTP1.addTextChangedListener(new PinTextWatcher(0, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP2.addTextChangedListener(new PinTextWatcher(1, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP3.addTextChangedListener(new PinTextWatcher(2, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP4.addTextChangedListener(new PinTextWatcher(3, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone));
                edtOTP1.setOnKeyListener(new PinOnKeyListener(0));
                edtOTP2.setOnKeyListener(new PinOnKeyListener(1));
                edtOTP3.setOnKeyListener(new PinOnKeyListener(2));
                edtOTP4.setOnKeyListener(new PinOnKeyListener(3));
                dialog.setOnKeyListener((v11, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });
                btnDone.setOnClickListener(v1 -> {
                    if (edtOTP1.getText().toString().equalsIgnoreCase("")
                            && edtOTP2.getText().toString().equalsIgnoreCase("")
                            && edtOTP3.getText().toString().equalsIgnoreCase("")
                            && edtOTP4.getText().toString().equalsIgnoreCase("")) {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText("Please enter OTP");
                    } else {
                        if (BWSApplication.isNetworkConnected(getActivity())) {
                            txtError.setVisibility(View.GONE);
                            txtError.setText("");
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.invalidate();
                            Call<VerifyPinModel> listCall = APINewClient.getClient().getVerifyPin(modelList.get(position).getCoUserId(),
                                    edtOTP1.getText().toString() + "" +
                                            edtOTP2.getText().toString() + "" +
                                            edtOTP3.getText().toString() + "" +
                                            edtOTP4.getText().toString());
                            listCall.enqueue(new Callback<VerifyPinModel>() {
                                @Override
                                public void onResponse(Call<VerifyPinModel> call, Response<VerifyPinModel> response) {
                                    try {
                                        progressBar.setVisibility(View.GONE);
                                        VerifyPinModel listModel = response.body();
                                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                            BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                            dialog.dismiss();
                                        } else if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                            txtError.setVisibility(View.VISIBLE);
                                            txtError.setText(listModel.getResponseMessage());
                                        } else {
                                            txtError.setVisibility(View.VISIBLE);
                                            txtError.setText(listModel.getResponseMessage());
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<VerifyPinModel> call, Throwable t) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }

                });
                dialog.show();
                dialog.setCancelable(false);
            });
        }

        @Override
        public int getItemCount() {
            return model.getCoUser().size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MultipleProfileChangeLayoutBinding bind;

            public MyViewHolder(MultipleProfileChangeLayoutBinding bind) {
                super(bind.getRoot());
                this.bind = bind;
            }
        }
    }

    public class PinTextWatcher implements TextWatcher {
        private int currentIndex;
        EditText edtOTP1, edtOTP2, edtOTP3, edtOTP4;
        Button btnDone;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex, EditText edtOTP1, EditText edtOTP2, EditText edtOTP3, EditText edtOTP4, Button btnDone) {
            this.currentIndex = currentIndex;
            this.edtOTP1 = edtOTP1;
            this.edtOTP2 = edtOTP2;
            this.edtOTP3 = edtOTP3;
            this.edtOTP4 = edtOTP4;
            this.btnDone = btnDone;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
            String OTP1 = edtOTP1.getText().toString().trim();
            String OTP2 = edtOTP2.getText().toString().trim();
            String OTP3 = edtOTP3.getText().toString().trim();
            String OTP4 = edtOTP4.getText().toString().trim();
            if (!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty()) {
                btnDone.setEnabled(true);
                btnDone.setTextColor(getResources().getColor(R.color.white));
                btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled);
            } else {
                btnDone.setEnabled(false);
                btnDone.setTextColor(getResources().getColor(R.color.white));
                btnDone.setBackgroundResource(R.drawable.gray_round_cornor);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = newTypedString;
            Log.e("OTP VERIFICATION", "" + text);

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts
            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1) {
                moveToNext();
            } else if (text.length() == 0) {
                if (!tvSendOTPbool) {
                    editTexts[0].requestFocus();
                } else {
                    moveToPrevious();
                }
            }
        }

        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getActivity().getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public class PinOnKeyListener implements View.OnKeyListener {
        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }
    }
}