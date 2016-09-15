package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Siena Idea on 04/08/2016.
 */
public class Answer implements Parcelable {
    public static String TAG = Answer.class.getName();

    private int id, upvotes, downvotes, my_vote;
    private String text, created_at;
    private boolean anonymous, accepted;
    private List<Material> materials;
    private Question question;
    private Person person;

    public Answer() {
    }

    protected Answer(Parcel in) {
        id = in.readInt();
        upvotes = in.readInt();
        downvotes = in.readInt();
        my_vote = in.readInt();
        text = in.readString();
        created_at = in.readString();
        anonymous = in.readByte() != 0;
        accepted = in.readByte() != 0;
        materials = in.createTypedArrayList(Material.CREATOR);
        question = in.readParcelable(Question.class.getClassLoader());
        person = in.readParcelable(Person.class.getClassLoader());
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
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

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
        dest.writeInt(upvotes);
        dest.writeInt(downvotes);
        dest.writeInt(my_vote);
        dest.writeString(text);
        dest.writeString(created_at);
        dest.writeByte((byte) (anonymous ? 1 : 0));
        dest.writeByte((byte) (accepted ? 1 : 0));
        dest.writeTypedList(materials);
        dest.writeParcelable(question, flags);
        dest.writeParcelable(person, flags);
    }
}
