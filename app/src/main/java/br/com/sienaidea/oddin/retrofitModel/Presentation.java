package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 01/08/2016.
 */
public class Presentation implements Parcelable {
    public static final String TAG = Presentation.class.getName();
    public static final String ARRAYLIST = "ARRAYLIST";
    public static final String NAME = Presentation.class.getName();
    public static final int OPEN = 0;
    public static final int FINISHED = 1;

    private int id, status;
    private String subject, created_at;
    private Instruction instruction;
    private Person person;

    public Presentation() {
    }

    protected Presentation(Parcel in) {
        id = in.readInt();
        status = in.readInt();
        subject = in.readString();
        created_at = in.readString();
        instruction = in.readParcelable(Instruction.class.getClassLoader());
        person = in.readParcelable(Person.class.getClassLoader());
    }

    public static final Creator<Presentation> CREATOR = new Creator<Presentation>() {
        @Override
        public Presentation createFromParcel(Parcel in) {
            return new Presentation(in);
        }

        @Override
        public Presentation[] newArray(int size) {
            return new Presentation[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(status);
        dest.writeString(subject);
        dest.writeString(created_at);
        dest.writeParcelable(instruction, flags);
        dest.writeParcelable(person, flags);
    }
}
