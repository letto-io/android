package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Siena Idea on 1/20/2017.
 */

public class Survey implements Parcelable{
    public static final String TAG = Survey.class.getSimpleName();
    private int id, my_vote;
    private String title, question, created_at;
    private List<Alternative> alternatives;

    public Survey() {
    }

    protected Survey(Parcel in) {
        id = in.readInt();
        my_vote = in.readInt();
        title = in.readString();
        question = in.readString();
        created_at = in.readString();
        alternatives = in.createTypedArrayList(Alternative.CREATOR);
    }

    public static final Creator<Survey> CREATOR = new Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel in) {
            return new Survey(in);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMy_vote() {
        return my_vote;
    }

    public void setMy_vote(int my_vote) {
        this.my_vote = my_vote;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(my_vote);
        parcel.writeString(title);
        parcel.writeString(question);
        parcel.writeString(created_at);
        parcel.writeTypedList(alternatives);
    }
}
