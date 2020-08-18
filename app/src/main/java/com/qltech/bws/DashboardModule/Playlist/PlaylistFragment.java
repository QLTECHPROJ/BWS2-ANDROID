package com.qltech.bws.DashboardModule.Playlist;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.qltech.bws.DashboardModule.Activities.AddAudioActivity;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentPlaylistBinding;

public class PlaylistFragment extends Fragment {
    FragmentPlaylistBinding binding;
    private PlaylistViewModel playlistViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        playlistViewModel =
                ViewModelProviders.of(this).get(PlaylistViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        View view = binding.getRoot();

        binding.tvExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddAudioActivity.class);
                startActivity(i);
            }
        });
        binding.ivStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivStatus.setImageResource(R.drawable.ic_play_icon);
            }
        });
        binding.llCreated1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.rlPlaylist, myPlaylistsFragment).
                        addToBackStack("MyPlaylistsFragment")
                        .commit();
            }
        });
        binding.llCreated2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.rlPlaylist, myPlaylistsFragment).
                        addToBackStack("MyPlaylistsFragment")
                        .commit();
            }
        });
        binding.llDownloads1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.rlPlaylist, myPlaylistsFragment).
                        addToBackStack("MyPlaylistsFragment")
                        .commit();
            }
        });
        binding.llDownloads2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.rlPlaylist, myPlaylistsFragment).
                        addToBackStack("MyPlaylistsFragment")
                        .commit();
            }
        });
        binding.rlCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.create_palylist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final RelativeLayout tvconfirm = dialog.findViewById(R.id.tvconfirm);

                tvconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .replace(R.id.rlPlaylist, myPlaylistsFragment).
                                addToBackStack("MyPlaylistsFragment")
                                .commit();
                        dialog.dismiss();
                    }
                });

                tvGoBack.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
        });
        playlistViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        return view;
    }
}