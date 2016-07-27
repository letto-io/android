package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siena Idea on 27/07/2016.
 */
public class Session implements Parcelable {
    public static String TAG = Session.class.getName();

    private String id, token, user_id, created_at, updated_at;

    protected Session(Parcel in) {
        id = in.readString();
        token = in.readString();
        user_id = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
        dest.writeString(id);
        dest.writeString(token);
        dest.writeString(user_id);
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }
}
