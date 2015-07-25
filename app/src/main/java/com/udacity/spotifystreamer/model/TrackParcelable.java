package com.udacity.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ethan_yu on 15/6/17.
 */
public class TrackParcelable implements Parcelable {
    private String artistName;
    private String trackName;
    private String albumName;
    private String imageUrl;
    private String previewUrl;
    private long duration_ms;

    private TrackParcelable (Parcel in) {

        artistName = in.readString();
        trackName = in.readString();
        albumName = in.readString();
        imageUrl = in.readString();
        previewUrl = in.readString();
        duration_ms = in.readLong();
    }

    public TrackParcelable (String artistName,
                            String trackName,
                            String albumName,
                            String imageUrl,
                            String previewUrl,
                            long duration_ms) {

        this.artistName = artistName;
        this.trackName = trackName;
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
        this.duration_ms = duration_ms;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {

        out.writeString(artistName);
        out.writeString(trackName);
        out.writeString(albumName);
        out.writeString(imageUrl);
        out.writeString(previewUrl);
        out.writeLong(duration_ms);
    }

    public static final Parcelable.Creator<TrackParcelable> CREATOR = new Parcelable.Creator<TrackParcelable>() {
        public TrackParcelable createFromParcel(Parcel in) {
            return new TrackParcelable(in);
        }

        public TrackParcelable[] newArray(int size) {
            return new TrackParcelable[size];
        }
    };

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public long getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(long duration_ms) {
        this.duration_ms = duration_ms;
    }
}
