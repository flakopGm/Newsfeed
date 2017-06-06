package com.example.joni.newsfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adaptador para la clase Noticia.
 */

public class NoticiaAdapter extends ArrayAdapter<Noticia> {

    private String fecha;

    public NoticiaAdapter(Context context, List<Noticia> listaDeNoticias) {
        super(context, 0, listaDeNoticias);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Compruebe si se está reutilizando una vista existente, de lo contrario infle la vista
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.disposicion_lista, parent, false);

            // Localización de la noticia actual.
            Noticia currentNoticia = getItem(position);
            // Localizamos el TextView con el id titulo y definimos su título según su posición.
            TextView tituloN = (TextView) listItemView.findViewById(R.id.titulo);
            String tituloCompleto = currentNoticia.getTituloNoticia();
            tituloN.setText(tituloCompleto);
            // Localizamos el TextView donde se expondrá el tipo de noticia.
            TextView tipoNoticia = (TextView) listItemView.findViewById(R.id.tipo_noticia);
            String tipofeed = currentNoticia.getTipoNoticia();
            // Obtenemos su primera letra para luego mostrarla en Mayúscula su primera letra.
            char mayuscula = tipofeed.toUpperCase().charAt(0);
            String tipoCamelCase = mayuscula + tipofeed.substring(1);
            tipoNoticia.setText(tipoCamelCase);
            // Obtenemos la fecha según la posición y realizamos las acciones para mostrar la fecha
            // de una forma cualquiera, en esta ocasión porque me gusta asi ;).
            String fechaCompleta = currentNoticia.getFechaPublicacion();
            if (fechaCompleta.contains(("T"))) {
                String[] partesFecha = fechaCompleta.split("T");
                String[] fechaSinSeparador = partesFecha[0].split("-");
                fecha = fechaSinSeparador[2] + " " + fechaSinSeparador[1] + " " +
                        fechaSinSeparador[0];
            }
            // Localizamos el TextView donde se mostrará la fecha y la definimos.
            TextView fechaN = (TextView) listItemView.findViewById(R.id.fecha_Publicacion);
            fechaN.setText(fecha);
            // Localizamos el TextView donde se mostrará las secciones de las noticias y las
            // definimos.
            TextView seccionN = (TextView) listItemView.findViewById(R.id.seccion_noticia);
            seccionN.setText(currentNoticia.getSeccion());
            // Localizamos la Imagen del icono y comprobamos el valor de la sección según la posición
            // luego se establece el icono indicado o el predefinido.
            ImageView iconoSeccion = (ImageView) listItemView.findViewById(R.id.image_seccion);
            switch (currentNoticia.getSeccion().toString()) {
                case "Football":
                    iconoSeccion.setImageResource(R.drawable.foottball);
                    break;
                case "Sport":
                    iconoSeccion.setImageResource(R.drawable.sport);
                    break;
                case "Music":
                    iconoSeccion.setImageResource(R.drawable.music);
                    break;
                case "Crosswords":
                    iconoSeccion.setImageResource(R.drawable.crossword);
                    break;
                case "UK news":
                    iconoSeccion.setImageResource(R.drawable.uknew);
                    break;
                case "Environment":
                    iconoSeccion.setImageResource(R.drawable.environment);
                    break;
                case "World news":
                    iconoSeccion.setImageResource(R.drawable.worldnews);
                    break;
                case "Politics":
                    iconoSeccion.setImageResource(R.drawable.politica);
                    break;
                case "Film":
                    iconoSeccion.setImageResource(R.drawable.cine);
                    break;
                case "Technology":
                    iconoSeccion.setImageResource(R.drawable.robot);
                    break;
                default:
                    iconoSeccion.setImageResource(R.drawable.theguardian);
            }
        }
        return listItemView;
    }
}
