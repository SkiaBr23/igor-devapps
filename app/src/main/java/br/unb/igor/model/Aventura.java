package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.ArraySet;

import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import br.unb.igor.helpers.ImageAssets;
import br.unb.igor.helpers.Utils;

public class Aventura implements Parcelable {

    public static final String KEY_ID = "keyAventura";
    public static final String KEY_TITLE = "tituloAventura";
    public static final String KEY_IMAGE = "resBackgroundId";
    public static final Random PRNG = new Random();

    String titulo;
    String key;
    int progresso = 0;
    int imagemFundo = PRNG.nextInt(ImageAssets.getBuiltInBackgroundCount());
    String sinopse;
    String mestreUserId;
    String livroReferencia;
    List<String> jogadoresUserIds = new ArrayList<>();
    List<String> jogadoresConvidadosIds = new ArrayList<>();
    List<String> anotacoes = new ArrayList<>();
    List<FichaJogador> fichas = new ArrayList<>();
    List<Sessao> sessoes = new ArrayList<>();

    @Exclude
    Set<String> jogadoresConvidadosIdsSet = new ArraySet<>();

    public Aventura() {
    }

    public Aventura(String tituloAventura, String mestreUserId) {
        this.titulo = tituloAventura;
        this.mestreUserId = mestreUserId;
    }

    public String getTitulo () {
        return this.titulo;
    }

    public void setTitulo (String tituloAventura) {
        this.titulo = tituloAventura;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getProgresso () {
        return this.progresso;
    }

    public void setProgresso (int progresso) {
        this.progresso = progresso;
    }

    public int getImagemFundo() {
        return this.imagemFundo;
    }

    public void setImagemFundo(int resource) {
        this.imagemFundo = resource;
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

    public void setJogadoresUserIds(List<String> ids) {
        jogadoresUserIds.clear();
        jogadoresUserIds.addAll(ids);
    }

    public List<String> getJogadoresConvidadosIds() {
        return jogadoresConvidadosIds;
    }

    public void setJogadoresConvidadosIds(List<String> ids) {
        jogadoresConvidadosIds.clear();
        jogadoresConvidadosIds.addAll(ids);
        jogadoresConvidadosIdsSet.clear();
        jogadoresConvidadosIdsSet.addAll(ids);
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

    public List<Sessao> getSessoes() {
        return this.sessoes;
    }

    public void setSessoes(List<Sessao> sessoes) {
        this.sessoes = sessoes;
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
        result.put("imagemFundo", imagemFundo);
        result.put("sinopse", sinopse);
        result.put("key", key);
        result.put("invitedById", mestreUserId);
        result.put("anotacoes", anotacoes);
        result.put("fichas", fichas);
        result.put("livroReferencia", livroReferencia);
        result.put("sessoes", sessoes);
        result.put("jogadoresUserIds", jogadoresUserIds);
        result.put("jogadoresConvidadosIds", jogadoresConvidadosIds);
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
        parcel.writeInt(imagemFundo);
        parcel.writeString(sinopse);
        parcel.writeString(mestreUserId);
        parcel.writeString(livroReferencia);
        parcel.writeStringList(jogadoresUserIds);
        parcel.writeStringList(jogadoresConvidadosIds);
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
            a.imagemFundo = parcel.readInt();
            a.sinopse = parcel.readString();
            a.mestreUserId = parcel.readString();
            a.livroReferencia = parcel.readString();
            parcel.readStringList(a.jogadoresUserIds);
            parcel.readStringList(a.anotacoes);
            parcel.readStringList(a.jogadoresConvidadosIds);
            // ler FichaJogador
            parcel.readList(a.sessoes, Sessao.class.getClassLoader());
            a.jogadoresConvidadosIdsSet.addAll(a.jogadoresConvidadosIds);
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

    public boolean isUserInvited(String key) {
        return jogadoresConvidadosIdsSet.contains(key);
    }

    public boolean isUserInAdventure(String key) {
        return jogadoresUserIds.contains(key);
    }

    public void addInvitedUser(String key) {
        if (!isUserInvited(key)) {
            jogadoresConvidadosIds.add(key);
            jogadoresConvidadosIdsSet.add(key);
        }
    }

    public void removeInvitedUser(String key) {
        if (isUserInvited(key)) {
            jogadoresConvidadosIdsSet.remove(key);
            jogadoresConvidadosIds.remove(key);
        }
    }

    public void kickUser(String key) {
        if(isUserInAdventure(key)) {
            removeInvitedUser(key);
            jogadoresUserIds.remove(key);
        }
    }

    public void acceptUser(String key){
        if(!isUserInAdventure(key)) {
            removeInvitedUser(key);
            jogadoresUserIds.add(key);
        }
    }

    @Exclude
    public Set<String> getJogadoresUserIdsSet() {
        return new ArraySet<>(jogadoresUserIds);
    }

    @Exclude
    public Set<String> getJogadoresConvidadosIdsSet() {
        return jogadoresConvidadosIdsSet;
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

    public void assignInternal(Aventura other) {
        this.titulo = other.titulo;
        this.key = other.key;
        this.progresso = other.progresso;
        this.imagemFundo = other.imagemFundo;
        this.sinopse = other.sinopse;
        this.mestreUserId = other.mestreUserId;
        this.livroReferencia = other.livroReferencia;
        setJogadoresUserIds(other.jogadoresUserIds);
        setJogadoresConvidadosIds(other.jogadoresConvidadosIds);
        this.anotacoes = other.anotacoes;
        this.fichas = other.fichas;
        this.sessoes = other.sessoes;
    }
}
