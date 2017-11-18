package br.unb.igor.model;

import android.graphics.Bitmap;

public class Livro {

    String titulo;
    String idAddedBy;
    String urlFile;
    Bitmap capaLivro;
    boolean statusDownload;

    public Livro() {
        this.statusDownload = false;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdAddedBy() {
        return idAddedBy;
    }

    public void setIdAddedBy(String idAddedBy) {
        this.idAddedBy = idAddedBy;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public Bitmap getCapaLivro() {
        return capaLivro;
    }

    public void setCapaLivro(Bitmap capaLivro) {
        this.capaLivro = capaLivro;
    }

    public boolean isDownloaded() {
        return statusDownload;
    }

    public void setDownloaded(boolean statusDownload) {
        this.statusDownload = statusDownload;
    }
}
