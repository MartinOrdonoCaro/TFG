package tfg.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfg.backend.dto.Filtro;
import tfg.backend.domain.Serie;
import tfg.backend.service.SerieService;

import java.util.List;

@RequestMapping("/serie")
@RestController
public class SerieController {

    @Autowired
    private SerieService serieService;

    @PostMapping
    public ResponseEntity<Serie> create(@RequestBody Serie serie){
        return new ResponseEntity<>(serieService.save(serie), HttpStatus.CREATED);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importFromUrl() throws Exception {
        return new ResponseEntity<>(serieService.importFromCesta(), HttpStatus.OK);
    }

    @PostMapping("/importFromMineco")
    public ResponseEntity<String> importFromMineco() throws Exception {
        return new ResponseEntity<>(this.serieService.importFromMineco(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Serie>> findAll(Integer page, Integer pageSize, String keyword, String fuente,
                                               String periodicidad, String territorio, String origen){
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(serieService.findAll(pageable, keyword, fuente, periodicidad, territorio, origen));
    }

    @GetMapping("/{id}")
    public Serie findById(@PathVariable Long id){
        return serieService.findById(id);
    }

    @GetMapping("/find-all-by-id")
    public ResponseEntity<List<Serie>> findAllById(@RequestParam List<Long> ids){
        return ResponseEntity.ok(serieService.findAllById(ids));
    }

    @GetMapping("/filtro")
    public ResponseEntity<Filtro> getFiltro(){
        return ResponseEntity.ok(serieService.getFiltro());
    }
}
