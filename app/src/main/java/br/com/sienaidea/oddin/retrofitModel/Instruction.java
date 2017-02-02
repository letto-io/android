package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 28/07/2016.
 */
public class Instruction implements Parcelable {
    public static String TAG = Instruction.class.getName();

    private int id;
    private String start_date, end_date, class_code;
    private Event event;
    private Lecture lecture;

    public Instruction() {
    }

    protected Instruction(Parcel in) {
        id = in.readInt();
        start_date = in.readString();
        end_date = in.readString();
        class_code = in.readString();
        event = in.readParcelable(Event.class.getClassLoader());
        lecture = in.readParcelable(Lecture.class.getClassLoader());
    }

    public static final Creator<Instruction> CREATOR = new Creator<Instruction>() {
        @Override
        public Instruction createFromParcel(Parcel in) {
            return new Instruction(in);
        }

        @Override
        public Instruction[] newArray(int size) {
            return new Instruction[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getClass_code() {
        return class_code;
    }

    public void setClass_code(String class_code) {
        this.class_code = class_code;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(start_date);
        parcel.writeString(end_date);
        parcel.writeString(class_code);
        parcel.writeParcelable(event, i);
        parcel.writeParcelable(lecture, i);
    }
}
