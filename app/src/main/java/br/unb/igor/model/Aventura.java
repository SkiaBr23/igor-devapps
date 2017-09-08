package br.unb.igor.model;

import java.util.List;

import br.unb.igor.R;

/**
 * Created by maxim on 04/09/2017.
 */

public class Aventura {
    String tituloAventura;
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

    public Aventura(String tituloAventura, String proximaSessao, String mestreUserId) {
        this.tituloAventura = tituloAventura;
        this.progresso = 0;
        this.mestreUserId = mestreUserId;
        this.dataProximaSessao = proximaSessao;
        RESOURCE_IMAGE = R.drawable.miniatura_krevast;
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
        return this.tituloAventura;
    }

    public int getProgresso () {
        return this.progresso;
    }

    public void setTituloAventura (String tituloAventura) {
        this.tituloAventura = tituloAventura;
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
}
