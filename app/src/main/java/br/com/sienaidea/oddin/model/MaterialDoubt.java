package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.sienaidea.oddin.retrofitModel.Material;

public class MaterialDoubt extends Material implements Parcelable {
    private int contribution_id;

    public MaterialDoubt() {
        super();
    }

    public int getContribution_id() {
        return contribution_id;
    }

    public void setContribution_id(int contribution_id) {
        this.contribution_id = contribution_id;
    }

    protected MaterialDoubt(Parcel parcel) {
        setContribution_id(parcel.readInt());
    }

    public static final Creator<MaterialDoubt> CREATOR = new Creator<MaterialDoubt>() {
        @Override
        public MaterialDoubt createFromParcel(Parcel in) {
            return new MaterialDoubt(in);
        }

        @Override
        public MaterialDoubt[] newArray(int size) {
            return new MaterialDoubt[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getContribution_id());
    }
}
