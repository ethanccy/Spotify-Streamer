package com.udacity.spotifystreamer.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public abstract class ParcelableCommand implements Serializable, Parcelable {

    private static final long serialVersionUID = -1859929388387975695L;

    public abstract void execute(Context context,
                                 Bundle args);

    public abstract void unexecute(Context context);

    public ParcelableCommand() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeSerializable(this);
    }

    private ParcelableCommand(Parcel in) {

    }

    public static final Parcelable.Creator<ParcelableCommand> CREATOR =
            new Parcelable.Creator<ParcelableCommand>() {
                public ParcelableCommand createFromParcel(Parcel in) {
                    return (ParcelableCommand) in.readSerializable();
                }

                public ParcelableCommand[] newArray(int size) {
                    return new ParcelableCommand[size];
                }
            };
}
