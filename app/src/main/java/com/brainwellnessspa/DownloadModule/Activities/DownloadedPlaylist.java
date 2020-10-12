package com.brainwellnessspa.DownloadModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment;
import com.brainwellnessspa.R;

import static com.brainwellnessspa.DashboardModule.Search.SearchFragment.comefrom_search;

public class DownloadedPlaylist extends AppCompatActivity {
    String PlaylistId, PlaylistName, PlaylistImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_playlist);
        if (getIntent() != null) {
            PlaylistId = getIntent().getStringExtra("PlaylistID");
            PlaylistName = getIntent().getStringExtra("PlaylistName");
            PlaylistImage = getIntent().getStringExtra("PlaylistImage");
        }
        Bundle bundle = new Bundle();
        Fragment myPlaylistsFragment = new MyPlaylistsFragment();
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        bundle.putString("New", "0");
        bundle.putString("PlaylistID", PlaylistId);
        bundle.putString("PlaylistName", PlaylistName);
        bundle.putString("PlaylistImage", PlaylistImage);
        bundle.putString("MyDownloads", "1");
        myPlaylistsFragment.setArguments(bundle);
        comefrom_search = 3;
        fragmentManager1.beginTransaction()
                .replace(R.id.flContainer, myPlaylistsFragment)
                .commit();
    }
}