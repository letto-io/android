package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Contribution implements Parcelable {
    public static final String NAME = Contribution.class.getName();
    private int id;
    private String text, createdat, personName;

    public Contribution() {
    }

    protected Contribution(Parcel parcel) {
        setId(parcel.readInt());
        setText(parcel.readString());
        setCreatedat(parcel.readString());
        setPersonName(parcel.readString());
    }

    public static final Creator<Contribution> CREATOR = new Creator<Contribution>() {
        @Override
        public Contribution createFromParcel(Parcel source) {
            return new Contribution(source);
        }

        @Override
        public Contribution[] newArray(int size) {
            return new Contribution[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getText());
        dest.writeString(getCreatedat());
        dest.writeString(getPersonName());
    }
}
