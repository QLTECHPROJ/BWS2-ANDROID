package com.qltech.bws.DashboardModule.Playlist.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qltech.bws.DashboardModule.Adapters.DirectionAdapter;
import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MySpannable;
import com.qltech.bws.databinding.ActivityQueueBinding;

import java.util.ArrayList;
import java.util.List;

public class AddQueueActivity extends AppCompatActivity {
    ActivityQueueBinding binding;
    List<DirectionModel> listModelList = new ArrayList<>();
    String play;

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + "<font   color=\"#F89552\">" + expandText + "</font>";
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + "<font color=\"#F89552\">" + expandText + "</font>";
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + "<font color=\"#F89552\">" + expandText + "</font>";
                }
                tv.setText(Html.fromHtml(text));
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, 3, "Read More...", false);
                    } else {
                        makeTextViewResizable(tv, 3, "Read More...", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_queue);

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MeasureRatio measureRatio = BWSApplication.measureRatio(AddQueueActivity.this, 40,
                1, 1, 0.6f, 40);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

        if (getIntent().hasExtra("play")) {
            play = getIntent().getStringExtra("play");
        } else {
            play = "";
        }
        if (play.equalsIgnoreCase("play")) {
            binding.llOptions.setVisibility(View.VISIBLE);
        } else {
            binding.llOptions.setVisibility(View.GONE);
        }

        binding.llAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddQueueActivity.this, AddPlaylistActivity.class);
                startActivity(i);
            }
        });

        binding.llViewQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddQueueActivity.this, ViewQueueActivity.class);
                startActivity(i);
            }
        });


        String text = "This home program is designed to support your Emotional Empowerment Program and help you maintain the new habits of positive thinking bias, working with your emotions and ";

        binding.tvSubDec.setText(text);

        binding.tvSubDec.post(() -> {
            int lineCount = binding.tvSubDec.getLineCount();
            if (lineCount < 3 || lineCount == 3) {

            } else {
                makeTextViewResizable(binding.tvSubDec, 3, "Read More...", true);
                binding.tvSubDec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(AddQueueActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.full_desc_layout);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                        final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);

                        tvClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.setCancelable(false);
                    }
                });
            }
        });

        DirectionAdapter directionAdapter = new DirectionAdapter(listModelList, AddQueueActivity.this);
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(AddQueueActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvDirlist.setLayoutManager(recentlyPlayed);
        binding.rvDirlist.setItemAnimator(new DefaultItemAnimator());
        binding.rvDirlist.setAdapter(directionAdapter);

        prepareTagData();
        binding.llAddQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void prepareTagData() {
        DirectionModel list = new DirectionModel("Self-love");
        listModelList.add(list);
        list = new DirectionModel("Love");
        listModelList.add(list);
        list = new DirectionModel("Self-respect");
        listModelList.add(list);
    }
}