package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by @skiabr23 on 08/09/2017.
 */

public class Convite implements Parcelable {

    String key;
    String tituloAventura;
    String mestreUserName;
    String mestreUserId;
    String keyAventura;
    String jogadorUserId;

    public Convite() {
    }

    public Convite(String tituloAventura, String mestreUserName, String mestreUserId, String keyAventura, String jogadorUserId) {
        this.tituloAventura = tituloAventura;
        this.mestreUserName = mestreUserName;
        this.mestreUserId = mestreUserId;
        this.keyAventura = keyAventura;
        this.jogadorUserId = jogadorUserId;
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

    public String getTituloAventura() {
        return tituloAventura;
    }

    public void setTituloAventura(String tituloAventura) {
        this.tituloAventura = tituloAventura;
    }

    public String getMestreUserName() {
        return mestreUserName;
    }

    public void setMestreUserName(String mestreUserName) {
        this.mestreUserName = mestreUserName;
    }

    public String getMestreUserId() {
        return mestreUserId;
    }

    public void setMestreUserId(String mestreUserId) {
        this.mestreUserId = mestreUserId;
    }

    public String getJogadorUserId() {
        return jogadorUserId;
    }

    public void setJogadorUserId(String jogadorUserId) {
        this.jogadorUserId = jogadorUserId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(keyAventura);
        parcel.writeString(key);
        parcel.writeString(tituloAventura);
        parcel.writeString(mestreUserId);
        parcel.writeString(mestreUserName);
        parcel.writeString(jogadorUserId);
    }

    public final static Creator<Convite> CREATOR = new Creator<Convite>() {

        @Override
        public Convite createFromParcel(Parcel parcel) {
            Convite s = new Convite();

            s.key = parcel.readString();
            s.jogadorUserId = parcel.readString();
            s.mestreUserId = parcel.readString();
            s.mestreUserName = parcel.readString();
            s.tituloAventura = parcel.readString();
            s.keyAventura = parcel.readString();
            s.key = parcel.readString();


            return s;
        }

        @Override
        public Convite[] newArray(int size) {
            return new Convite[size];
        }
    };
}

