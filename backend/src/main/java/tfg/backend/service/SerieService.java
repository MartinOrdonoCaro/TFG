package tfg.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import tfg.backend.dto.Filtro;
import tfg.backend.domain.Serie;
import tfg.backend.repository.SerieRepository;
import tfg.backend.scrapper.Ieca;
import tfg.backend.scrapper.Mineco;

import java.io.*;
import java.util.*;

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

    public Serie save(Serie serie){
        return this.serieRepository.save(serie);
    }

    public String importFromMineco() throws IOException {
        List<Serie> series = Mineco.importSeries();
        series.forEach(this::save);

        return "Se han insertado "+series.size()+" series";
    }

    public String importFromCesta() throws Exception {
        List<Serie> series = Ieca.importFromCesta();
        series.forEach(this::save);

        return "Se han insertado "+series.size()+" series";
    }
}
