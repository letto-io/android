package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Participant implements Parcelable {
    private String name;
    private int profile;
    private boolean online;

    public static final String NAME = Participant.class.getName();

    public Participant() {
    }

    protected Participant(Parcel in) {
        setName(in.readString());
        setProfile(in.readInt());
        setOnline(Boolean.parseBoolean(in.readString()));
    }

    public static final Creator<Participant> CREATOR = new Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel source) {
            return new Participant(source);
        }

        @Override
        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeInt(getProfile());
        dest.writeString(String.valueOf(isOnline()));
    }
}
