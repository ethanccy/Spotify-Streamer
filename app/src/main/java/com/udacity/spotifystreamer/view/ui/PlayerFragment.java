package com.udacity.spotifystreamer.view.ui;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.spotifystreamer.R;
import com.udacity.spotifystreamer.model.TrackParcelable;
import com.udacity.spotifystreamer.utils.ParcelableCommandMediaService;

import java.util.ArrayList;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends DialogFragment {

    final static String TAG = PlayerFragment.class.getName();

    private static final String POSITION = "position";
    private static final String TRACKS = "tracks";

    public static final String ACTION_MEDIAPLAYER_PLAY = "play";
    public static final String ACTION_MEDIAPLAYER_RESUME = "resume";
    public static final String ACTION_MEDIAPLAYER_PAUSE = "pause";
    public static final String ACTION_MEDIAPLAYER_STOP = "stop";
    public static final String ACTION_MEDIAPLAYER_NEXT = "next";
    public static final String ACTION_MEDIAPLAYER_PREVIOUS = "previous";
    public static final String ACTION_MEDIAPLAYER_SEEK_TO = "seek_to";

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
    private SeekBar seekBar;

    private boolean mSongPlaying;
    private int mSeekTo;

    private ParcelableCommandMediaService parcelableCommandMediaService;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {

            int curProgress = seekBar.getProgress();
            if (curProgress < seekBar.getMax()) {
                seekBar.setProgress(curProgress + 1);
                handler.postDelayed(this, 1000);
            } else {
                mSongPlaying = false;
                seekBar.setProgress(0);
                imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);
                parcelableCommandMediaService.execute(
                        getActivity(),
                        ParcelableCommandMediaService.makeArgs(
                                ACTION_MEDIAPLAYER_STOP,
                                Uri.parse(mTracks.get(mPosition).getPreviewUrl()),
                                0)
                );
            }
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

            mSongPlaying = savedInstanceState.getBoolean("SongPlaying");
            mPosition = savedInstanceState.getInt("Position");
            mTracks = savedInstanceState.getParcelableArrayList("Tracks");
            mSeekTo = savedInstanceState.getInt("SeekTo");
        }

        parcelableCommandMediaService = new ParcelableCommandMediaService();

        initializeView(rootView);

        return rootView;
    }

    private void initializeView(View rootView) {

        textViewArtist = (TextView)rootView.findViewById(R.id.textViewArtist);
        textViewAlbum = (TextView)rootView.findViewById(R.id.textViewAlbum);
        textViewTrack = (TextView)rootView.findViewById(R.id.textViewTrack);
        textViewEnd  = (TextView)rootView.findViewById(R.id.textViewEnd);
        imageViewAlbum = (ImageView)rootView.findViewById(R.id.imageViewAlbum);
        seekBar = (SeekBar)rootView.findViewById(R.id.seekBar);

        if (mTracks != null) {

            updateCurrentTrack(mTracks.get(mPosition));
        }

        imageButtonPrevious = (ImageButton)rootView.findViewById(R.id.imageButtonPrevious);
        imageButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPosition  = (--mPosition + mTracks.size()) % mTracks.size();

                updateCurrentTrack(mTracks.get(mPosition));

                parcelableCommandMediaService.execute(
                        getActivity(),
                        ParcelableCommandMediaService.makeArgs(
                                ACTION_MEDIAPLAYER_PREVIOUS,
                                Uri.parse(mTracks.get(mPosition).getPreviewUrl()),
                                0)
                );

                seekBar.setProgress(0);
                imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                mSongPlaying = true;
            }
        });

        imageButtonPlayPause = (ImageButton)rootView.findViewById(R.id.imageButtonPlayPause);
        if (mSongPlaying)
            imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);

        imageButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSongPlaying) {
                    parcelableCommandMediaService.execute(
                            getActivity(),
                            ParcelableCommandMediaService.makeArgs(
                                    ACTION_MEDIAPLAYER_PAUSE,
                                    null,
                                    0)
                    );

                    imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);

                    handler.removeCallbacks(runnable);

                } else {
                    if (seekBar.getProgress() == 0) {

                        parcelableCommandMediaService.execute(
                                getActivity(),
                                ParcelableCommandMediaService.makeArgs(
                                        ACTION_MEDIAPLAYER_PLAY,
                                        Uri.parse(mTracks.get(mPosition).getPreviewUrl()),
                                        0)
                        );

                    } else {

                        parcelableCommandMediaService.execute(
                                getActivity(),
                                ParcelableCommandMediaService.makeArgs(
                                        ACTION_MEDIAPLAYER_RESUME,
                                        Uri.parse(mTracks.get(mPosition).getPreviewUrl()),
                                        0)
                        );
                    }

                    imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);

                    handler.postDelayed(runnable, 1000);
                }
                mSongPlaying = !mSongPlaying;
            }
        });

        imageButtonNext = (ImageButton)rootView.findViewById(R.id.imageButtonNext);
        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPosition = ++mPosition % mTracks.size();

                updateCurrentTrack(mTracks.get(mPosition));

                parcelableCommandMediaService.execute(
                        getActivity(),
                        ParcelableCommandMediaService.makeArgs(
                                ACTION_MEDIAPLAYER_NEXT,
                                Uri.parse(mTracks.get(mPosition).getPreviewUrl()),
                                0)
                );

                seekBar.setProgress(0);
                imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                mSongPlaying = true;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    parcelableCommandMediaService.execute(
                            getActivity(),
                            ParcelableCommandMediaService.makeArgs(
                                    ACTION_MEDIAPLAYER_SEEK_TO,
                                    Uri.parse(mTracks.get(mPosition).getPreviewUrl()),
                                    progress)
                    );
                    imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    mSongPlaying = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setMax((int) (mTracks.get(mPosition).getDuration_ms() / 1000));

        textViewEnd.setText(getEndTimeString(seekBar.getMax()));

        if (mSeekTo > 0) {

            seekBar.setProgress(mSeekTo);
            parcelableCommandMediaService.execute(
                    getActivity(),
                    ParcelableCommandMediaService.makeArgs(
                            ACTION_MEDIAPLAYER_SEEK_TO,
                            Uri.parse(mTracks.get(mPosition).getPreviewUrl()),
                            mSeekTo)
            );

            imageButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);

            handler.postDelayed(runnable, 1000);
        }
    }

    private String getEndTimeString(int seconds) {

        int minute = seconds / 60;

        StringBuffer sb = new StringBuffer();
        sb.append(minute)
                .append(":")
                .append(
                        (seconds - minute * 60) > 9 ? (seconds - minute * 60) : "0" + (seconds - minute * 60));

        return sb.toString();
    }

    private void updateCurrentTrack(TrackParcelable track) {

        textViewArtist.setText(track.getArtistName());
        textViewAlbum.setText(track.getAlbumName());
        textViewTrack.setText(track.getTrackName());
        if (track.getImageUrl() != null)
            Picasso.with(getActivity()).load(track.getImageUrl()).into(imageViewAlbum);

        seekBar.setMax((int) (mTracks.get(mPosition).getDuration_ms() / 1000));

        textViewEnd.setText(getEndTimeString(seekBar.getMax()));
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
    public void onDestroy() {

        parcelableCommandMediaService.unexecute(getActivity());

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean("SongPlaying", mSongPlaying);
        savedInstanceState.putInt("Position", mPosition);
        savedInstanceState.putParcelableArrayList("Tracks", mTracks);
        savedInstanceState.putInt("SeekTo", seekBar.getProgress());

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
