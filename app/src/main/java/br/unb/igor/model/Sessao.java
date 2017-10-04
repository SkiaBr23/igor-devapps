package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by x05119695116 on 08/09/2017.
 */

public class Sessao implements Parcelable {
    String key;
    String titulo;
    String resumo;
    String data;
    Boolean finalizada;
    String keyAventura;

    public Sessao() {
    }

    public Sessao(String keyAventura, String titulo, String data) {
        this.data = data;
        this.keyAventura = keyAventura;
        this.titulo = titulo;
        this.resumo = "";
        this.finalizada = false;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyAventura() {
        return keyAventura;
    }

    public void setKeyAventura(String keyAventura) {
        this.keyAventura = keyAventura;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Boolean getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(Boolean finalizada) {
        this.finalizada = finalizada;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
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
        parcel.writeString(data);
        parcel.writeInt((finalizada ? 1: 0));
    }

    public final static Parcelable.Creator<Sessao> CREATOR = new Parcelable.Creator<Sessao>() {

        @Override
        public Sessao createFromParcel(Parcel parcel) {
            Sessao s = new Sessao();
            s.resumo = parcel.readString();
            s.data = parcel.readString();
            s.finalizada = parcel.readInt() != 0;
            return s;
        }

        @Override
        public Sessao[] newArray(int size) {
            return new Sessao[size];
        }
    };
}

