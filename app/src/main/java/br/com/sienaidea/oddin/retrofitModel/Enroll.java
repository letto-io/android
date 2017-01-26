package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 1/26/2017.
 */

public class Enroll implements Parcelable{
    private int id;
    private int profile;
    Instruction instruction;
    Person person;

    public Enroll() {
    }

    protected Enroll(Parcel in) {
        id = in.readInt();
        profile = in.readInt();
        instruction = in.readParcelable(Instruction.class.getClassLoader());
        person = in.readParcelable(Person.class.getClassLoader());
    }

    public static final Creator<Enroll> CREATOR = new Creator<Enroll>() {
        @Override
        public Enroll createFromParcel(Parcel in) {
            return new Enroll(in);
        }

        @Override
        public Enroll[] newArray(int size) {
            return new Enroll[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(profile);
        parcel.writeParcelable(instruction, i);
        parcel.writeParcelable(person, i);
    }
}
