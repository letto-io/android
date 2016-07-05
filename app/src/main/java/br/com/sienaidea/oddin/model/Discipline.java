package br.com.sienaidea.oddin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Discipline implements Parcelable {
    public static final String NAME = Discipline.class.getName();
    public static final int TEACHER = 2;
    private String codigo, nome, dataInicio, codEvent;
    private int profile, instruction_id, turma;

    public Discipline() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getCodEvent() {
        return codEvent;
    }

    public void setCodEvent(String codEvent) {
        this.codEvent = codEvent;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getInstruction_id() {
        return instruction_id;
    }

    public void setInstruction_id(int instruction_id) {
        this.instruction_id = instruction_id;
    }

    public int getTurma() {
        return turma;
    }

    public void setTurma(int turma) {
        this.turma = turma;
    }


    public Discipline(Parcel parcel){
        setCodigo(parcel.readString());
        setNome(parcel.readString());
        setDataInicio(parcel.readString());
        setTurma(parcel.readInt());
        setCodEvent(parcel.readString());
        setProfile(parcel.readInt());
        setInstruction_id(parcel.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getCodigo());
        dest.writeString(getNome());
        dest.writeString(getDataInicio());
        dest.writeInt(getTurma());
        dest.writeString(getCodEvent());
        dest.writeInt(getProfile());
        dest.writeInt(getInstruction_id());
    }

    public static final Creator<Discipline> CREATOR = new Creator<Discipline>() {
        @Override
        public Discipline createFromParcel(Parcel source) {
            return new Discipline(source);
        }

        @Override
        public Discipline[] newArray(int size) {
            return new Discipline[size];
        }
    };
}
