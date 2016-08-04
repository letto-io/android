package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Siena Idea on 04/08/2016.
 */
public class Question implements Parcelable {
    public static final String TAG = Question.class.getName();

    private int id, upvotes, downvotes, my_vote;
    private String text, created_at;
    private boolean anonymous;
    private Presentation presentation;
    private Person person;
    private List<Answer> answers;

    protected Question(Parcel in) {
        id = in.readInt();
        upvotes = in.readInt();
        downvotes = in.readInt();
        my_vote = in.readInt();
        text = in.readString();
        created_at = in.readString();
        anonymous = in.readByte() != 0;
        presentation = in.readParcelable(Presentation.class.getClassLoader());
        person = in.readParcelable(Person.class.getClassLoader());
        answers = in.createTypedArrayList(Answer.CREATOR);
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getMy_vote() {
        return my_vote;
    }

    public void setMy_vote(int my_vote) {
        this.my_vote = my_vote;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public void setPresentation(Presentation presentation) {
        this.presentation = presentation;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(upvotes);
        dest.writeInt(downvotes);
        dest.writeInt(my_vote);
        dest.writeString(text);
        dest.writeString(created_at);
        dest.writeByte((byte) (anonymous ? 1 : 0));
        dest.writeParcelable(presentation, flags);
        dest.writeParcelable(person, flags);
        dest.writeTypedList(answers);
    }
}
