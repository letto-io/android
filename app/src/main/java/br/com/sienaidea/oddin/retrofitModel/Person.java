package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    public static final String TAG = Person.class.getName();
    public static final String NAME = "NAME";

    private int id;
    private String name, email;

    public Person() {
    }

    protected Person(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(email);
    }
}
