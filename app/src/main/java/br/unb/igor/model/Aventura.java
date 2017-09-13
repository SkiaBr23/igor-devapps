package br.unb.igor.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unb.igor.R;

/**
 * Created by maxim on 04/09/2017.
 */

public class Aventura {
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
        this.titulo = tituloAventura;
        this.progresso = 0;
        this.mestreUserId = mestreUserId;
        this.dataProximaSessao = proximaSessao;
        RESOURCE_IMAGE = R.drawable.miniatura_krevast;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
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

    public String getTituloAventura () {
        return this.titulo;
    }

    public int getProgresso () {
        return this.progresso;
    }

    public void setTituloAventura (String tituloAventura) {
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
        result.put("jogadoresUserIds", jogadoresUserIds);

        return result;
    }
}