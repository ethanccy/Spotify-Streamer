package com.udacity.spotifystreamer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.IOException;

public class MediaService extends Service
                                implements MediaPlayer.OnPreparedListener,
                                           MediaPlayer.OnSeekCompleteListener,
                                           MediaPlayer.OnErrorListener {

    final static String TAG = MediaService.class.getName();

    private static String ACTION_PLAY = "com.udacity.spotifystreamer.action.PLAY";

    public static final String SEEK_TO = "seek_to";
    public static final String ACTION_MEDIAPLAYER = "action";
    public static final String ACTION_MEDIAPLAYER_PLAY = "play";
    public static final String ACTION_MEDIAPLAYER_RESUME = "resume";
    public static final String ACTION_MEDIAPLAYER_PAUSE = "pause";
    public static final String ACTION_MEDIAPLAYER_STOP = "stop";
    public static final String ACTION_MEDIAPLAYER_NEXT = "next";
    public static final String ACTION_MEDIAPLAYER_PREVIOUS = "previous";
    public static final String ACTION_MEDIAPLAYER_SEEK_TO = "seek_to";

    private MediaPlayer mPlayer;

    private boolean isPrepared;

    private int mSeekTo;

    public static Intent makeIntent(final Context context,
                                    String action,
                                    Uri songUri,
                                    int seekTo) {

        Intent intent = new Intent(ACTION_PLAY,
                                   songUri,
                                   context,
                                   MediaService.class);

        intent.putExtra(ACTION_MEDIAPLAYER, action);
        intent.putExtra(SEEK_TO, seekTo);

        return intent;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        isPrepared = false;
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_PLAY.equals(intent.getAction())) {

            final String action = intent.getStringExtra(ACTION_MEDIAPLAYER);
            final String songUri = intent.getDataString();
            final int seek_to = intent.getIntExtra(SEEK_TO, 0);

            try {
                if (ACTION_MEDIAPLAYER_PLAY.equals(action)) {

                    mPlayer.setDataSource(songUri);
                    mPlayer.prepareAsync();

                } else if (ACTION_MEDIAPLAYER_RESUME.equals(action)) {

                    if (isPrepared)
                        mPlayer.start();

                } else if (ACTION_MEDIAPLAYER_PAUSE.equals(action)) {

                    mPlayer.pause();

                } else if (ACTION_MEDIAPLAYER_STOP.equals(action)) {

                    mPlayer.stop();
                    isPrepared = false;

                } else if (ACTION_MEDIAPLAYER_NEXT.equals(action) ||
                        ACTION_MEDIAPLAYER_PREVIOUS.equals(action)) {

                    mPlayer.reset();
                    mPlayer.setDataSource(songUri);
                    mPlayer.prepareAsync();

                } else if (ACTION_MEDIAPLAYER_SEEK_TO.equals(action)) {

                    if (isPrepared == false) {
                        mPlayer.reset();
                        mPlayer.setDataSource(songUri);
                        mPlayer.prepareAsync();
                        mSeekTo = seek_to;
                    } else
                        mPlayer.seekTo(seek_to * 1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mPlayer.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayer.setLooping(false);
        mPlayer.start();
        isPrepared = true;
        if (mSeekTo != 0) {
            mPlayer.seekTo(mSeekTo * 1000);
            mSeekTo = 0;
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (isPrepared)
            mPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        mPlayer.reset();
        isPrepared = false;
        return true;
    }
}
