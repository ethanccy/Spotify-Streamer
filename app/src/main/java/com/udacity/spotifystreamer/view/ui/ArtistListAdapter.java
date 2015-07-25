package com.udacity.spotifystreamer.view.ui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.spotifystreamer.R;
import com.udacity.spotifystreamer.model.ArtistParcelable;
import com.udacity.spotifystreamer.model.TrackParcelable;

import java.util.List;

/**
 * Created by ethan_yu on 15/6/14.
 */
class ArtistListAdapter extends ArrayAdapter {

    final static String TAG = ArtistListAdapter.class.getName();

    Context context;
    int resource;
    List objects;

    public ArtistListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(resource, viewGroup, false);
        }

        final ImageView imageViewArtist = (ImageView)view.findViewById(R.id.imageViewArtist);

        String imageUrl = null;
        String name = null;
        if (objects.get(i) instanceof ArtistParcelable) {

            imageUrl = ((ArtistParcelable) objects.get(i)).getThumbnailUrl();
            name = ((ArtistParcelable) objects.get(i)).getArtistName();
        }
        else if (objects.get(i) instanceof TrackParcelable) {

            imageUrl = ((TrackParcelable) objects.get(i)).getImageUrl();

            name = new StringBuffer()
                    .append(((TrackParcelable) objects.get(i)).getTrackName())
                    .append("\n")
                    .append(((TrackParcelable) objects.get(i)).getAlbumName()).toString();

        }

        if (imageUrl != null) {

            Log.d(TAG, "Artist URI: " + imageUrl);
            Picasso.with(context).load(imageUrl).into(imageViewArtist);

        } else {

            Picasso.with(context).load(R.drawable.spotify).into(imageViewArtist);
        }

        final TextView textViewArtist = (TextView)view.findViewById(R.id.textViewArtist);
        textViewArtist.setText(name);

        return view;
    }
}
