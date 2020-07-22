package tfg.backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tfg.backend.domain.Dato;
import tfg.backend.domain.Serie;
import tfg.backend.repository.SerieRepository;
import tfg.backend.utils.JsonReader;
import tfg.backend.utils.SSLTool;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static tfg.backend.utils.JsonReader.readJsonFromUrl;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    public Page<Serie> findAll(Pageable pageable){
        return this.serieRepository.findAll(pageable);
    }

    public List<Serie> findAllById(List<Long> id){
        return (List<Serie>) this.serieRepository.findAllById(id);
    }

    public Serie findById(Long id) {
        return this.serieRepository.findById(id).get();
    }

    public Serie create(Serie serie){
        return this.serieRepository.save(serie);
    }

    public String importFromCesta() throws Exception {
        File initialFile = new File("src\\main\\java\\tfg\\backend\\utils\\cesta.json");
        InputStream is = new FileInputStream(initialFile);
        JSONTokener tokener = new JSONTokener(is);
        JSONObject cesta = new JSONObject(tokener);
        JSONArray datos = cesta.getJSONArray("datos");
        int i;
        for(i = 0; i < datos.length(); i++){
            JSONObject serieJson =  datos.getJSONObject(i);
            JSONObject indicador = serieJson.getJSONObject("indicador");
            String codigo = indicador.getString("codigo");
            JSONArray datosSerie = JsonReader.extractJson(new URL("https://ws089.juntadeandalucia.es/indea-gestion/restServices/datosSerie?tipoPeriodo=1&codIndicador="
                    +codigo+"&codTerritorio=99000&numPeriodos=24&iniPeriodo=null&finPeriodo=null"));

            Serie serie = new Serie();
            serie.setId(Long.valueOf(i));
            serie.setCodigo(codigo);
            serie.setDescripcion(indicador.getString("descripcion"));
            serie.setPeriodicidad(indicador.getJSONObject("periodicidad").getString("per_cod"));
            serie.setRuta(indicador.getString("ruta"));

            serie.setNombreFuente(serieJson.getJSONObject("fuente").getJSONObject("fte_id_organismo").getString("org_nombre"));
            serie.setSiglasFuente(serieJson.getJSONObject("fuente").getJSONObject("fte_id_organismo").getString("org_siglas"));

            Set<Dato> valores = new HashSet<>();


            for (int n=0; n<datosSerie.length(); n++){
                Dato dato = new Dato();

                dato.setAnio(datosSerie.getJSONObject(n).getString("dat_anio"));
                dato.setPeriodo(datosSerie.getJSONObject(n).getString("dat_periodo"));
                dato.setValor(datosSerie.getJSONObject(n).getFloat("dat_dato"));

                valores.add(dato);
            }
            serie.setDatos(valores);

            this.serieRepository.save(serie);
            System.out.println(serie.toString());
        }

        return "Se han insertado "+ i +" serie/s";
    }
}
