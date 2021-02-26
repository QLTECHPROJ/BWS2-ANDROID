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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityResourceDetailsBinding;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.segment.analytics.Properties;

public class ResourceDetailsActivity extends AppCompatActivity {
    ActivityResourceDetailsBinding binding;
    String id, title, author, linkOne, linkTwo, image, description, resourceType, UserID, mastercat, subcat;
    Context ctx;
    Properties p, p1;

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
            mastercat = getIntent().getStringExtra(CONSTANTS.mastercat);
            subcat = getIntent().getStringExtra(CONSTANTS.subcat);

            p = new Properties();
            p1 = new Properties();
            p.putValue("userId", UserID);
            p.putValue("resourceId", id);
            p.putValue("resourceName", title);
            if (getIntent().getStringExtra("audio_books") != null) {
                binding.tvScreenName.setText(R.string.Audio_Book);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                resourceType = getString(R.string.Audio_Book);
                p.putValue("author", author);
                p1.putValue("author", author);
            }
            if (getIntent().getStringExtra("podcasts") != null) {
                binding.tvScreenName.setText(R.string.Podcasts);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                resourceType = getString(R.string.Podcasts);
                p.putValue("author", author);
                p1.putValue("author", author);
            }
            if (getIntent().getStringExtra("apps") != null) {
                binding.tvScreenName.setText(R.string.Apps);
                binding.btnComplete.setVisibility(View.GONE);
                binding.llPlatfroms.setVisibility(View.VISIBLE);
                binding.tvCreator.setVisibility(View.GONE);
                resourceType = getString(R.string.Apps);
                p.putValue("author", "");
                p1.putValue("author", "");
            }
            if (getIntent().getStringExtra("website") != null) {
                binding.tvScreenName.setText(R.string.Websites);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                binding.tvCreator.setVisibility(View.GONE);
                resourceType = getString(R.string.Websites);
                p.putValue("author", "");
                p1.putValue("author", "");
            }
            if (getIntent().getStringExtra("documentaries") != null) {
                binding.tvScreenName.setText(R.string.Documentaries);
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                resourceType = getString(R.string.Documentaries);
                p.putValue("author", author);
                p1.putValue("author", author);
            }

            try {
                p.putValue("resourceType", resourceType);
                p.putValue("resourceDesc", description);
                p.putValue("masterCategory", mastercat);
                p.putValue("subCategory", subcat);
                if (linkOne.equalsIgnoreCase("")) {
                    p.putValue("resourceLink", linkTwo);
                } else if (linkTwo.equalsIgnoreCase("")) {
                    p.putValue("resourceLink", linkOne);
                } else {

                }
                BWSApplication.addToSegment("Resource Details Viewed", p, CONSTANTS.screen);
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.tvTitle.setText(title);
            binding.tvCreator.setText(author);
            binding.tvSubTitle.setText(description);

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.44f, 0);
            binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(image).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(40))).priority(Priority.HIGH)
                    .skipMemoryCache(false).into(binding.ivRestaurantImage);

            binding.btnComplete.setOnClickListener(view -> {
                if (linkOne.equalsIgnoreCase("")) {
                    BWSApplication.showToast("Not Available", ctx);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkOne));
                    startActivity(i);
                    p1.putValue("userId", UserID);
                    p1.putValue("resourceId", id);
                    p1.putValue("resourceName", title);
                    p1.putValue("resourceType", resourceType);
                    p1.putValue("resourceDesc", description);
                    p1.putValue("resourceLink", linkOne);
                    p1.putValue("masterCategory", mastercat);
                    p1.putValue("subCategory", subcat);
                    BWSApplication.addToSegment("Resource External Link Clicked", p1, CONSTANTS.track);
                }
            });

            binding.ivAndroid.setOnClickListener(view -> {
                if (linkOne.equalsIgnoreCase("")) {
                    BWSApplication.showToast("Not Available", ctx);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkOne));
                    startActivity(i);
                    p1.putValue("userId", UserID);
                    p1.putValue("resourceId", id);
                    p1.putValue("resourceName", title);
                    p1.putValue("resourceType", resourceType);
                    p1.putValue("resourceDesc", description);
                    p1.putValue("resourceLink", linkOne);
                    p1.putValue("masterCategory", mastercat);
                    p1.putValue("subCategory", subcat);
                    BWSApplication.addToSegment("Resource External Link Clicked", p1, CONSTANTS.track);
                }
            });

            binding.ivIos.setOnClickListener(view -> {
                if (linkTwo.equalsIgnoreCase("")) {
                    BWSApplication.showToast("Not Available", ctx);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(linkTwo));
                    startActivity(i);
                    p1.putValue("userId", UserID);
                    p1.putValue("resourceId", id);
                    p1.putValue("resourceName", title);
                    p1.putValue("resourceType", resourceType);
                    p1.putValue("resourceDesc", description);
                    p1.putValue("resourceLink", linkTwo);
                    p1.putValue("masterCategory", mastercat);
                    p1.putValue("subCategory", subcat);
                    BWSApplication.addToSegment("Resource External Link Clicked", p1, CONSTANTS.track);
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