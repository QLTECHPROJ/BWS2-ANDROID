package com.brainwellnessspa.MembershipModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.room.Room;

import com.brainwellnessspa.AddPayment.AddPaymentActivity;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.MembershipModule.Models.RegisterModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityCheckoutPaymentBinding;
import com.brainwellnessspa.databinding.YeardialogBinding;
import com.google.firebase.installations.FirebaseInstallations;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.MembershipModule.Adapters.MembershipPlanAdapter.planFlag;
import static com.brainwellnessspa.MembershipModule.Adapters.MembershipPlanAdapter.planId;
import static com.brainwellnessspa.MembershipModule.Adapters.MembershipPlanAdapter.price;
import static com.brainwellnessspa.SplashModule.SplashScreenActivity.analytics;

public class CheckoutPaymentActivity extends AppCompatActivity {
    ActivityCheckoutPaymentBinding binding;
    String MobileNo = "", Code = "", Name = "", Promocode = "";
    Context context;
    Activity activity;
    Dialog d;
    int a = 0;
    String TrialPeriod;
    int position;
    int year, month;
    YeardialogBinding binding1;
    String strToken;
    AudioDatabase DB;
    private ArrayList<MembershipPlanListModel.Plan> listModelList;
    private long mLastClickTime = 0;
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
                binding.btnPayment.setEnabled(true);
                binding.btnPayment.setTextColor(getResources().getColor(R.color.white));
                binding.btnPayment.setBackgroundResource(R.drawable.extra_round_cornor);
            } else {
                binding.btnPayment.setEnabled(false);
                binding.btnPayment.setTextColor(getResources().getColor(R.color.light_gray));
                binding.btnPayment.setBackgroundResource(R.drawable.gray_round_cornor);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout_payment);
        context = CheckoutPaymentActivity.this;
        activity = CheckoutPaymentActivity.this;
        if (getIntent().getExtras() != null) {
            MobileNo = getIntent().getStringExtra("MobileNo");
            Code = getIntent().getStringExtra("Code");
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            Promocode = getIntent().getStringExtra(CONSTANTS.Promocode);
            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
            listModelList = getIntent().getParcelableArrayListExtra("PlanData");
            position = getIntent().getIntExtra("position", 0);
        }

        DB = Room.databaseBuilder(CheckoutPaymentActivity.this,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();

        binding.llBack.setOnClickListener(view -> {
            Intent i = new Intent(context, OrderSummaryActivity.class);
            i.putParcelableArrayListExtra("PlanData", listModelList);
            i.putExtra("TrialPeriod", TrialPeriod);
            i.putExtra("Name", Name);
            i.putExtra("position", position);
            i.putExtra("Promocode", Promocode);
            startActivity(i);
            finish();
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

//        DecimalFormat precision = new DecimalFormat("#.##");
        binding.tvDoller.setText("$" + price);
//        binding.tvDoller.setText("$" + precision.format(price));
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
        MeasureRatio measureRatio = BWSApplication.measureRatio(CheckoutPaymentActivity.this, 0,
                5, 3, 1f, 0);
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
        binding.etName.addTextChangedListener(addCardTextWatcher);
        binding.etNumber.addTextChangedListener(addCardTextWatcher);
        binding.etCvv.addTextChangedListener(addCardTextWatcher);
        binding.textMonth.addTextChangedListener(addCardTextWatcher);
        binding.btnPayment.setOnClickListener(view -> {
            if (binding.etNumber.getText().toString().equalsIgnoreCase("")) {
                binding.tlNumber.setError("Card number is required.");
                binding.txtError.setText("");
                binding.tlName.setError("");
            } else if (binding.etNumber.getText().toString().length() <= 15 || binding.etNumber.getText().toString().length() > 16) {
                binding.tlName.setError("");
                binding.tlNumber.setError("Please enter a valid card number");
                binding.txtError.setText("");
            } else if (binding.etName.getText().toString().equalsIgnoreCase("")) {
                binding.tlName.setError("Card holder name is required");
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
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                binding.tlName.setError("");
                binding.tlNumber.setError("");
                binding.txtError.setText("");
                final String strCardNo = binding.etNumber.getText().toString().trim().replaceAll("\\s+", "");
                int months = binding1.MonthPicker.getValue();
                int Years = binding1.YearPicker.getValue();
                Card card = new Card(strCardNo, months, Years, binding.etCvv.getText().toString());

                new Stripe().createToken(card, getString(R.string.stripe_test_key), new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Log.e("error.........", "" + error.toString());
                        BWSApplication.showToast("Please enter valid card details", getApplicationContext());
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    }

                    @Override
                    public void onSuccess(Token token) {
                        strToken = token.getId();
                        Log.e("strToken.............", "" + strToken);
                        SharedPreferences sharedPreferences2 = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
                        String fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "");
                        if (TextUtils.isEmpty(fcm_id)) {
                            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(activity, task -> {
                                String newToken = task.getResult().getToken();
                                Log.e("newToken", newToken);
                                SharedPreferences.Editor editor = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit();
                                editor.putString(CONSTANTS.Token, newToken); //Friend
                                editor.apply();
                                editor.commit();
                            });

                            /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, instanceIdResult -> {
                                String newToken = instanceIdResult.getToken();
                                Log.e("newToken", newToken);
                                SharedPreferences.Editor editor = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit();
                                editor.putString(CONSTANTS.Token, newToken); //Friend
                                editor.apply();
                                editor.commit();
                            });*/
                            SharedPreferences sharedPreferences3 = getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE);
                            fcm_id = sharedPreferences3.getString(CONSTANTS.Token, "");
                        }
                        if (!strToken.equalsIgnoreCase("")) {
                            if (BWSApplication.isNetworkConnected(context)) {
                                String deviceid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                String countryCode = Code.replace("+", "");
                                Call<RegisterModel> listCall = APIClient.getClient().getMembershipPayment(planId, planFlag, strToken, MobileNo,
                                        countryCode, fcm_id, CONSTANTS.FLAG_ONE, deviceid, Promocode);
                                listCall.enqueue(new Callback<RegisterModel>() {
                                    @Override
                                    public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        try {
                                            RegisterModel cardModel = response.body();
                                            if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                                Properties p = new Properties();
                                                p.putValue("userId", cardModel.getResponseData().getUserID());
                                                BWSApplication.addToSegment("Payment Card Add Clicked", p, CONSTANTS.track);
                                                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                                String Uname = "";
                                                if (cardModel.getResponseData().getName().equalsIgnoreCase("")) {
                                                    Uname = "Guest";
                                                } else {
                                                    Uname = cardModel.getResponseData().getName();
                                                }
                                                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = shared.edit();
                                                try {
                                                    analytics.identify(new Traits()
                                                            .putEmail(cardModel.getResponseData().getEmail())
                                                            .putName(Uname)
                                                            .putPhone(cardModel.getResponseData().getPhoneNumber())
                                                            .putValue("userId", cardModel.getResponseData().getUserID())
                                                            .putValue("id", cardModel.getResponseData().getUserID())
                                                            .putValue("deviceId", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
                                                            .putValue("deviceType", "Android")
                                                            .putValue("countryCode", Code)
                                                            .putValue("countryName", Name)
                                                            .putValue("name", Uname)
                                                            .putValue("phone", cardModel.getResponseData().getPhoneNumber())
                                                            .putValue("email", cardModel.getResponseData().getEmail())
                                                            .putValue("plan", cardModel.getResponseData().getPlan())
                                                            .putValue("planStatus", cardModel.getResponseData().getPlanStatus())
                                                            .putValue("planStartDt", cardModel.getResponseData().getPlanStartDt())
                                                            .putValue("planExpiryDt", cardModel.getResponseData().getPlanExpiryDate())
                                                            .putValue("clinikoId", cardModel.getResponseData().getClinikoId()));
                                                    editor.putBoolean(CONSTANTS.PREF_KEY_Identify, true);
                                                    editor.putBoolean(CONSTANTS.PREF_KEY_IdentifyAgain, true);
                                                } catch (Exception e) {
                                                    editor.putBoolean(CONSTANTS.PREF_KEY_Identify, false);
                                                    editor.putBoolean(CONSTANTS.PREF_KEY_IdentifyAgain, false);
                                                    e.printStackTrace();
                                                }
                                                editor.putString(CONSTANTS.PREF_KEY_UserID, cardModel.getResponseData().getUserID());
                                                editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                                                editor.putString(CONSTANTS.PREF_KEY_MobileNo, MobileNo);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayerFirstLogin, "1");
                                                editor.putString(CONSTANTS.PREF_KEY_AudioFirstLogin, "1");
                                                editor.putString(CONSTANTS.PREF_KEY_PlaylistFirstLogin, "1");
                                                editor.putString(CONSTANTS.PREF_KEY_AccountFirstLogin, "1");
                                                editor.putString(CONSTANTS.PREF_KEY_ReminderFirstLogin, "1");
                                                editor.putString(CONSTANTS.PREF_KEY_SearchFirstLogin, "1");
                                                editor.commit();
                                                GetAllMedia();
                                                Intent i = new Intent(CheckoutPaymentActivity.this, ThankYouMpActivity.class);
                                                startActivity(i);
                                                finish();
                                            } else if (cardModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                                BWSApplication.showToast(cardModel.getResponseMessage(), context);
                                            } else {
                                                BWSApplication.showToast(cardModel.getResponseMessage(), context);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<RegisterModel> call, Throwable t) {
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

    public void GetAllMedia() {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllData12().observe(this, audioList -> {
            if (audioList.size() != 0) {
                for (int i = 0; i < audioList.size(); i++) {
                    FileUtils.deleteDownloadedFile(getApplicationContext(), audioList.get(i).getName());
                }
            }
            SharedPreferences preferences11 = getSharedPreferences(CONSTANTS.PREF_KEY_Logout_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit1 = preferences11.edit();
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadName);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadUrl);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadPlaylistId);
            edit1.clear();
            edit1.commit();
            DeletallLocalCart();

        });
    }

    private void DeletallLocalCart() {
        AudioDatabase.databaseWriteExecutor.execute(() -> {
            DB.taskDao().deleteAll();
        });
        AudioDatabase.databaseWriteExecutor.execute(() -> {
            DB.taskDao().deleteAllPlalist();
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(context, OrderSummaryActivity.class);
        i.putParcelableArrayListExtra("PlanData", listModelList);
        i.putExtra("TrialPeriod", TrialPeriod);
        i.putExtra("position", position);
        i.putExtra("Promocode", Promocode);
        startActivity(i);
        finish();
    }

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
                binding.txtError.setText("Please enter a valid expiry date");
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