package br.com.sienaidea.oddin.retrofitModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Material implements Parcelable {
    public static String TAG = Material.class.getName();

    private int id, attachable_id;
    private String name, mime, attachable_type, uploaded_at, url;
    private boolean checked, downloaded, accepted;
    Person person;

    public Material() {
    }

    protected Material(Parcel in) {
        id = in.readInt();
        attachable_id = in.readInt();
        name = in.readString();
        mime = in.readString();
        attachable_type = in.readString();
        uploaded_at = in.readString();
        url = in.readString();
        checked = in.readByte() != 0;
        downloaded = in.readByte() != 0;
        accepted = in.readByte() != 0;
        person = in.readParcelable(Person.class.getClassLoader());
    }

    public static final Creator<Material> CREATOR = new Creator<Material>() {
        @Override
        public Material createFromParcel(Parcel in) {
            return new Material(in);
        }

        @Override
        public Material[] newArray(int size) {
            return new Material[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAttachable_id() {
        return attachable_id;
    }

    public void setAttachable_id(int attachable_id) {
        this.attachable_id = attachable_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getAttachable_type() {
        return attachable_type;
    }

    public void setAttachable_type(String attachable_type) {
        this.attachable_type = attachable_type;
    }

    public String getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(String uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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
        dest.writeInt(attachable_id);
        dest.writeString(name);
        dest.writeString(mime);
        dest.writeString(attachable_type);
        dest.writeString(uploaded_at);
        dest.writeString(url);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeByte((byte) (downloaded ? 1 : 0));
        dest.writeByte((byte) (accepted ? 1 : 0));
        dest.writeParcelable(person, flags);
    }
}
