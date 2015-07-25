package com.udacity.spotifystreamer.view.ui;

import android.app.Activity;
import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.spotifystreamer.R;
import com.udacity.spotifystreamer.model.TrackParcelable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends DialogFragment implements MediaPlayer.OnPreparedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POSITION = "position";
    private static final String TRACKS = "tracks";

    // TODO: Rename and change types of parameters
    private int mPosition;
    private ArrayList<TrackParcelable> mTracks;

    private OnFragmentInteractionListener mListener;

    private TextView textViewArtist;
    private TextView textViewAlbum;
    private TextView textViewTrack;
    private TextView textViewEnd;
    private ImageView imageViewAlbum;
    private ImageButton imageButtonPrevious;
    private ImageButton imageButtonPlayPause;
    private ImageButton imageButtonNext;
    private ProgressBar progressBar;

    private MediaPlayer player;
    private boolean isPrepared = false;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {

            int curProgress = progressBar.getProgress();
            progressBar.setProgress(curProgress + 1);
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @param tracks Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(int position, ArrayList<TrackParcelable> tracks) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putParcelableArrayList(TRACKS, tracks);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        if (savedInstanceState == null) {

            if (getArguments() != null) {

                mPosition = getArguments().getInt(POSITION);
                mTracks = getArguments().getParcelableArrayList(TRACKS);

            } else {

                mPosition = getActivity().getIntent().getIntExtra("Position", 0);
                mTracks = getActivity().getIntent().getParcelableArrayListExtra("Tracks");
            }
        } else {

            mPosition = savedInstanceState.getInt("Position");
            mTracks = savedInstanceState.getParcelableArrayList("Tracks");
        }

        initializeView(rootView);

        return rootView;
    }

    private void initializeView(View rootView) {

        textViewArtist = (TextView)rootView.findViewById(R.id.textViewArtist);
        textViewAlbum = (TextView)rootView.findViewById(R.id.textViewAlbum);
        textViewTrack = (TextView)rootView.findViewById(R.id.textViewTrack);
        textViewEnd  = (TextView)rootView.findViewById(R.id.textViewEnd);
        imageViewAlbum = (ImageView)rootView.findViewById(R.id.imageViewAlbum);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);

        if (mTracks != null) {

            updateCurrentTrack(mTracks.get(mPosition));
        }

        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(mTracks.get(mPosition).getPreviewUrl());
            player.setOnPreparedListener(this);
            player.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

        imageButtonPrevious = (ImageButton)rootView.findViewById(R.id.imageButtonPrevious);
        imageButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPosition > 0)
                    mPosition--;
                else
                    return;

                updateCurrentTrack(mTracks.get(mPosition));

                imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);

                player.reset();

                progressBar.setProgress(0);
                handler.removeCallbacks(runnable);

                try {
                    player.setDataSource(mTracks.get(mPosition).getPreviewUrl());
                    player.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        imageButtonPlayPause = (ImageButton)rootView.findViewById(R.id.imageButtonPlayPause);
        imageButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (player.isPlaying()) {

                    player.pause();

                    imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);

                    handler.removeCallbacks(runnable);

                } else {

                    player.start();

                    imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);

                    handler.postDelayed(runnable, 1000);
                }
            }
        });

        imageButtonNext = (ImageButton)rootView.findViewById(R.id.imageButtonNext);
        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPosition < mTracks.size() - 1)
                    mPosition++;
                else
                    return;

                updateCurrentTrack(mTracks.get(mPosition));

                imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);

                player.reset();

                progressBar.setProgress(0);
                handler.removeCallbacks(runnable);

                try {
                    player.setDataSource(mTracks.get(mPosition).getPreviewUrl());
                    player.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        progressBar.setMax((int) (mTracks.get(mPosition).getDuration_ms() / 1000));

        textViewEnd.setText(getEndTimeString(progressBar.getMax()));
    }

    private String getEndTimeString(int seconds) {

        int minute = seconds / 60;

        StringBuffer sb = new StringBuffer();
        sb.append(minute)
                .append(":")
                .append(seconds - minute * 60);

        return sb.toString();
    }

    private void updateCurrentTrack(TrackParcelable track) {

        textViewArtist.setText(track.getArtistName());
        textViewAlbum.setText(track.getAlbumName());
        textViewTrack.setText(track.getTrackName());
        if (track.getImageUrl() != null)
            Picasso.with(getActivity()).load(track.getImageUrl()).into(imageViewAlbum);

        progressBar.setMax((int) (mTracks.get(mPosition).getDuration_ms() / 1000));

        textViewEnd.setText(getEndTimeString(progressBar.getMax()));
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
    public void onStop() {

        player.release();
        player = null;

        super.onStop();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        player.start();

        imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);

        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("Position", mPosition);
        savedInstanceState.putParcelableArrayList("Tracks", mTracks);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {

        if (getDialog() != null) {

            getDialog().getWindow().setLayout(
                    getResources().getDisplayMetrics().widthPixels * 3 / 4,
                    getResources().getDisplayMetrics().heightPixels * 3 / 4);
        }

        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
