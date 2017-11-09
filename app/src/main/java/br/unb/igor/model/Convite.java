package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Convite implements Parcelable {

    String key;
    String invitedById;
    String keyAventura;
    Boolean unseen;
    String invitedByName;
    String urlPhoto;

    public Convite() {
    }

    public Convite(String invitedById, String keyAventura, String invitedByName, String urlPhoto) {
        this.invitedById = invitedById;
        this.keyAventura = keyAventura;
        this.invitedByName = invitedByName;
        this.urlPhoto = urlPhoto;
    }

    public Convite(String invitedById, String keyAventura) {
        this.invitedById = invitedById;
        this.keyAventura = keyAventura;
        this.unseen = true;
    }

    public String getInvitedByName() {
        return invitedByName;
    }

    public void setInvitedByName(String invitedByName) {
        this.invitedByName = invitedByName;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
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

    public String getInvitedById() {
        return invitedById;
    }

    public void setInvitedById(String invitedById) {
        this.invitedById = invitedById;
    }

    public Boolean getUnseen() {
        return unseen;
    }

    public void setUnseen(Boolean unseen) {
        this.unseen = unseen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(keyAventura);
        parcel.writeString(key);
        parcel.writeString(invitedById);
        parcel.writeInt(unseen ? 1 : 0);
    }

    public final static Creator<Convite> CREATOR = new Creator<Convite>() {

        @Override
        public Convite createFromParcel(Parcel parcel) {
            Convite s = new Convite();
            s.keyAventura = parcel.readString();
            s.key = parcel.readString();
            s.invitedById = parcel.readString();
            s.unseen = parcel.readInt() == 1;
            return s;
        }

        @Override
        public Convite[] newArray(int size) {
            return new Convite[size];
        }
    };
}

