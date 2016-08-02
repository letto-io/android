package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.sienaidea.oddin.retrofitModel.Person;

public class Doubt implements Parcelable {

    public static final String NAME = Doubt.class.getName();
    public static final String ARRAYLIST = "ARRAYLIST";
    public static final int OPEN = 0;
    public static final int CLOSED = 2;

    private Person person;
    private String text, createdat, time;
    private boolean like, anonymous, understand;
    private int id, status, likes, contributions, presentation_id, person_id;

    public Doubt() {
    }

    public Doubt(String text, boolean anonymous) {
        this.text = text;
        this.anonymous = anonymous;
    }

    protected Doubt(Parcel parcel) {
        setPerson(parcel.<Person>readParcelable(Person.class.getClassLoader()));
        setId(parcel.readInt());
        setStatus(parcel.readInt());
        setLikes(parcel.readInt());
        setLike(Boolean.parseBoolean(parcel.readString()));
        setContributions(parcel.readInt());
        setText(parcel.readString());
        setCreatedat(parcel.readString());
        setTime(parcel.readString());
        setAnonymous(Boolean.parseBoolean(parcel.readString()));
        setUnderstand(Boolean.parseBoolean(parcel.readString()));
        setPresentation_id(parcel.readInt());
        setPerson_id(parcel.readInt());
    }

    public static final Creator<Doubt> CREATOR = new Creator<Doubt>() {
        @Override
        public Doubt createFromParcel(Parcel source) {
            return new Doubt(source);
        }

        @Override
        public Doubt[] newArray(int size) {
            return new Doubt[size];
        }
    };

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isUnderstand() {
        return understand;
    }

    public void setUnderstand(boolean understand) {
        this.understand = understand;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void like() {
        this.likes++;
    }

    public void removeLike() {
        this.likes--;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }

    public int getPresentation_id() {
        return presentation_id;
    }

    public void setPresentation_id(int presentation_id) {
        this.presentation_id = presentation_id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getPerson(), flags);
        dest.writeInt(getId());
        dest.writeInt(getStatus());
        dest.writeInt(getLikes());
        dest.writeString(String.valueOf(isLike()));
        dest.writeInt(getContributions());
        dest.writeString(getText());
        dest.writeString(getCreatedat());
        dest.writeString(getTime());
        dest.writeString(String.valueOf(isAnonymous()));
        dest.writeString(String.valueOf(isUnderstand()));
        dest.writeInt(getPresentation_id());
        dest.writeInt(getPerson_id());
    }
}
