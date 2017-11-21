package br.unb.igor.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

public class Livro {

    String titulo;
    String idAddedBy;
    String urlFile;
    String urlThumbnail;

    @Exclude
    boolean isDownloaded;


    public Livro() {
        this.isDownloaded = false;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
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

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }
}
