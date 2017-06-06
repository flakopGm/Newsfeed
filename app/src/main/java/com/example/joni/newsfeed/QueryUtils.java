package com.example.joni.newsfeed;


import android.app.LoaderManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase con métodos de ayuda relacionados con la solicitud de red y recepción de datos JSON
 */

public class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static Context context;

    /**
     * Cree un constructor privado porque nadie debería crear un objeto {@link QueryUtils}.
     * Esta clase sólo está destinada a mantener variables estáticas y métodos, a los que se puede
     * acceder directamente desde el nombre de clase QueryUtils (y no se necesita una instancia de
     * objeto de QueryUtils).
     */
    private QueryUtils() {
    }

    public static List<Noticia> recogerDatosNoticia(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Creacion url.
        URL url = crearUrl(requestUrl);

        // Realizar solicitud HTTP a la dirección URL y recibir una respuesta JSON
        String jsonResponse = null;
        try {
            jsonResponse = crearSolicitudHttp(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error al cerrar el flujo de entrada", e);
        }
        //Extraiga los campos relevantes de la respuesta JSON y cree un objeto {@link Terremoto}
        List<Noticia> listaNoticias = extraerNoticias(jsonResponse);

        return listaNoticias;
    }

    /**
     * Devuelve el nuevo objeto URL de la URL de cadena dada.
     */
    private static URL crearUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
            Toast.makeText(QueryUtils.context, R.string.error,
                    Toast.LENGTH_SHORT).show();
        }
        return url;
    }

    /**
     * Realiza una solicitud HTTP a la URL dada y devuelve una cadena como respuesta.
     */
    private static String crearSolicitudHttp(URL url) throws IOException {
        String jsonResponse = "";

        // Si la URL es nula, devolver JSON.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Si la solicitud fue satisfactoria (código de respuesta 200), lea el flujo de entrada
            // y analiza la respuesta.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the feeds JSON results.", e);
            Toast.makeText(context, R.string.error,
                    Toast.LENGTH_SHORT).show();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convierta el {@link InputStream} en una String que contenga toda la respuesta JSON del
     * servidor.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Devuelve una lista de objetos {@link Noticia} que se ha creado a partir del
     * análisis de una respuesta JSON.
     */
    public static List<Noticia> extraerNoticias(String noticiaJSON) {
        if (TextUtils.isEmpty(noticiaJSON)) {
            return null;
        }
        // Crear una ArrayList vacía que podemos comenzar a agregar noticias.
        List<Noticia> listaNoticias = new ArrayList<>();

        // Intente analizar el JSON_RESPONSE. Si hay un problema con la forma en que se
        // formatea el JSON, se lanzará un objeto de excepción JSONException. Coge la excepción
        // para que la aplicación no se bloquee e imprima el mensaje de error en los registros.
        try {
            JSONObject lista = new JSONObject(noticiaJSON);
            JSONObject response = lista.getJSONObject("response");
            JSONArray resultados = response.getJSONArray("results");

            for (int i = 0; i < resultados.length(); i++) {
                JSONObject noticia = resultados.getJSONObject(i);

                // Recogemos Título, tipo, sección, fecha de publicación y la url de cada noticia.
                String tituloNoticia = noticia.getString("webTitle");
                String autorsNoticia = noticia.getString("type");
                String fechaNoticia = noticia.getString("webPublicationDate");
                String seccionNoticia = noticia.getString("sectionName");
                String urlNoticia = noticia.getString("webUrl");
                Noticia currentNoticia = new Noticia(tituloNoticia, autorsNoticia, fechaNoticia,
                        seccionNoticia, urlNoticia);
                listaNoticias.add(currentNoticia);
            }

        } catch (JSONException e) {
            //Si se produce un error al ejecutar cualquiera de las instrucciones anteriores en el
            // bloque "try", tome la excepción aquí, para que la aplicación no se bloquee. Imprima
            // un mensaje de registro con el mensaje de la excepción.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            Toast.makeText(context, R.string.error,
                    Toast.LENGTH_SHORT).show();
        }
        // Devolver la lista de terremotos.
        return listaNoticias;
    }
}
