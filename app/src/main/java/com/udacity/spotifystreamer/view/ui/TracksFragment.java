package com.udacity.spotifystreamer.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.udacity.spotifystreamer.R;
import com.udacity.spotifystreamer.common.BaseActivity;
import com.udacity.spotifystreamer.model.TrackParcelable;
import com.udacity.spotifystreamer.view.PlayerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TracksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TracksFragment extends Fragment {

    private static final String TAG = TracksFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SpotifyId = "SpotifyId";
    private static final String ArtistName = "ArtistName";

    // TODO: Rename and change types of parameters
    private String mSpotifyId;
    private String mArtistName;

    private OnFragmentInteractionListener mListener;

    private ArrayList<TrackParcelable> trackList;
    private ListView listViewTrack;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TracksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TracksFragment newInstance(String param1, String param2) {
        TracksFragment fragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putString(SpotifyId, param1);
        args.putString(ArtistName, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TracksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            trackList = new ArrayList<TrackParcelable>();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        if (savedInstanceState == null) {

            if (getArguments() != null) {

                mSpotifyId = getArguments().getString(SpotifyId);
                mArtistName = getArguments().getString(ArtistName);

            } else {

                mArtistName = getActivity()
                        .getIntent()
                        .getStringExtra("ArtistName");

                Log.d(TAG, "mArtistName: " + mArtistName);

                mSpotifyId = getActivity()
                        .getIntent()
                        .getStringExtra("SpotifyId");
            }

            searchTracks();

        } else {

            trackList = savedInstanceState.getParcelableArrayList("Tracks");
            mArtistName = savedInstanceState.getString("ArtistName");
            mSpotifyId = savedInstanceState.getString("SpotifyId");
        }

        initializeView(rootView);

        return rootView;
    }

    private void initializeView(View rootView) {

        listViewTrack = (ListView) rootView.findViewById(R.id.listViewTrack);

        listViewTrack.setAdapter(
                new ArtistListAdapter(
                        getActivity(), R.layout.artist, trackList));

        listViewTrack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (getArguments() != null) {

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    PlayerFragment.newInstance(i, trackList)
                            .show(fragmentManager, "dialog");

                } else {

                    Intent intent = new Intent(getActivity(), PlayerActivity.class)
                            .putExtra("Position", i)
                            .putExtra("Tracks", trackList);

                    startActivity(intent);
                }
            }
        });

        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(mArtistName);
    }

    private void searchTracks() {

        ((BaseActivity)getActivity()).showProgress(getResources().getString(R.string.show_progress));

        final SpotifyApi api = new SpotifyApi();

        final SpotifyService spotify = api.getService();

        Map qParam = new HashMap<String, Object>();

        qParam.put("country", "TW");

        spotify.getArtistTopTrack(mSpotifyId, qParam, new Callback<Tracks>() {
            @Override
            public void success(final Tracks tracks, Response response) {

                showTracks(tracks, response);
            }

            @Override
            public void failure(final RetrofitError error) {

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ((BaseActivity) getActivity()).finishProgress();

                            Toast.makeText(
                                    getActivity(),
                                    error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (NullPointerException e) {

                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    private void showTracks(final Tracks tracks, Response response) {

        try {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    trackList.clear();

                    if (tracks.tracks.size() == 0) {

                        Toast.makeText(
                                getActivity(),
                                "No tracks are found.",
                                Toast.LENGTH_SHORT).show();

                        if (getArguments() == null)
                            getActivity().finish();

                    } else {

                        for (Track track : tracks.tracks) {

                            trackList.add(new TrackParcelable(
                                    mArtistName,
                                    track.name,
                                    track.album.name,
                                    track.album.images.size() > 0 ? track.album.images.get(0).url : null,
                                    track.preview_url,
                                    track.duration_ms));
                        }
                    }

                    ((ArtistListAdapter) listViewTrack.getAdapter()).notifyDataSetChanged();

                    ((BaseActivity)getActivity()).finishProgress();
                }
            });

        } catch (NullPointerException e) {

            Log.d(TAG, "Error: " + e.getMessage());
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelableArrayList("Tracks", trackList);
        savedInstanceState.putString("ArtistName", mArtistName);
        savedInstanceState.putString("SpotifyId", mSpotifyId);
        super.onSaveInstanceState(savedInstanceState);
    }
}
