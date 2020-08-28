package tfg.backend.scrapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import tfg.backend.domain.Dato;
import tfg.backend.domain.Serie;
import tfg.backend.utils.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Ieca {

    public static List<Serie> importFromCesta() throws Exception {
        List<Serie> series = new ArrayList<>();
        File initialFile = new File("src\\main\\java\\tfg\\backend\\resources\\cesta.json");
        InputStream is = new FileInputStream(initialFile);
        JSONTokener tokener = new JSONTokener(is);
        JSONArray cesta = new JSONArray(tokener);
        int i;
        for(i = 0; i < cesta.length(); i++){
            try{
                JSONObject serieJson =  cesta.getJSONObject(i);

                String codigo = serieJson.getString("codigo");
                JSONObject serieData = serieJson.getJSONArray("series").getJSONObject(0);
                String codTerritorio = serieData.getJSONObject("territorio").getString("ter_codigo");
                Integer tipoPeriodo = serieData.getJSONObject("periodos").getInt("tipo");
                String numPeriodos = tipoPeriodo.equals(2) ? "-1" : "24";

                JSONArray datosSerie = JsonReader.extractJson(
                        new URL("https://ws089.juntadeandalucia.es/indea-gestion/restServices/datosSerie?" +
                                "tipoPeriodo=" + tipoPeriodo +
                                "&codIndicador=" + codigo +
                                "&codTerritorio=" + codTerritorio +
                                "&numPeriodos=" + numPeriodos +
                                "&iniPeriodo=null&finPeriodo=null"));

                Serie serie = new Serie();
                serie.setCodigo(codigo);
                serie.setTasa(serieData.getJSONObject("indicador").isNull("tasa") ? null : serieData.getJSONObject("indicador").getJSONObject("tasa").getString("descripcion"));
                serie.setUnidad(serieData.getJSONObject("indicador").isNull("tasa") ? null : serieData.getJSONObject("indicador").getJSONObject("tasa").getJSONObject("unmEscDefecto").getJSONObject("unidad").getString("unm_descripcion"));
                serie.setDescripcion(serieJson.getString("descripcion"));
                serie.setNombreFuente(serieData.getJSONObject("fuente").getJSONObject("fte_id_organismo").getString("org_nombre"));
                serie.setSiglasFuente(serieData.getJSONObject("fuente").getJSONObject("fte_id_organismo").isNull("org_siglas") ? null : serieData.getJSONObject("fuente").getJSONObject("fte_id_organismo").getString("org_siglas"));
                serie.setTerritorio(serieData.getJSONObject("territorio").getString("ter_descripcion"));
                serie.setPeriodicidad(serieJson.getJSONObject("periodicidad").getString("per_descripcion"));

                if(serie.getTasa() == null){
                    JSONObject unidad = serieData.isNull("unidad") ? serieData.getJSONObject("unidadEscala") : serieData.getJSONObject("unidad");
                    serie.setTasa(unidad.getJSONObject("escala").isNull("esc_etiquetaDef") ? unidad.getJSONObject("escala").getString("esc_descripcion") : unidad.getJSONObject("escala").getString("esc_etiquetaDef"));
                    serie.setUnidad(unidad.getJSONObject("unidad").getString("unm_descripcion"));
                }
                if(serie.getSiglasFuente() == null){
                    serie.setSiglasFuente(Arrays.stream(serie.getNombreFuente().split(" ")).map(word -> word.charAt(0)).map(Object::toString).collect(Collectors.joining()));
                }

                Set<Dato> valores = new HashSet<>();

                for (int n=0; n<datosSerie.length(); n++){
                    Dato dato = new Dato();

                    dato.setAnio(datosSerie.getJSONObject(n).getString("dat_anio"));
                    dato.setPeriodo(datosSerie.getJSONObject(n).getString("dat_periodo"));
                    dato.setValor(datosSerie.getJSONObject(n).isNull("dat_dato") ? null : datosSerie.getJSONObject(n).getFloat("dat_dato"));

                    valores.add(dato);
                }
                serie.setDatos(valores);

                System.out.println("guardando serie "+i);
                series.add(serie);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return series;
    }
}
