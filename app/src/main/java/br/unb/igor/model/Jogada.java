package br.unb.igor.model;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jogada {

    private String comando;
    private String resultado;
    private String idAutor;
    private String nomeAutor;
    private String nomePersonagem;
    private String urlFotoAutor;
    private Date timestamp;
    private Double probabilidade;
    private int tipo;

    @Exclude
    private int faces;

    @Exclude
    private int diceCount;

    private static final Pattern regexDiceInfo = Pattern.compile("(\\d+)d(\\d+)");

    public enum TipoComando {
        DADO, DADO_ACERTO_CRITICO, DADO_FALHA_CRITICA, EMOJI, TEXTO;
    }

    public Jogada() {
        this.timestamp = new Date();
    }

    public Jogada(String comando, String resultado, String idAutor, String nomeAutor, String nomePersonagem, String urlFotoAutor, int tipo) {
        this.resultado = resultado;
        this.idAutor = idAutor;
        this.nomeAutor = nomeAutor;
        this.nomePersonagem = nomePersonagem;
        this.urlFotoAutor = urlFotoAutor;
        this.tipo = tipo;
        this.timestamp = new Date();

        setComando(comando);
    }

    public Double getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(Double probabilidade) {
        this.probabilidade = probabilidade;
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
        Matcher m = regexDiceInfo.matcher(comando);
        if (m.matches()) {
            diceCount = Integer.valueOf(m.group(1));
            faces = Integer.valueOf(m.group(2));
        } else {
            diceCount = 0;
            faces = 0;
        }
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

    @Exclude
    public int getFacesRolled() {
        return faces;
    }

    @Exclude
    public int getDiceRolled() {
        return diceCount;
    }
}
