package tfg.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import tfg.backend.dto.Filtro;
import tfg.backend.domain.Serie;
import tfg.backend.exception.DuplicateSerieException;
import tfg.backend.exception.SerieNotFoundException;
import tfg.backend.repository.SerieRepository;
import tfg.backend.scrapper.Ieca;
import tfg.backend.scrapper.Mineco;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public Page<Serie> findAll(Pageable pageable, String keyword, String fuente, String periodicidad, String territorio, String origen){
        Serie serie = new Serie();
        serie.setDescripcion(keyword.isEmpty() ? null : keyword);
        serie.setSiglasFuente(fuente.isEmpty() ? null : fuente);
        serie.setPeriodicidad(periodicidad.isEmpty() ? null : periodicidad);
        serie.setTerritorio(territorio.isEmpty() ? null : territorio);
        serie.setOrigen(origen.isEmpty() ? null : origen);

        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("descripcion", new ExampleMatcher.GenericPropertyMatcher().contains()).withIgnoreCase();
        Example<Serie> example = Example.of(serie, matcher);

        return this.serieRepository.findAll(example, pageable);
    }
    public Filtro getFiltro(){
        Filtro filtro = new Filtro();
        filtro.setTerritorios(new ArrayList<>());
        filtro.setFuentes(new ArrayList<>());
        filtro.setPeriodos(new ArrayList<>());
        filtro.setOrigenes(Arrays.asList("IECA", "MINECO"));

        mongoTemplate.getCollection("series").distinct("territorio", String.class).forEach(filtro.getTerritorios()::add);
        mongoTemplate.getCollection("series").distinct("siglasFuente", String.class).forEach(filtro.getFuentes()::add);
        mongoTemplate.getCollection("series").distinct("periodicidad", String.class).forEach(filtro.getPeriodos()::add);


        return filtro;
    }

    public List<Serie> findAllById(List<Long> id){
        List<Serie> series = this.serieRepository.findByIdIn(id);

        if(series.size() != id.size()) {
            List<Long> notFound = new ArrayList<>(id);
            notFound.removeAll(series.stream().map(
                    serie -> Long.parseLong(serie.getCodigo())).collect(Collectors.toList()));

            throw new SerieNotFoundException("serie.not.found", new String[] {
                    notFound.stream().map(String::valueOf).collect(Collectors.toList()).toString() });
        }
        return series;

    }

    public Serie findById(Long id) {
        Optional<Serie> serieOptional = this.serieRepository.findById(id);
        if (serieOptional.isPresent()) {
            return serieOptional.get();
        } else {
            throw new SerieNotFoundException("serie.not.found", new String[] { id.toString() });
        }
    }

    public Serie save(Serie serie){
        try {
            return this.serieRepository.save(serie);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateSerieException("serie.duplicate.exception", new String[] {serie.getCodigo()});
        }
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
