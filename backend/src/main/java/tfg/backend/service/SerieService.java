package tfg.backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import tfg.backend.domain.Dato;
import tfg.backend.dto.Filtro;
import tfg.backend.domain.Serie;
import tfg.backend.repository.SerieRepository;
import tfg.backend.utils.JsonReader;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public Page<Serie> findAll(Pageable pageable, String keyword, String fuente, String periodicidad, String territorio, String tasa){
        Serie serie = new Serie();
        serie.setDescripcion(keyword.isEmpty() ? null : keyword);
        serie.setSiglasFuente(fuente.isEmpty() ? null : fuente);
        serie.setPeriodicidad(periodicidad.isEmpty() ? null : periodicidad);
        serie.setTerritorio(territorio.isEmpty() ? null : territorio);

        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("descripcion", new ExampleMatcher.GenericPropertyMatcher().contains()).withIgnoreCase();

        Example<Serie> example = Example.of(serie, matcher);

        return this.serieRepository.findAll(example, pageable);
    }
    public Filtro getFiltro(){
        Filtro filtro = new Filtro();
        filtro.setTerritorios(new ArrayList<>());
        filtro.setFuentes(new ArrayList<>());
        filtro.setPeriodos(new ArrayList<>());

        mongoTemplate.getCollection("serie").distinct("territorio", String.class).forEach(filtro.getTerritorios()::add);
        mongoTemplate.getCollection("serie").distinct("siglasFuente", String.class).forEach(filtro.getFuentes()::add);
        mongoTemplate.getCollection("serie").distinct("periodicidad", String.class).forEach(filtro.getPeriodos()::add);


        return filtro;
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
                this.serieRepository.save(serie);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return "Se han insertado "+ i +" serie/s";
    }
}
