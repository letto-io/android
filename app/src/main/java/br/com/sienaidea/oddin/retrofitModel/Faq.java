package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 1/19/2017.
 */

public class Faq implements Parcelable{
    public static final String TAG = Faq.class.getSimpleName();
    private int id;
    private String question, answer;
    private boolean detailVisible;

    public Faq() {
    }

    protected Faq(Parcel in) {
        id = in.readInt();
        question = in.readString();
        answer = in.readString();
        detailVisible = in.readByte() != 0;
    }

    public static final Creator<Faq> CREATOR = new Creator<Faq>() {
        @Override
        public Faq createFromParcel(Parcel in) {
            return new Faq(in);
        }

        @Override
        public Faq[] newArray(int size) {
            return new Faq[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isDetailVisible() {
        return detailVisible;
    }

    public void setDetailVisible(boolean detailVisible) {
        this.detailVisible = detailVisible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(question);
        parcel.writeString(answer);
        parcel.writeByte((byte) (detailVisible ? 1 : 0));
    }
}
