package br.unb.igor.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Jogada {

    private String key;
    private String keyAventura;
    private String comando;
    private String resultado;
    private String idAutor;
    private String nomeAutor;
    private String nomePersonagem;
    private String urlFotoAutor;
    private Date timestamp;
    private Double probabilidade;
    private Double probabilidadePeloMenos;
    private TipoComando tipo;

    public enum TipoComando {
        DADO, DADO_MAXIMO, DADO_MINIMO, DADO_CRITICO, DADO_FALHA, EMOJI, TEXTO;
    }

    public Jogada() {
        this.timestamp = new Date();
    }

    public Jogada(String comando, String resultado, String idAutor, String nomeAutor, String nomePersonagem, String urlFotoAutor, TipoComando tipo) {
        this.resultado = resultado;
        this.idAutor = idAutor;
        this.nomeAutor = nomeAutor;
        this.nomePersonagem = nomePersonagem;
        this.urlFotoAutor = urlFotoAutor;
        this.tipo = tipo;
        this.timestamp = new Date();

        setComando(comando);
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

    public Double getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(Double probabilidade) {
        this.probabilidade = probabilidade;
    }

    public Double getProbabilidadePeloMenos() {
        return probabilidadePeloMenos;
    }

    public void setProbabilidadePeloMenos(Double probabilidade) {
        this.probabilidadePeloMenos = probabilidade;
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

    public TipoComando getTipo() {
        return tipo;
    }

    public void setTipo(TipoComando tipo) {
        this.tipo = tipo;
    }

    public static TipoComando getTipoRolagem(int resultado, int qtdDado, int tipoDado, int modificador){
        if(tipoDado == 20 && qtdDado == 1 && resultado - modificador == 20){
            return TipoComando.DADO_CRITICO;
        } else if(tipoDado == 20 && qtdDado == 1 && resultado - modificador == 1){
            return TipoComando.DADO_FALHA;
        } else if(resultado - modificador == qtdDado*tipoDado){
            return TipoComando.DADO_MAXIMO;
        } else if(resultado - modificador == qtdDado){
            return TipoComando.DADO_MINIMO;
        }
        return TipoComando.DADO;
    }
}
