package com.mirzahansuslu.seng449midtermapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button playButton,pauseButton,nextButton,prevButton;
    Button startServiceButton;
    private BroadcastReceiver broadcastReceiver;
    MediaPlayer mediaPlayer ;
    int currentMusic = 0;
    int length = 0;
    int currMusicId =0;
    TextView musicTitle,serviceText;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    serviceText.append("\n" +intent.getExtras().get("coordinates").toString());

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Integer> musicList = new ArrayList<>();
        ArrayList<Integer> locationList = new ArrayList<>();

        playButton = findViewById(R.id.playButton);

        pauseButton = findViewById(R.id.pauseButton);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        musicList.add(0,R.raw.dropit);
        musicList.add(0,R.raw.lifelike);
        musicList.add(0,R.raw.powerful);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),musicList.get(currentMusic));
        startServiceButton = findViewById(R.id.startServiceButton);

        serviceText = findViewById(R.id.locationText);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               playMusic();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              nextMusic(musicList);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               prevMusic(musicList);
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMusic(musicList);
            }
        });
        musicTitle = findViewById(R.id.musicTitleView);

        musicTitle.setText(showMusic(musicList));
        if(!runtime_permissions())
            enable_buttons();

    }

    public void playMusic() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        else {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }

    }
    public void nextMusic(ArrayList<Integer> musicList) {
        if(currentMusic < musicList.size() -1) {
            currentMusic++;
        }
        else {
            currentMusic=0;
        }

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(),musicList.get(currentMusic));
        mediaPlayer.start();
    }
    public void prevMusic(ArrayList<Integer> musicList) {
        if(currentMusic > 0) {
            currentMusic--;
        }
        else {
            currentMusic = musicList.size() -1 ;
        }

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(),musicList.get(currentMusic));
        mediaPlayer.start();
    }
    public void stopMusic(ArrayList<Integer> musicList) {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
              length=mediaPlayer.getCurrentPosition();

        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(),musicList.get(currentMusic));

    }
    public String showMusic(ArrayList<Integer> musicList) {
        for(int i=0;i<musicList.size();i++) {
             currMusicId = musicList.get(i);


        }
        return "Current song id : "+currMusicId;

     }
    private void enable_buttons() {

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),LocationTracker.class);
                startService(i);
            }
        });




    }
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }








}