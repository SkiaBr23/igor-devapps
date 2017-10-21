package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import br.unb.igor.helpers.Utils;

public class Aventura implements Parcelable {

    public static final String KEY_ID = "keyAventura";
    public static final String KEY_TITLE = "tituloAventura";
    public static final String KEY_IMAGE = "resBackgroundId";

    String titulo;
    String key;
    int progresso = 0;
    int RESOURCE_IMAGE = 0;
    int numeroSessoes = 0;
    String sinopse;
    String mestreUserId;
    String livroReferencia;
    List<String> jogadoresUserIds = new ArrayList<>();
    List<String> anotacoes = new ArrayList<>();
    List<FichaJogador> fichas = new ArrayList<>();
    List<Sessao> sessoes = new ArrayList<>();

    public Aventura() {
        // Empty constructor for json deserialization =)
    }

    public Aventura(String tituloAventura, String mestreUserId) {
        Random gerador = new Random();
        this.titulo = tituloAventura;
        this.progresso = 0;
        this.RESOURCE_IMAGE = gerador.nextInt(6);
        this.mestreUserId = mestreUserId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Sessao> getSessoes() {
        return this.sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        this.sessoes = sessoes;
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
    public Aventura fromMap(Map<String, Object> map) {
        Aventura a = new Aventura();
        a.titulo = (String)map.get("tituloAventura");
        return a;
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
        parcel.writeInt(progresso);
        parcel.writeInt(RESOURCE_IMAGE);
        parcel.writeInt(numeroSessoes);
        parcel.writeString(sinopse);
        parcel.writeString(mestreUserId);
        parcel.writeString(livroReferencia);
        parcel.writeStringList(jogadoresUserIds);
        parcel.writeStringList(anotacoes);
//        List<FichaJogador> fichas; // precisa implementar Parcel em FichaJogador
        parcel.writeList(sessoes);
    }

    public static final Parcelable.Creator<Aventura> CREATOR = new Parcelable.Creator<Aventura>() {

        @Override
        public Aventura createFromParcel(Parcel parcel) {
            Aventura a = new Aventura();
            a.titulo = parcel.readString();
            a.key = parcel.readString();
            a.progresso = parcel.readInt();
            a.RESOURCE_IMAGE = parcel.readInt();
            a.numeroSessoes = parcel.readInt();
            a.sinopse = parcel.readString();
            a.mestreUserId = parcel.readString();
            a.livroReferencia = parcel.readString();
            parcel.readStringList(a.jogadoresUserIds);
            parcel.readStringList(a.anotacoes);
            // ler FichaJogador
            parcel.readList(a.sessoes, Sessao.class.getClassLoader());
            return a;
        }

        @Override
        public Aventura[] newArray(int size) {
            return new Aventura[size];
        }
    };

    @Exclude
    public List<Sessao> getListaSessoes() {
        return sessoes;
    }

    @Exclude
    public int getIndexOf(List<Aventura> aventuras, String key){
        for(Aventura aventura : aventuras){
            if(aventura.getKey().equals(key)){
                return aventuras.indexOf(aventura);
            }
        }
        return -1;
    }

    public void addSessao(Sessao s) {
        sessoes.add(s);
        Collections.sort(sessoes, Utils.ComparatorSessionByDateDesc);
    }

    @Exclude
    public Sessao getProximaSessao() {
        if (sessoes != null && !sessoes.isEmpty()) {
            ArrayList<Sessao> copy = new ArrayList<>(sessoes);
            Collections.sort(copy, Utils.ComparatorSessionByDateAsc);
            Locale locale = Locale.getDefault();
            TimeZone timeZone = TimeZone.getDefault();
            Calendar now = Calendar.getInstance(timeZone, locale);
            now.set(Calendar.HOUR, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);
            now.set(Calendar.AM_PM, 0);
            Calendar sessionDate = Calendar.getInstance(timeZone, locale);
            try {
                for (Sessao sessao : copy) {
                    sessionDate.setTime(Utils.DateFormatter_MMddyy.parse(sessao.getData()));
                    if (sessionDate.after(now) || sessionDate.equals(now)) {
                        return sessao;
                    }
                }
            } catch (ParseException e) {
            }
        }
        return null;
    }
}
