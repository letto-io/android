package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    private String id, name;

    public Person() {
    }

    protected Person(Parcel parcel) {
        setId(parcel.readString());
        setName(parcel.readString());
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getName());
    }
}
