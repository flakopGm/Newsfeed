package com.example.joni.newsfeed;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * AsyncTaskLoader
 */

public class NoticiaLoader extends AsyncTaskLoader<List<Noticia>> {

    private String url;

    public NoticiaLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Noticia> loadInBackground() {
        //No realice la solicitud si no hay URL o si la primera URL es nula.
        if (url == null) {
            return null;
        }
        List<Noticia> listaNoticias = QueryUtils.recogerDatosNoticia(url);

        return listaNoticias;
    }
}
