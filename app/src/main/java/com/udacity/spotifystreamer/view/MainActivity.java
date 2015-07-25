package com.udacity.spotifystreamer.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.spotifystreamer.R;
import com.udacity.spotifystreamer.common.BaseActivity;
import com.udacity.spotifystreamer.view.ui.TracksFragment;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final String TRACKSFRAGMENT_TAG = "TFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.tracks_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tracks_container, new TracksFragment(), TRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
