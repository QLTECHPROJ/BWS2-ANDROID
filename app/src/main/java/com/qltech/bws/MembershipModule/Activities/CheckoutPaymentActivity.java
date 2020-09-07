package com.qltech.bws.MembershipModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qltech.bws.AddPayment.AddPaymentActivity;
import com.qltech.bws.AddPayment.Model.AddCardModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityCheckoutPaymentBinding;
import com.qltech.bws.databinding.YeardialogBinding;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter.planFlag;
import static com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter.planId;

public class CheckoutPaymentActivity extends AppCompatActivity {
    ActivityCheckoutPaymentBinding binding;
    String MobileNo;
    Context context;
    Activity activity;
    Dialog d;
    int a = 0;
    int year, month;
    YeardialogBinding binding1;
    String strToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout_payment);
        context = CheckoutPaymentActivity.this;
        activity = CheckoutPaymentActivity.this;
        if (getIntent() != null) {
            MobileNo = getIntent().getStringExtra("MobileNo");
        }
        Glide.with(activity).load(R.drawable.loading).asGif().into(binding.ImgV);

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        month = month + 1;
        d = new Dialog(context);
        d.setTitle("Year Picker");
        binding1 = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.yeardialog, null, false);
        d.setContentView(binding1.getRoot());
       /* binding.etNumber.addTextChangedListener(addCardTextWatcher);
        binding.etName.addTextChangedListener(addCardTextWatcher);
        binding.textMonth.addTextChangedListener(addCardTextWatcher);
        binding.etCvv.addTextChangedListener(addCardTextWatcher);*/

        binding.etNumber.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.etNumber.getText().toString().length() == 16) {
                    binding.etName.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        MeasureRatio measureRatio = BWSApplication.measureRatio(CheckoutPaymentActivity.this, 18,
                5, 3, 1f, 18);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.ic_checkout_card_logo);
        binding.opendilog.setOnClickListener(v -> {
            a = 1;
            showYearDialog();
        });

        AddPaymentActivity.CreditCardFormatTextWatcher tv = new AddPaymentActivity.CreditCardFormatTextWatcher(binding.etNumber);
        binding.etNumber.addTextChangedListener(tv);

        binding.btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (binding.textMonth.getText().toString().equalsIgnoreCase("Expiry Date")
                            && a == 0 && binding.etNumber.getText().toString().equalsIgnoreCase("")
                            && binding.etCvv.getText().toString().equalsIgnoreCase("")
                            && binding.etName.getText().toString().equalsIgnoreCase("")) {
                        binding.txtError.setText(getString(R.string.blank_all_fields));
                    } else if (binding.etNumber.getText().toString().equalsIgnoreCase("")) {
                        binding.tlNumber.setError(getString(R.string.blank_card_no_error));
                        binding.txtError.setText("");
                        binding.tlName.setError("");
                    } else if (binding.etNumber.getText().toString().length() < 16) {
                        binding.tlName.setError("");
                        binding.tlNumber.setError(getString(R.string.valid_card_no_error));
                        binding.txtError.setText("");
                    } else if (binding.etName.getText().toString().equalsIgnoreCase("")) {
                        binding.tlName.setError(getString(R.string.blank_card_holder_name_error));
                        binding.tlNumber.setError("");
                        binding.txtError.setText("");
                    } else if (binding1.MonthPicker.getValue() < month && binding1.YearPicker.getValue() == year) {
                        binding.txtError.setText(getString(R.string.invalid_expiry_date));
                        binding.tlName.setError("");
                        binding.tlNumber.setError("");
                    } else if (binding.textMonth.getText().toString().equalsIgnoreCase("Expiry Date") || a == 0) {
                        binding.txtError.setText("Hey! I'll need you to add expiry details");
                        binding.tlName.setError("");
                        binding.tlNumber.setError("");
                    } else if (binding.etCvv.getText().toString().matches("")) {
                        binding.tlName.setError("");
                        binding.tlNumber.setError("");
                        binding.txtError.setText(getString(R.string.valid_cvv_error));
                    } else if (binding.etCvv.getText().toString().length() < 3) {
                        binding.tlName.setError("");
                        binding.tlNumber.setError("");
                        binding.txtError.setText(getString(R.string.invalid_cvv_error));
                    } else {
                        binding.tlName.setError("");
                        binding.tlNumber.setError("");
                        binding.txtError.setText("");
                        showProgressBar();
                        final String strCardNo = binding.etNumber.getText().toString().trim().replaceAll("\\s+", "");
                        int months = binding1.MonthPicker.getValue();
                        int Years = binding1.YearPicker.getValue();

                        Card card = new Card(strCardNo, months, Years, binding.etCvv.getText().toString());

                        new Stripe().createToken(card, getString(R.string.stipe_test_key), new TokenCallback() {
                            @Override
                            public void onError(Exception error) {
                                Log.e("error.........", "" + error.toString());
                                BWSApplication.showToast("Invalid Card Details", getApplicationContext());
                                hideProgressBar();
                            }

                            @Override
                            public void onSuccess(Token token) {
                                strToken = token.getId();
                                Log.e("strToken.............", "" + strToken);
                                if (!strToken.equalsIgnoreCase("")) {
                                    if (BWSApplication.isNetworkConnected(context)) {
                                        showProgressBar();
                                        Call<AddCardModel> listCall = APIClient.getClient().getMembershipPayment(planId, planFlag, strToken, MobileNo);
                                        listCall.enqueue(new Callback<AddCardModel>() {
                                            @Override
                                            public void onResponse(Call<AddCardModel> call, Response<AddCardModel> response) {
                                                hideProgressBar();
                                                if (response.isSuccessful()) {
                                                    AddCardModel cardModel = response.body();
                                                    if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                                        if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = shared.edit();
                                                            editor.putString(CONSTANTS.PREF_KEY_UserID, cardModel.getResponseData().getUserId());
                                                            editor.putString(CONSTANTS.PREF_KEY_MobileNo, MobileNo);
                                                            editor.commit();
                                                            Intent i = new Intent(CheckoutPaymentActivity.this, ThankYouMpActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        } else if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                                            BWSApplication.showToast(cardModel.getResponseMessage(), context);
                                                        } else {
                                                            BWSApplication.showToast(cardModel.getResponseMessage(), context);
                                                        }
                                                    } else {
                                                        BWSApplication.showToast(cardModel.getResponseMessage(), context);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<AddCardModel> call, Throwable t) {

                                            }

                                        });
                                    } else {
                                        BWSApplication.showToast(getString(R.string.no_server_found), context);
                                        hideProgressBar();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /*  private TextWatcher addCardTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String CardNo = binding.etNumber.getText().toString().trim();
            String CardName = binding.etName.getText().toString().trim();
            String Month = binding.textMonth.getText().toString().trim();
            String CVV = binding.etCvv.getText().toString().trim();
            if (!CardNo.isEmpty() && !CardName.isEmpty() && !Month.isEmpty() && !CVV.isEmpty()) {
                binding.btnPayment.setEnabled(true);
                binding.btnPayment.setTextColor(getResources().getColor(R.color.white));
                binding.btnPayment.setBackgroundResource(R.drawable.extra_round_cornor);
            } else {
                binding.btnPayment.setEnabled(false);
                binding.btnPayment.setTextColor(getResources().getColor(R.color.light_gray));
                binding.btnPayment.setBackgroundResource(R.drawable.gray_extra_round_corners);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };*/

    public void showYearDialog() {
        binding1.MonthPicker.setMaxValue(12);
        binding1.MonthPicker.setMinValue(1);
        binding1.MonthPicker.setWrapSelectorWheel(false);
        binding1.MonthPicker.setValue(month);
        binding1.MonthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding1.YearPicker.setMaxValue(year + 80);
        binding1.YearPicker.setMinValue(year);
        binding1.YearPicker.setWrapSelectorWheel(false);
        binding1.YearPicker.setValue(year);
        binding1.YearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        binding1.set.setOnClickListener(v -> {
            if (binding1.MonthPicker.getValue() < month && binding1.YearPicker.getValue() == year) {
                binding.txtError.setText("Please Select Valid Month And Year");
                d.dismiss();
            } else {
                binding.textMonth.setText(" " + binding1.MonthPicker.getValue() + " / " + binding1.YearPicker.getValue());
                binding.txtError.setText("");
                d.dismiss();
            }
        });
        binding1.cancle.setOnClickListener(v -> {
            d.dismiss();
        });
        d.show();
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CreditCardFormatTextWatcher implements TextWatcher {
        public static final int NO_MAX_LENGTH = -1;
        private int maxLength = NO_MAX_LENGTH;
        private int paddingPx;
        private boolean internalStopFormatFlag;

        public CreditCardFormatTextWatcher(@NonNull TextView textView) {
            setPaddingEm(textView, 1f);
        }

        public static void formatCardNumber(@NonNull Editable ccNumber, int paddingPx, int maxLength) {
            int textLength = ccNumber.length();
            // first remove any previous span
            AddPaymentActivity.CreditCardFormatTextWatcher.PaddingRightSpan[] spans = ccNumber.getSpans(0, ccNumber.length(), AddPaymentActivity.CreditCardFormatTextWatcher.PaddingRightSpan.class);
            for (int i = 0; i < spans.length; i++) {
                ccNumber.removeSpan(spans[i]);
            }
            // then truncate to max length
            if (maxLength > 0 && textLength > maxLength - 1) {
                ccNumber.replace(maxLength, textLength, "");
            }
            // finally add margin spans
            for (int i = 1; i <= ((textLength - 1) / 4); i++) {
                int end = i * 4;
                int start = end - 1;
                AddPaymentActivity.CreditCardFormatTextWatcher.PaddingRightSpan marginSPan = new AddPaymentActivity.CreditCardFormatTextWatcher.PaddingRightSpan(paddingPx);
                ccNumber.setSpan(marginSPan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }


        public void setPaddingPx(int paddingPx) {
            this.paddingPx = paddingPx;
        }


        public void setPaddingEm(@NonNull TextView textView, float em) {
            float emSize = textView.getPaint().measureText("x");
            setPaddingPx((int) (em * emSize));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (internalStopFormatFlag) {
                return;
            }
            internalStopFormatFlag = true;
            formatCardNumber(s, paddingPx, maxLength);
            internalStopFormatFlag = false;
        }

        public static class PaddingRightSpan extends ReplacementSpan {

            private int mPadding;

            public PaddingRightSpan(int padding) {
                mPadding = padding;
            }

            @Override
            public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
                float[] widths = new float[end - start];
                paint.getTextWidths(text, start, end, widths);
                int sum = mPadding;
                for (int i = 0; i < widths.length; i++) {
                    sum += widths[i];
                }
                return sum;
            }

            @Override
            public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
                canvas.drawText(text, start, end, x, y, paint);
            }
        }
    }
}