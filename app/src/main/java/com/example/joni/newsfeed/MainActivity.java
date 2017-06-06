package com.example.joni.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal, cuenta con 3 cargadores para las diferentes pantallas de búsquedas.
 */

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Noticia>>,
        SwipeRefreshLayout.OnRefreshListener {

    //Sample JSON response for the guardian api.
    private final static String GUARDIAN_REQUEST_DEFAULT = "https://content.guardianapis.com/search?";
    private NoticiaAdapter adapter;
    private TextView emptyView;
    private ProgressBar progressBar;
    private ImageView fotoBackground;
    private EditText editText;
    private Uri.Builder uriBuilder;
    private int cargadorNumero = -1; // Control para los cargadores.
    private SwipeRefreshLayout swipeRefreshLayout; // Refrescar noticias.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Actualizador de noticias. Localización, escucha y definición de color para el progress.
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.actualizador_noticias);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));

        // Localización de elementos y definición de su contenido.
        fotoBackground = (ImageView) findViewById(R.id.foto_fondo);
        fotoBackground.setImageResource(R.drawable.reeding);
        editText = (EditText) findViewById(R.id.edit_text);
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        emptyView = (TextView) findViewById(R.id.empty_view);
        final LinearLayout botonesBusqueda = (LinearLayout) findViewById(R.id.botones_busqueda);
        final LinearLayout buscarNoticias = (LinearLayout) findViewById(R.id.busqueda_feed);
        final TextView ultimasNoticias = (TextView) findViewById(R.id.ultimas_noticias);
        ultimasNoticias.setText(R.string.ultimas_noticias);
        final ImageView atrasPrincipal = (ImageView) findViewById(R.id.atrasPrincipal);

        // Comprobación de conexión
        final ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Encuentra una referencia a {@link ListView} en el diseño
        final ListView noticiasListView = (ListView) findViewById(R.id.list);
        // Definimos el TextView para la vista vacía o sin resultados.
        noticiasListView.setEmptyView(emptyView);
        adapter = new NoticiaAdapter(this, new ArrayList<Noticia>());
        noticiasListView.setAdapter(adapter);

        // Escucha para cada item de la lista de noticias.
        noticiasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Noticia currentNoticia = adapter.getItem(position);
                String webNoticia = currentNoticia.getUrlNoticia();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webNoticia));
                startActivity(intent);

            }
        });

        // Escucha para el icono atrás de las pantallas de búsqueda.
        final ImageView iconoAtrasBusquedas = (ImageView) findViewById(R.id.atras);
        iconoAtrasBusquedas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        /*
        * Al ir atrás destruimos el cargador y no hace falta que muestre los datos ni el toast
        * informativo. Reseteamos la visibilidad de los elementos predeterminados
         */
                getLoaderManager().destroyLoader(cargadorNumero);
                buscarNoticias.setVisibility(View.INVISIBLE);
                editText.setText("");
                botonesBusqueda.setVisibility(View.VISIBLE);
                fotoBackground.setVisibility(View.VISIBLE);
                emptyView.setText("");
                cargadorNumero = -1;
                ultimasNoticias.setVisibility(View.VISIBLE);
            }
        });

        // Localización y escucha para el icono buscar.
        final ImageView iconoBuscar = (ImageView) findViewById(R.id.icono_busqueda);
        iconoBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyView.setText("");
                // Ocultación del teclado.
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService
                            (Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                // Limpiamos el adaptador de resultados antiguos.
                adapter.clear();
                // Ocultamos la foto de fondo para mostrar los datos.
                fotoBackground.setVisibility(View.GONE);
                // Conexión.
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                /*
                * Si la conexión es nula o no esta conectada indica al usuario mediante el TextView
                * de la emptyView, de lo contrario se establece el progressBar y los divisores de
                * la lista del mismo color que la pantalla de búsqueda en la que se encuentra el
                * usuario. Por último se llama al cargador con @restartLoader para que elimine datos
                * antiguos y muestre nuevos.
                * La segunda comprobación (después de comprobar la conexión) se realiza por control
                * de cargadores, ya que hay 3, aquí se gestionan 2.
                 */
                if (networkInfo != null && networkInfo.isConnected()) {
                    switch (cargadorNumero) {
                        case 0:
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                                    .getColor(R.color.porPalabra), PorterDuff.Mode.SRC_IN);
                            noticiasListView.getDivider().setColorFilter(getResources()
                                    .getColor(R.color.porPalabra), PorterDuff.Mode.SRC_IN);
                            getLoaderManager().restartLoader(0, null, MainActivity.this);
                            break;
                        case 2:
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                                    .getColor(R.color.porSeccion), PorterDuff.Mode.SRC_IN);
                            noticiasListView.getDivider().setColorFilter(getResources()
                                    .getColor(R.color.porSeccion), PorterDuff.Mode.SRC_IN);
                            getLoaderManager().restartLoader(2, null, MainActivity.this);
                            break;
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setText(R.string.compruebe_conexion);
                }
            }

        });

        // Localización y escucha para el icono borrar. Reseteamos los valores predeterminado y
        // limpiamos el adaptor de resultados antiguos.
        final ImageView iconoBorrar = (ImageView) findViewById(R.id.icono_borrar);
        iconoBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                fotoBackground.setVisibility(View.VISIBLE);
                adapter.clear();
                emptyView.setText("");
            }
        });

        // Localización, definición del texto y escucha para la búsqueda por palabras.
        final TextView porPalabra = (TextView) findViewById(R.id.busqueda_palabra);
        porPalabra.setText(R.string.buscar_por_noticias);
        porPalabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarNoticias.setVisibility(View.VISIBLE);
                buscarNoticias.setBackgroundResource(R.color.porPalabra);
                botonesBusqueda.setVisibility(View.INVISIBLE);
                cargadorNumero = 0;
                ultimasNoticias.setVisibility(View.GONE);
            }
        });

        // Localización, definición del texto y escucha para la búsqueda por sección.
        final TextView porSeccion = (TextView) findViewById(R.id.busqueda_seccion);
        porSeccion.setText(R.string.buscar_por_seccion);
        porSeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonesBusqueda.setVisibility(View.INVISIBLE);
                buscarNoticias.setVisibility(View.VISIBLE);
                buscarNoticias.setBackgroundResource(R.color.porSeccion);
                ultimasNoticias.setVisibility(View.GONE);
                cargadorNumero = 2;
            }
        });

        // Escucha para las últimas noticias.
        ultimasNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoBackground.setVisibility(View.GONE);
                emptyView.setText("");

                botonesBusqueda.setVisibility(View.GONE);
                atrasPrincipal.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                // Definición del color en el progressBar y el divider del ListView igual que la
                // pantalla de últimas noticias.
                progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                        .getColor(R.color.purple), PorterDuff.Mode.SRC_IN);
                noticiasListView.getDivider().setColorFilter(getResources()
                        .getColor(R.color.purple), PorterDuff.Mode.SRC_IN);

                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                // Si hay una conexión de red, busque datos.
                if (networkInfo != null && networkInfo.isConnected()) {
                    cargadorNumero = 1;
                    // Obtener una referencia a LoaderManager, con el fin de interactuar con los
                    // cargadores.
                    LoaderManager loaderManager = getLoaderManager();
                    /*
                    * Inicialice el cargador. Pase la constante de identificación interna definida
                    * anteriormente y pase en nulo para el paquete. Pase esta actividad para el
                    * parámetro LoaderCallbacks (que es válido porque esta actividad implementa la
                    * interfaz LoaderCallbacks).
                     */
                    loaderManager.restartLoader(1, null, MainActivity.this);
                } else { // Si no hay conexion mostrar información en el EmptyTextView
                    adapter.clear();
                    progressBar.setVisibility(View.GONE);
                    emptyView.setText(R.string.compruebe_conexion);
                }
            }
        });

        // Escucha para el botón atrás de últimas noticias.
        atrasPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atrasPrincipal.setVisibility(View.GONE);
                botonesBusqueda.setVisibility(View.VISIBLE);
                adapter.clear(); // Limpiamos resultados antiguos.
                fotoBackground.setVisibility(View.VISIBLE);
                emptyView.setText("");
                cargadorNumero = -1;
                getLoaderManager().destroyLoader(1); //Destruimos el cargados.
            }
        });
    }

    @Override
    public Loader<List<Noticia>> onCreateLoader(int id, Bundle args) {

        String busqueda = editText.getText().toString();
        int nCargador = id; // id de cada cargador.

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_DEFAULT);
        uriBuilder = baseUri.buildUpon();

        switch (nCargador) { // Según el cargador formará la uri definida para cada caso.
            case 0:
                uriBuilder.appendQueryParameter("q", busqueda);
                uriBuilder.appendQueryParameter("api-key", "0d974930-3149-4d53-8a30-dfe05c17dcbd");
                break;
            case 1:
                uriBuilder.appendQueryParameter("api-key", "0d974930-3149-4d53-8a30-dfe05c17dcbd");
                break;
            case 2:
                uriBuilder.appendQueryParameter("section", busqueda);
                uriBuilder.appendQueryParameter("api-key", "0d974930-3149-4d53-8a30-dfe05c17dcbd");
        }
        return new NoticiaLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Noticia>> loader, List<Noticia> data) {
        swipeRefreshLayout.setRefreshing(false); //Cargamos noticias pero no actualizamos.
        progressBar.setVisibility(View.GONE);
        if (fotoBackground.getVisibility() != View.VISIBLE &&
                progressBar.getVisibility() != View.VISIBLE) {
            emptyView.setText(R.string.no_resultados);
        }
        // Borre el adaptador de datos de noticias previas
        adapter.clear();
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
            Toast.makeText(MainActivity.this, R.string.info, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Noticia>> loader) {
        //Loader reset, para que podamos borrar nuestros datos existentes.
        adapter.clear();
    }

    @Override
    public void onRefresh() {
        // Reseteamos el cargador actual con el valor de @cargadorNumero para actualizar los datos.
        getLoaderManager().restartLoader(cargadorNumero, null, this);
    }
}
