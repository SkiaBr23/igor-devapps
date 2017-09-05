package br.unb.igor.model;

import br.unb.igor.R;

/**
 * Created by maxim on 04/09/2017.
 */

public class Aventura {
    String tituloAventura;
    String proximaSessao;
    int progresso;
    int RESOURCE_IMAGE;

    public Aventura (String tituloAventura, String proximaSessao) {
        this.tituloAventura = tituloAventura;
        this.progresso = 0;
        this.proximaSessao = proximaSessao;
        RESOURCE_IMAGE = R.drawable.miniatura_krevast;
    }

    public String getTituloAventura () {
        return this.tituloAventura;
    }

    public String getProximaSessao () {
        return this.proximaSessao;
    }

    public int getProgresso () {
        return this.progresso;
    }

    public void setTituloAventura (String tituloAventura) {
        this.tituloAventura = tituloAventura;
    }

    public void setProximaSessao (String proximaSessao) {
        this.proximaSessao = proximaSessao;
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
