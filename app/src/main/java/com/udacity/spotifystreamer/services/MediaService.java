package com.udacity.spotifystreamer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

public class MediaService extends Service
                                implements MediaPlayer.OnPreparedListener,
                                           MediaPlayer.OnSeekCompleteListener,
                                           MediaPlayer.OnErrorListener,
                                           MediaPlayer.OnCompletionListener {

    final static String TAG = MediaService.class.getName();

    private static String ACTION_PLAY = "com.udacity.spotifystreamer.action.PLAY";
    public static final String MEDIA_SERVICE_MESSAGE = "com.udacity.spotifystreamer.services.MEDIA_SERVICE_MESSAGE";
    public static final String DURATION = "com.udacity.spotifystreamer.services.DURATION";
    public static final String COMPLETE = "com.udacity.spotifystreamer.services.COMPLETE";

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

    private String mPreviewUri;

    private boolean isPrepared;

    private int mSeekTo;

    private IBinder mBinder = new LocalBinder();

    public static Intent makeIntent(final Context context) {

        Intent intent = new Intent(context, MediaService.class);

        return intent;
    }

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
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_PLAY.equals(intent.getAction())) {

            final String action = intent.getStringExtra(ACTION_MEDIAPLAYER);
            mPreviewUri = intent.getDataString();
            final int seek_to = intent.getIntExtra(SEEK_TO, 0);

            try {
                if (ACTION_MEDIAPLAYER_PLAY.equals(action)) {
                    isPrepared = false;
                    mPlayer.reset();
                    mPlayer.setDataSource(mPreviewUri);
                    mPlayer.prepareAsync();

                } else if (ACTION_MEDIAPLAYER_RESUME.equals(action)) {

                    if (isPrepared)
                        mPlayer.start();
                    else {
                        mPlayer.reset();
                        mPlayer.setDataSource(mPreviewUri);
                        mPlayer.prepareAsync();
                    }

                } else if (ACTION_MEDIAPLAYER_PAUSE.equals(action)) {

                    mPlayer.pause();

                } else if (ACTION_MEDIAPLAYER_STOP.equals(action)) {

                    mPlayer.stop();
                    isPrepared = false;

                } else if (ACTION_MEDIAPLAYER_NEXT.equals(action) ||
                        ACTION_MEDIAPLAYER_PREVIOUS.equals(action)) {

                    isPrepared = false;
                    mPlayer.reset();
                    mPlayer.setDataSource(mPreviewUri);
                    mPlayer.prepareAsync();

                } else if (ACTION_MEDIAPLAYER_SEEK_TO.equals(action)) {

                    if (isPrepared == false) {
                        mPlayer.reset();
                        mPlayer.setDataSource(mPreviewUri);
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

    private void sendDuration() {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(
                        new Intent(MEDIA_SERVICE_MESSAGE)
                                .putExtra(DURATION, getDuration()));
    }

    @Override
    public void onDestroy() {
        mPlayer.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        isPrepared = false;
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(
                        new Intent(MEDIA_SERVICE_MESSAGE)
                                .putExtra(COMPLETE, true));
    }

    public class LocalBinder extends Binder {
        public MediaService getMediaServiceInstance() {
            return MediaService.this;
        }
    }

    public String getPreviewUri() {
        return mPreviewUri;
    }

    public int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration() / 1000;
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition() / 1000;
        }
        return 0;
    }

    public boolean isSongPlaying() {
        return isPrepared;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayer.setLooping(false);
        mPlayer.start();
        sendDuration();
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
