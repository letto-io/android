package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 28/07/2016.
 */
public class Instruction implements Parcelable {
    public static String TAG = Instruction.class.getName();

    private int id, class_number;
    private String start_date, end_date;
    private Event event;
    private Lecture lecture;

    protected Instruction(Parcel in) {
        id = in.readInt();
        class_number = in.readInt();
        start_date = in.readString();
        end_date = in.readString();
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

    public int getClass_number() {
        return class_number;
    }

    public void setClass_number(int class_number) {
        this.class_number = class_number;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(class_number);
        dest.writeString(start_date);
        dest.writeString(end_date);
        dest.writeParcelable(event, flags);
        dest.writeParcelable(lecture, flags);
    }
}
