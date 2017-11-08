package br.unb.igor.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Jogada {

    private String comando;
    private String resultado;
    private String idAutor;
    private String nomeAutor;
    private String nomePersonagem;
    private String urlFotoAutor;
    private Date timestamp;
    private int tipo;

    public enum TipoComando {
        DADO, DADO_ACERTO_CRITICO, DADO_FALHA_CRITICA, EMOJI, TEXTO;
    }


    public Jogada() {
        this.timestamp = new Date();
    }

    public Jogada(String comando, String resultado, String idAutor, String nomeAutor, String nomePersonagem, String urlFotoAutor, int tipo) {
        this.comando = comando;
        this.resultado = resultado;
        this.idAutor = idAutor;
        this.nomeAutor = nomeAutor;
        this.nomePersonagem = nomePersonagem;
        this.urlFotoAutor = urlFotoAutor;
        this.tipo = tipo;
        this.timestamp = new Date();
    }

    public String getTimeSent() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(this.timestamp);
    }

    public String getTimeSentMin() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(this.timestamp);
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNomePersonagem() {
        return nomePersonagem;
    }

    public void setNomePersonagem(String nomePersonagem) {
        this.nomePersonagem = nomePersonagem;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(String idAutor) {
        this.idAutor = idAutor;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getUrlFotoAutor() {
        return urlFotoAutor;
    }

    public void setUrlFotoAutor(String urlFotoAutor) {
        this.urlFotoAutor = urlFotoAutor;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
