package com.voixdesagesse.VoixDeSagesse.controller;

// import com.voixdesagesse.VoixDeSagesse.entity.Histoire;
// import com.voixdesagesse.VoixDeSagesse.service.HistoireService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/histoire")
public class HistoireController {

    // @Autowired
    // private HistoireService histoireService;

    // // Créer une histoire
    // @PostMapping
    // public ResponseEntity<Histoire> createHistoire(@RequestBody Histoire histoire) {
    //     Histoire createdHistoire = histoireService.createHistoire(histoire);
    //     return new ResponseEntity<>(createdHistoire, HttpStatus.CREATED);
    // }

    // // Récupérer une histoire par ID
    // @GetMapping("/{id}")
    // public ResponseEntity<Histoire> getHistoireById(@PathVariable String id) {
    //     Optional<Histoire> histoire = histoireService.getHistoireById(id);
    //     return histoire.map(ResponseEntity::ok)
    //                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    // }

    // // Lister toutes les histoires
    // @GetMapping
    // public List<Histoire> getAllHistoire() {
    //     return histoireService.getAllHistoire();
    // }

    // // Mettre à jour une histoire
    // @PutMapping("/{id}")
    // public ResponseEntity<Histoire> updateHistoire(@PathVariable String id, @RequestBody Histoire updatedHistoire) {
    //     Histoire histoire = histoireService.updateHistoire(id, updatedHistoire);
    //     return histoire != null ? ResponseEntity.ok(histoire) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    // }

    // // Supprimer une histoire
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteHistoire(@PathVariable String id) {
    //     histoireService.deleteHistoire(id);
    //     return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    // }

    // // Rechercher une histoire par type
    // @GetMapping("/search")
    // public List<Histoire> findHistoireByType(@RequestParam String typeHistoire) {
    //     return histoireService.findByTypeHistoire(typeHistoire);
    // }
}
