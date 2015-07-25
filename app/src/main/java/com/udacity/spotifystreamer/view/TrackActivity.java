package com.udacity.spotifystreamer.view;

import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.spotifystreamer.R;
import com.udacity.spotifystreamer.common.BaseActivity;
import com.udacity.spotifystreamer.view.ui.TracksFragment;


public class TrackActivity extends BaseActivity {

    final static String TAG = TrackActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        if (savedInstanceState == null) {

            String mArtistName =
                    getIntent().getStringExtra("ArtistName");

            Log.d(TAG, "mArtistName: " + mArtistName);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tracks_container, new TracksFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {

            case R.id.action_settings:
                return true;

            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
