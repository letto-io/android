package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 1/20/2017.
 */

public class Alternative implements Parcelable {
    public static final String TAG = Alternative.class.getSimpleName() ;
    private int id, choice_count;
    private String description;

    public Alternative() {
    }

    protected Alternative(Parcel in) {
        id = in.readInt();
        choice_count = in.readInt();
        description = in.readString();
    }

    public static final Creator<Alternative> CREATOR = new Creator<Alternative>() {
        @Override
        public Alternative createFromParcel(Parcel in) {
            return new Alternative(in);
        }

        @Override
        public Alternative[] newArray(int size) {
            return new Alternative[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChoice_count() {
        return choice_count;
    }

    public void setChoice_count(int choice_count) {
        this.choice_count = choice_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(choice_count);
        parcel.writeString(description);
    }
}
