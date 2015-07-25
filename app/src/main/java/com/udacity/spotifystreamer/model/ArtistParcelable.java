package com.udacity.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ethan_yu on 15/6/12.
 */
public class ArtistParcelable implements Parcelable {
    private String artistName;
    private String spotifyId;
    private String thumbnailUrl;

    private ArtistParcelable(Parcel in) {
        artistName = in.readString();
        spotifyId = in.readString();
        thumbnailUrl = in.readString();
    }

    public ArtistParcelable(String artistName, String spotifyId, String thumbnailUrl) {
        this.artistName = artistName;
        this.spotifyId = spotifyId;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {

        out.writeString(artistName);
        out.writeString(spotifyId);
        out.writeString(thumbnailUrl);
    }

    public static final Parcelable.Creator<ArtistParcelable> CREATOR = new Parcelable.Creator<ArtistParcelable>() {
        public ArtistParcelable createFromParcel(Parcel in) {
            return new ArtistParcelable(in);
        }

        public ArtistParcelable[] newArray(int size) {
            return new ArtistParcelable[size];
        }
    };
}
