package com.udacity.spotifystreamer.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.udacity.spotifystreamer.R;
import com.udacity.spotifystreamer.common.BaseActivity;
import com.udacity.spotifystreamer.model.ArtistParcelable;
import com.udacity.spotifystreamer.view.TrackActivity;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArtistsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArtistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistsFragment extends Fragment {

    private static final String TAG = ArtistsFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<ArtistParcelable> artistList;

    private ListView listViewArtist;

    private boolean mDualPane;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistsFragment newInstance(String param1, String param2) {
        ArtistsFragment fragment = new ArtistsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        if (savedInstanceState == null || !savedInstanceState.containsKey("Artists")) {

            artistList = new ArrayList<ArtistParcelable>();

        } else {

            artistList = savedInstanceState.getParcelableArrayList("Artists");
        }

        initializeView(rootView);


        return rootView;
    }

    private void initializeView(View rootView) {

        listViewArtist = (ListView) rootView.findViewById(R.id.listViewArtist);

        listViewArtist.setAdapter(
                new ArtistListAdapter(
                        getActivity(),
                        R.layout.artist,
                        artistList));

        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (mDualPane) {

                    TracksFragment tracksFragment =
                            TracksFragment.newInstance(
                                    artistList.get(i).getSpotifyId(),
                                    artistList.get(i).getArtistName());

                    ((BaseActivity)getActivity()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.tracks_container, tracksFragment)
                            .commit();

                } else {

                    Intent intent = new Intent(getActivity(), TrackActivity.class)
                            .putExtra("SpotifyId", artistList.get(i).getSpotifyId())
                            .putExtra("ArtistName", artistList.get(i).getArtistName());

                    startActivity(intent);
                }
            }
        });

        final EditText editTextSearch = (EditText)rootView.findViewById(R.id.editTextSearch);

        editTextSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keycode == KeyEvent.KEYCODE_ENTER)) {

                    searchArtists(((EditText) view).getText().toString());

                    return true;
                }
                return false;
            }
        });
    }

    private void searchArtists(String artist) {

        ((BaseActivity)getActivity())
                .showProgress(getResources().getString(R.string.show_progress));

        final SpotifyApi api = new SpotifyApi();

        final SpotifyService spotify = api.getService();

        spotify.searchArtists(artist, new Callback<ArtistsPager>() {

            @Override
            public void success(final ArtistsPager artistsPager, Response response) {

                showArtists(artistsPager, response);
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

    private void showArtists(final ArtistsPager artistsPager, Response response) {

        try {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    artistList.clear();

                    if (artistsPager.artists.items.size() == 0) {

                        Toast.makeText(
                                getActivity(),
                                "No artists are found.",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        for (Artist artist : artistsPager.artists.items) {
                            artistList.add(
                                    new ArtistParcelable(
                                            artist.name,
                                            artist.id,
                                            (artist.images.size() > 0) ? artist.images.get(0).url : null));
                        }
                    }

                    ((ArtistListAdapter) listViewArtist.getAdapter()).notifyDataSetChanged();

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
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        View trackFrame = getActivity().findViewById(R.id.tracks_container);

        mDualPane = trackFrame != null;
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

        savedInstanceState.putParcelableArrayList("Artists", artistList);
        super.onSaveInstanceState(savedInstanceState);
    }
}
