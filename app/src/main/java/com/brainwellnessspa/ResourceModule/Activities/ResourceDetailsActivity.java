package com.brainwellnessspa.ResourceModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityResourceDetailsBinding;
import com.segment.analytics.Properties;

public class ResourceDetailsActivity extends AppCompatActivity {
    ActivityResourceDetailsBinding binding;
    String id, title, author, linkOne, linkTwo, image, description, resourceType, UserID;
    Context ctx;
    Properties p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource_details);
        ctx = ResourceDetailsActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getIntent().getExtras() != null) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra(CONSTANTS.title);
            author = getIntent().getStringExtra(CONSTANTS.author);
            linkOne = getIntent().getStringExtra(CONSTANTS.linkOne);
            linkTwo = getIntent().getStringExtra(CONSTANTS.linkTwo);
            image = getIntent().getStringExtra(CONSTANTS.image);
            description = getIntent().getStringExtra(CONSTANTS.description);

            if (getIntent().getStringExtra("audio_books") != null) {
                binding.tvScreenName.setText(R.string.Audio_Book);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                resourceType = getString(R.string.Audio_Book);
            }
            if (getIntent().getStringExtra("podcasts") != null) {
                binding.tvScreenName.setText(R.string.Podcasts);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                resourceType = getString(R.string.Podcasts);
            }
            if (getIntent().getStringExtra("apps") != null) {
                binding.tvScreenName.setText(R.string.Apps);
                binding.btnComplete.setVisibility(View.GONE);
                binding.llPlatfroms.setVisibility(View.VISIBLE);
                binding.tvCreator.setVisibility(View.GONE);
                resourceType = getString(R.string.Apps);
            }
            if (getIntent().getStringExtra("website") != null) {
                binding.tvScreenName.setText(R.string.Websites);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                binding.tvCreator.setVisibility(View.GONE);
                resourceType = getString(R.string.Websites);
            }
            if (getIntent().getStringExtra("documentaries") != null) {
                binding.tvScreenName.setText(R.string.Documentaries);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                resourceType = getString(R.string.Documentaries);
            }
           /* p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("resourceId", id);
            p.putValue("resourceName", title);
            p.putValue("resourceType", resourceType);
            BWSApplication.addToSegment("Resource Details Viewed", p, CONSTANTS.screen);*/
            binding.tvTitle.setText(title);
            binding.tvCreator.setText(author);
            binding.tvSubTitle.setText(description);

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.44f, 0);
            binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(image).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

            binding.btnComplete.setOnClickListener(view -> {
                if (linkOne.equalsIgnoreCase("")) {
                    BWSApplication.showToast("Not Available", ctx);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkOne));
                    startActivity(i);
                    /*p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("resourceId", id);
                    p.putValue("resourceName", title);
                    p.putValue("resourceType", resourceType);
                    p.putValue("resourceLink", linkOne);
                    BWSApplication.addToSegment("Resource External Link Clicked", p, CONSTANTS.track);*/
                }
            });

            binding.ivAndroid.setOnClickListener(view -> {
                if (linkOne.equalsIgnoreCase("")) {
                    BWSApplication.showToast("Not Available", ctx);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkOne));
                    startActivity(i);
                    /*p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("resourceId", id);
                    p.putValue("resourceName", title);
                    p.putValue("resourceType", resourceType);
                    p.putValue("resourceLink", linkOne);
                    BWSApplication.addToSegment("Resource External Link Clicked", p, CONSTANTS.track);*/
                }
            });

            binding.ivIos.setOnClickListener(view -> {
                if (linkTwo.equalsIgnoreCase("")) {
                    BWSApplication.showToast("Not Available", ctx);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkTwo));
                    startActivity(i);
                    /*p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("resourceId", id);
                    p.putValue("resourceName", title);
                    p.putValue("resourceType", resourceType);
                    p.putValue("resourceLink", linkTwo);
                    BWSApplication.addToSegment("Resource External Link Clicked", p, CONSTANTS.track);*/
                }
            });
        }

        binding.llBack.setOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}