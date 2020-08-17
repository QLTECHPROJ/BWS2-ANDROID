package com.qltech.bws.ResourceModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityResourceDetailsBinding;

public class ResourceDetailsActivity extends AppCompatActivity {
    ActivityResourceDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_resource_details);

        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("audio_books") != null) {
                binding.tvScreenName.setText("Audio Book");
                binding.tvTitle.setText("Happiness Is A Choice");
                binding.tvCreator.setText("Barry Neil Kaufman");
                binding.tvSubTitle.setText("Barry Kaufman demonstrates clearly and dramatically, that the potential for happiness is inside each of us. He and his wife have observed that people who are most successful in finding happiness share certain traits. And he has used these traits to create six Shortcuts to Happiness that you can begin using immediately. Kaufman demonstrates clearly and dramatically how to make the choice by making love a viable, vital, useful force in our daily lives.");
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
            }
            if (getIntent().getStringExtra("podcasts") != null) {
                binding.tvScreenName.setText("Podcasts");
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                binding.tvTitle.setText("The Tony Robbins Podcast");
                binding.tvCreator.setText("Tony Robbins");
                binding.tvSubTitle.setText("Tony Robbins' powerful words and insightful lessons have helped more than 50 million people from over 100 countries to create meaningful change in their lives. How do you want to change your life? Are you looking to take your business to the next level, develop stronger relationships, improve your finances or better your physical health? The Tony Robbins podcast gives you access to Tony s proven strategies for success so you can accomplish your goals, too. Whether you re looking for insight into how to build a bigger business or deepen your relationships, you have access to all the tactics Tony uses in his own life. With an extensive selection of episodes featuring insightful lessons from Tony, interviews with some of the most successful people in the world and never-before-released audio content from deep within the archives, you ll find you can learn a great deal while listening during your morning commute or daily workout. As Tony says, Every day, stand guard at the door of your mind, and you alone decide what thoughts and beliefs you let into your life. For they will shape whether you feel rich or poor, cursed or blessed. Get started today by choosing an episode that fuels your hunger and enables you to take your life to the next level.");
            }
            if (getIntent().getStringExtra("apps") != null) {
                binding.tvScreenName.setText("Apps");
                binding.btnComplete.setVisibility(View.GONE);
                binding.llPlatfroms.setVisibility(View.VISIBLE);
                binding.tvCreator.setVisibility(View.GONE);
                binding.tvTitle.setText("Calm");
                binding.tvSubTitle.setText("Calm is a leading app for meditation and sleep. Join the millions experiencing lower stress, less anxiety, and more restful sleep with our guided meditations, Sleep Stories, breathing programs, masterclasses, and relaxing music. Recommended by top psychologists, therapists, and mental health experts.");
            }
            if (getIntent().getStringExtra("website") != null) {
                binding.tvScreenName.setText("Website");
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                binding.tvCreator.setVisibility(View.GONE);
                binding.tvTitle.setText("Gaia");
                binding.tvSubTitle.setText("We're dedicated to finding and creating informative and enlightening films, original shows, classes, and articles that aren't available through mainstream media. Fuel your expansion into the topics you love exploring with exclusive videos you won t find anywhere else, filmed with world-renowned luminaries here to support your awakening.");
            }
            if (getIntent().getStringExtra("documentaries") != null) {
                binding.tvScreenName.setText("Documentaries");
                binding.btnComplete.setVisibility(View.VISIBLE);
                binding.llPlatfroms.setVisibility(View.GONE);
                binding.tvTitle.setText("Kumare");
                binding.tvCreator.setText("2011");
                binding.tvSubTitle.setText("A documentary about a man who impersonates a wise Indian Guru and builds a following in Arizona. At the height of his popularity, the Guru Kumare must reveal his true identity to his disciples and unveil his greatest teaching of all.");
            }
        }

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}