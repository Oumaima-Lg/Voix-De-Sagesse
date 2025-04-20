package com.voixdesagesse.VoixDeSagesse.controller;

// import com.voixdesagesse.VoixDeSagesse.entity.Sagesse;
// import com.voixdesagesse.VoixDeSagesse.service.SagesseService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/sagesse")
public class SagesseController {

    // @Autowired
    // private SagesseService sagesseService;

    // // Créer une sagesse
    // @PostMapping
    // public ResponseEntity<Sagesse> createSagesse(@RequestBody Sagesse sagesse) {
    //     Sagesse createdSagesse = sagesseService.createSagesse(sagesse);
    //     return new ResponseEntity<>(createdSagesse, HttpStatus.CREATED);
    // }

    // // Récupérer une sagesse par ID
    // @GetMapping("/{id}")
    // public ResponseEntity<Sagesse> getSagesseById(@PathVariable String id) {
    //     Optional<Sagesse> sagesse = sagesseService.getSagesseById(id);
    //     return sagesse.map(ResponseEntity::ok)
    //                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    // }

    // // Lister toutes les sagesses
    // @GetMapping
    // public List<Sagesse> getAllSagesse() {
    //     return sagesseService.getAllSagesse();
    // }

    // // Mettre à jour une sagesse
    // @PutMapping("/{id}")
    // public ResponseEntity<Sagesse> updateSagesse(@PathVariable String id, @RequestBody Sagesse updatedSagesse) {
    //     Sagesse sagesse = sagesseService.updateSagesse(id, updatedSagesse);
    //     return sagesse != null ? ResponseEntity.ok(sagesse) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    // }

    // // Supprimer une sagesse
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteSagesse(@PathVariable String id) {
    //     sagesseService.deleteSagesse(id);
    //     return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    // }

    // // Rechercher une sagesse par type
    // @GetMapping("/search")
    // public List<Sagesse> findSagesseByType(@RequestParam String typeSagesse) {
    //     return sagesseService.findByTypeSagesse(typeSagesse);
    // }
}
