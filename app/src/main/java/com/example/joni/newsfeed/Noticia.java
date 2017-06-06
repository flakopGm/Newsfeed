package com.example.joni.newsfeed;

/**
 * Clase {@Noticia} para mostrar su título, tipo de noticia, sección a la que pertenece y la url
 * de su información más detallada.
 */

public class Noticia {

    private String tituloNoticia;
    private String tipoNoticia;
    private String fechaPublicacion;
    private String seccion;
    private String urlNoticia;

    public Noticia(String titulo, String autores, String fechaPublicacion, String seccion,
                   String url) {
        this.tituloNoticia = titulo;
        this.tipoNoticia = autores;
        this.fechaPublicacion = fechaPublicacion;
        this.seccion = seccion;
        this.urlNoticia = url;
    }

    public String getTituloNoticia() {
        return tituloNoticia;
    }

    public String getTipoNoticia() {
        return tipoNoticia;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getUrlNoticia() {
        return urlNoticia;
    }
}
