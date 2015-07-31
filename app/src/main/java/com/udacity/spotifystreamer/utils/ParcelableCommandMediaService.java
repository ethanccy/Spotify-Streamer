package com.udacity.spotifystreamer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.udacity.spotifystreamer.common.ParcelableCommand;
import com.udacity.spotifystreamer.services.MediaService;

public class ParcelableCommandMediaService extends ParcelableCommand {

    private static final String TAG = ParcelableCommandMediaService.class.getSimpleName();
    private static final long serialVersionUID = -2687566537546879111L;

    public static final String SONG_URI = "song_uri";
    public static final String SEEK_TO = "seek_to";
    public static final String ACTION_MEDIAPLAYER = "action";

    private Intent mIntent;

    public ParcelableCommandMediaService() {
        super();
    }

    @Override
    public void execute(Context context, Bundle args) {
        final String action = args.getString(ACTION_MEDIAPLAYER);
        final Uri song_uri = args.getParcelable(SONG_URI);
        final int seekTo = args.getInt(SEEK_TO);

        mIntent = MediaService.makeIntent(
                context,
                action,
                song_uri,
                seekTo);

        context.startService(mIntent);
    }

    @Override
    public void unexecute(Context context) {
        context.stopService(mIntent);
    }

    public static Bundle makeArgs(String action, Uri songUri, int seekTo) {
        final Bundle args = new Bundle();
        args.putString(ACTION_MEDIAPLAYER, action);
        args.putParcelable(SONG_URI, songUri);
        args.putInt(SEEK_TO, seekTo);
        return args;
    }
}
