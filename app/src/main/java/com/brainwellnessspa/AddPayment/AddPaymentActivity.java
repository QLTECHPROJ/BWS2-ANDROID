package com.brainwellnessspa.AddPayment;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.brainwellnessspa.AddPayment.Model.AddCardModel;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.BillingOrderActivity;
import com.brainwellnessspa.BillingOrderModule.Activities.PaymentActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityAddPaymentBinding;
import com.brainwellnessspa.databinding.YeardialogBinding;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPaymentActivity extends AppCompatActivity {
    ActivityAddPaymentBinding binding;
    String userId;
    Context context;
    Activity activity;
    Dialog d;
    int a = 0;
    int year, month;
    YeardialogBinding binding1;
    String strToken, ComePayment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_payment);
        context = AddPaymentActivity.this;
        activity = AddPaymentActivity.this;

        if (getIntent() != null) {
            ComePayment = getIntent().getStringExtra("ComePayment");
        }

        binding.llBack.setOnClickListener(view -> {
            if (ComePayment.equalsIgnoreCase("1")) {
                finish();
            } else if (ComePayment.equalsIgnoreCase("2")) {
                Intent i = new Intent(context, PaymentActivity.class);
                startActivity(i);
                finish();
            } else {
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
        binding.etNumber.addTextChangedListener(addCardTextWatcher);
        binding.etName.addTextChangedListener(addCardTextWatcher);
        binding.textMonth.addTextChangedListener(addCardTextWatcher);
        binding.etCvv.addTextChangedListener(addCardTextWatcher);

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

        binding.opendilog.setOnClickListener(v -> {
            a = 1;
            showYearDialog();
        });

        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        userId = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));

        CreditCardFormatTextWatcher tv = new CreditCardFormatTextWatcher(binding.etNumber);
        binding.etNumber.addTextChangedListener(tv);
        binding.etName.addTextChangedListener(addCardTextWatcher);
        binding.etNumber.addTextChangedListener(addCardTextWatcher);
        binding.etCvv.addTextChangedListener(addCardTextWatcher);
        binding.textMonth.addTextChangedListener(addCardTextWatcher);
        binding.btnSave.setOnClickListener(view -> {
            if (binding.etNumber.getText().toString().equalsIgnoreCase("")) {
                binding.tlNumber.setError("Card number is required.");
                binding.txtError.setText("");
                binding.tlName.setError("");
            } else if (binding.etNumber.getText().toString().length() <= 15 || binding.etNumber.getText().toString().length() > 16) {
                binding.tlName.setError("");
                binding.tlNumber.setError("Please enter a valid card number");
                binding.txtError.setText("");
            } else if (binding.etName.getText().toString().equalsIgnoreCase("")) {
                binding.tlName.setError("Card name is required");
                binding.tlNumber.setError("");
                binding.txtError.setText("");
            } else if (binding1.MonthPicker.getValue() < month && binding1.YearPicker.getValue() == year) {
                binding.txtError.setText("Please enter a valid expiry mm/yyyy");
                binding.tlName.setError("");
                binding.tlNumber.setError("");
            } else if (binding.textMonth.getText().toString().equalsIgnoreCase("Expiry Date") || a == 0) {
                binding.txtError.setText("Expiry month is required");
                binding.tlName.setError("");
                binding.tlNumber.setError("");
            } else if (binding.etCvv.getText().toString().matches("")) {
                binding.tlName.setError("");
                binding.tlNumber.setError("");
                binding.txtError.setText("CVV is required");
            } else if (binding.etCvv.getText().toString().length() < 3) {
                binding.tlName.setError("");
                binding.tlNumber.setError("");
                binding.txtError.setText("Please enter a valid CVV number");
            } else {
                binding.tlName.setError("");
                binding.tlNumber.setError("");
                binding.txtError.setText("");
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                final String strCardNo = binding.etNumber.getText().toString().trim().replaceAll("\\s+", "");
                int months = binding1.MonthPicker.getValue();
                int Years = binding1.YearPicker.getValue();

                Card card = new Card(strCardNo, months, Years, binding.etCvv.getText().toString());
                new Stripe().createToken(card, getString(R.string.stipe_test_key), new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Log.e("error.........", "" + error.toString());
                        BWSApplication.showToast("Please enter valid card details", context);
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }

                    @Override
                    public void onSuccess(Token token) {
                        strToken = token.getId();
                        Log.e("strToken.............", "" + strToken);
                        if (!strToken.equalsIgnoreCase("")) {
                            if (BWSApplication.isNetworkConnected(context)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                Call<AddCardModel> listCall = APIClient.getClient().getAddCard(userId, strToken);
                                listCall.enqueue(new Callback<AddCardModel>() {
                                    @Override
                                    public void onResponse(Call<AddCardModel> call, Response<AddCardModel> response) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        if (response.isSuccessful()) {
                                            AddCardModel cardModel = response.body();
                                            if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                                if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                    finish();
                                                    BWSApplication.showToast(cardModel.getResponseMessage(), context);
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
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    }

                                });
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), context);
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        }
                    }
                });
            }
        });
    }

    private TextWatcher addCardTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String CardNo = binding.etNumber.getText().toString().trim();
            String CardName = binding.etName.getText().toString().trim();
            String Month = binding.textMonth.getText().toString().trim();
            String CVV = binding.etCvv.getText().toString().trim();
            if (!CardNo.isEmpty() || !CardName.isEmpty() || !Month.isEmpty() || !CVV.isEmpty()) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.extra_round_cornor);
            } else {
                binding.btnSave.setEnabled(false);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.gray_extra_round_corners);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

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

    @Override
    public void onBackPressed() {
        if (ComePayment.equalsIgnoreCase("1")) {
            finish();
        } else if (ComePayment.equalsIgnoreCase("2")) {
            Intent i = new Intent(context, PaymentActivity.class);
            startActivity(i);
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            PaddingRightSpan[] spans = ccNumber.getSpans(0, ccNumber.length(), PaddingRightSpan.class);
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
                PaddingRightSpan marginSPan = new PaddingRightSpan(paddingPx);
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