package tfg.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        return new ResponseEntity<>(serieService.create(serie), HttpStatus.CREATED);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importFromUrl(@RequestBody String url) throws Exception {
        return new ResponseEntity<>(serieService.importFromCesta(), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5000")
    @GetMapping
    public ResponseEntity<List<Serie>> findAll(Integer page){
        Pageable pageable = PageRequest.of(page, 5);
        return ResponseEntity.ok(serieService.findAll(pageable).getContent());
    }

    @CrossOrigin(origins = "http://localhost:5000")
    @GetMapping("/{id}")
    public Serie findById(@PathVariable Long id){
        return serieService.findById(id);
    }
}
