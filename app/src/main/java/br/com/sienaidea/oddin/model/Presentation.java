package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Presentation implements Parcelable {
    public static final String NAME = Presentation.class.getName();
    public static final String ARRAYLIST = Presentation.class.getName()+"ARRAYLIST";
    public static final int OPEN = 0;
    public static final int FINISHED = 1;
    private String personName, subject, createdat;
    private int id, status, instruction_id;

    public Presentation() {
    }

    public Presentation(String subject) {
        this.subject = subject;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

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

    public int getInstruction_id() {
        return instruction_id;
    }

    public void setInstruction_id(int instruction_id) {
        this.instruction_id = instruction_id;
    }

    protected Presentation(Parcel parcel) {
        setPersonName(parcel.readString());
        setId(parcel.readInt());
        setStatus(parcel.readInt());
        setSubject(parcel.readString());
        setCreatedat(parcel.readString());
        setInstruction_id(parcel.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getPersonName());
        dest.writeInt(getId());
        dest.writeInt(getStatus());
        dest.writeString(getSubject());
        dest.writeString(getCreatedat());
        dest.writeInt(getInstruction_id());
    }

    public static final Creator<Presentation> CREATOR = new Creator<Presentation>() {
        @Override
        public Presentation createFromParcel(Parcel source) {
            return new Presentation(source);
        }

        @Override
        public Presentation[] newArray(int size) {
            return new Presentation[size];
        }
    };
}
