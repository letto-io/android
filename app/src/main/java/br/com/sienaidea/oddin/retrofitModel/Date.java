package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 23/09/2016.
 */

public class Date implements Parcelable {
    public static final String TAG = Date.class.getSimpleName();
    private int id, instruction_id, person_id;
    private String subject, text, date, created_at, updated_at;

    public Date() {
    }

    protected Date(Parcel in) {
        id = in.readInt();
        instruction_id = in.readInt();
        person_id = in.readInt();
        subject = in.readString();
        text = in.readString();
        date = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<Date> CREATOR = new Creator<Date>() {
        @Override
        public Date createFromParcel(Parcel in) {
            return new Date(in);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInstruction_id() {
        return instruction_id;
    }

    public void setInstruction_id(int instruction_id) {
        this.instruction_id = instruction_id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(instruction_id);
        dest.writeInt(person_id);
        dest.writeString(subject);
        dest.writeString(text);
        dest.writeString(date);
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }
}
