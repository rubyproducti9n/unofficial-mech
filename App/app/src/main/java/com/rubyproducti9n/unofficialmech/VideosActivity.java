package com.rubyproducti9n.unofficialmech;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.ui.PlayerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideosActivity extends AppCompatActivity {
    private PlayerView videoView;
    private SimpleExoPlayer player;
    StyledPlayerView styledPlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        String videoPath = "https://drive.google.com/uc?export=download&id=1H0o9WotOcvuQy8c4YguRVyTM0oTHZn-g";           //storage location of the video to be played
        Uri videoUri = Uri.parse(videoPath);                   //converting it to Uri
        MediaItem mediaItem = MediaItem.fromUri(videoUri);     //build the MediaItem

        player = new SimpleExoPlayer.Builder(this).build();
        styledPlayerView = findViewById(R.id.styled_player_view);
        styledPlayerView.setPlayer(player);

        player.setMediaItem(mediaItem);   //now Player knows which Media to play

        player.prepare();               //getting ready for play
        player.play();

//        videoView = findViewById(R.id.video_view);
//
//        // Replace with your Google Drive video URL
//        String videoUrl = "https://drive.google.com/uc?export=view&id=VIDEO_ID";
//
//        // Initialize ExoPlayer
//        player = new SimpleExoPlayer.Builder(this).build();
//        videoView.setPlayer(player);
//
//        // Create a media source
//        Uri uri = Uri.parse(videoUrl);
//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, "MyApp"))).createMediaSource(MediaItem.fromUri(uri));
//
//        // Set the media source to the player
//        player.setMediaSource(mediaSource);
//        player.prepare();
//        player.setPlayWhenReady(true);

        List<Item> itemsList = new ArrayList<>();

        // Adding items to the list
        itemsList.add(new Item("Geetha Govindam Hindi Dubbed", "https://i0.wp.com/moviegalleri.net/wp-content/uploads/2018/06/Vijay-Devarakonda-Rashmika-Mandanna-Geetha-Govindam-First-Look-Poster-HD.jpg?ssl=1", "HD", "https://drive.google.com/uc?export=download&id=1H0o9WotOcvuQy8c4YguRVyTM0oTHZn-g"));

        // Displaying the items in the list
        System.out.println("Items List:");
        for (Item item : itemsList) {
            System.out.println(item);
            System.out.println();  // For better readability
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    class Item {
        private String name;
        private String imgUrl;
        private String quality;
        private String link;

        // Constructor to initialize the item
        public Item(String name, String imgUrl, String quality, String link) {
            this.name = name;
            this.imgUrl = imgUrl;
            this.quality = quality;
            this.link = link;
        }

        // Getter methods to access item details
        public String getName() {
            return name;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getSize() {
            return quality;
        }

        public String getLink() {
            return link;
        }

        @Override
        public String toString() {
            return "Name: " + name + "\nimgUrl: " + imgUrl + "\nSize: " + quality + "\nLink: " + link;
        }
    }
}
