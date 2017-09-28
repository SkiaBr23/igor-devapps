package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by x05119695116 on 08/09/2017.
 */

public class Sessao implements Parcelable {

    String resumo;
    int numeroSessao;
    String data;

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public int getNumeroSessao() {
        return numeroSessao;
    }

    public void setNumeroSessao(int numeroSessao) {
        this.numeroSessao = numeroSessao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(resumo);
        parcel.writeInt(numeroSessao);
        parcel.writeString(data);
    }

    public final static Parcelable.Creator<Sessao> CREATOR = new Parcelable.Creator<Sessao>() {

        @Override
        public Sessao createFromParcel(Parcel parcel) {
            Sessao s = new Sessao();
            s.resumo = parcel.readString();
            s.numeroSessao = parcel.readInt();
            s.data = parcel.readString();
            return s;
        }

        @Override
        public Sessao[] newArray(int size) {
            return new Sessao[size];
        }
    };
}

