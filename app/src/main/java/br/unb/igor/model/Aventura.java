package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by maxim on 04/09/2017.
 */

public class Aventura implements Parcelable {
    String titulo;
    String key;
    String dataProximaSessao;
    int progresso;
    int RESOURCE_IMAGE;
    int numeroSessoes;
    String sinopse;
    String mestreUserId;
    String livroReferencia;
    List<String> jogadoresUserIds;
    List<String> anotacoes;
    List<FichaJogador> fichas;
    List<Sessao> sessoes;

    public Aventura() {
        // Empty constructor for json deserialization =)
    }

    public Aventura(String tituloAventura, String proximaSessao, String mestreUserId) {
        Random gerador = new Random();
        this.titulo = tituloAventura;
        this.progresso = 0;
        this.RESOURCE_IMAGE = gerador.nextInt(6);
        this.mestreUserId = mestreUserId;
        this.dataProximaSessao = proximaSessao;
    }

    public int getIndexOf(List<Aventura> aventuras, String key){
        for(Aventura aventura : aventuras){
            if(aventura.getKey().equals(key)){
                return aventuras.indexOf(aventura);
            }
        }

        return -1;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Sessao> getSessoes() {
        if (this.sessoes == null) {
            this.sessoes = new ArrayList<>();
        }
        return this.sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        this.sessoes = sessoes;
    }

    public String getDataProximaSessao() {
        return dataProximaSessao;
    }

    public void setDataProximaSessao(String dataProximaSessao) {
        this.dataProximaSessao = dataProximaSessao;
    }

    public int getNumeroSessoes() {
        return numeroSessoes;
    }

    public void setNumeroSessoes(int numeroSessoes) {
        this.numeroSessoes = numeroSessoes;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getMestreUserId() {
        return mestreUserId;
    }

    public void setMestreUserId(String mestreUserId) {
        this.mestreUserId = mestreUserId;
    }

    public String getLivroReferencia() {
        return livroReferencia;
    }

    public void setLivroReferencia(String livroReferencia) {
        this.livroReferencia = livroReferencia;
    }

    public List<String> getJogadoresUserIds() {
        return jogadoresUserIds;
    }

    public void setJogadoresUserIds(List<String> jogadoresUserIds) {
        this.jogadoresUserIds = jogadoresUserIds;
    }

    public List<String> getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(List<String> anotacoes) {
        this.anotacoes = anotacoes;
    }

    public List<FichaJogador> getFichas() {
        return fichas;
    }

    public void setFichas(List<FichaJogador> fichas) {
        this.fichas = fichas;
    }

    public String getTitulo () {
        return this.titulo;
    }

    public int getProgresso () {
        return this.progresso;
    }

    public void setTitulo (String tituloAventura) {
        this.titulo = tituloAventura;
    }

    public void setProgresso (int progresso) {
        this.progresso = progresso;
    }

    public int getImageResource() {
        return this.RESOURCE_IMAGE;
    }

    public void setImageResource(int resource) {
        this.RESOURCE_IMAGE = resource;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tituloAventura", titulo);
        result.put("progresso", progresso);

        result.put("RESOURCE_IMAGE", RESOURCE_IMAGE);
        result.put("numeroSessoes", numeroSessoes);
        result.put("sinopse", sinopse);
        result.put("key", key);
        result.put("mestreUserId", mestreUserId);
        result.put("anotacoes", anotacoes);
        result.put("fichas", fichas);
        result.put("livroReferencia", livroReferencia);
        result.put("sessoes", sessoes);
        result.put("dataProximaSessao",dataProximaSessao);
        result.put("jogadoresUserIds", jogadoresUserIds);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(key);
        parcel.writeString(dataProximaSessao);
        parcel.writeInt(progresso);
        parcel.writeInt(RESOURCE_IMAGE);
        parcel.writeInt(numeroSessoes);
        parcel.writeString(sinopse);
        parcel.writeString(mestreUserId);
        parcel.writeString(livroReferencia);
        parcel.writeStringList(jogadoresUserIds);
        parcel.writeStringList(anotacoes);
//        List<FichaJogador> fichas;
        parcel.writeList(sessoes);
    }

    public static final Parcelable.Creator<Aventura> CREATOR = new Parcelable.Creator<Aventura>() {

        @Override
        public Aventura createFromParcel(Parcel parcel) {
            Aventura a = new Aventura();
            a.titulo = parcel.readString();
            a.key = parcel.readString();
            a.dataProximaSessao = parcel.readString();
            a.progresso = parcel.readInt();
            a.RESOURCE_IMAGE = parcel.readInt();
            a.numeroSessoes = parcel.readInt();
            a.sinopse = parcel.readString();
            a.mestreUserId = parcel.readString();
            a.livroReferencia = parcel.readString();
            a.jogadoresUserIds = new ArrayList<>();
            parcel.readStringList(a.jogadoresUserIds);
            a.anotacoes = new ArrayList<>();
            parcel.readStringList(a.anotacoes);

            parcel.readList(a.sessoes, Sessao.class.getClassLoader());
            return a;
        }

        @Override
        public Aventura[] newArray(int size) {
            return new Aventura[size];
        }
    };
}
