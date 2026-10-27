package com.bloodbank.api;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Donor;
import com.bloodbank.matching.MatchingService;
import com.bloodbank.matching.RecipientMatch;
import com.bloodbank.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorApiController {

    private final DonorService donorService;
    private final MatchingService matchingService;

    public DonorApiController(DonorService donorService, MatchingService matchingService) {
        this.donorService = donorService;
        this.matchingService = matchingService;
    }

    @GetMapping
    public List<Donor> list(@RequestParam(required = false) BloodGroup bloodGroup) {
        return donorService.search(bloodGroup);
    }

    @GetMapping("/{id}")
    public Donor get(@PathVariable Long id) {
        return donorService.get(id);
    }

    @PostMapping
    public ResponseEntity<Donor> create(@Valid @RequestBody Donor donor, UriComponentsBuilder uri) {
        Donor saved = donorService.create(donor);
        return ResponseEntity.created(uri.path("/api/donors/{id}").buildAndExpand(saved.getId()).toUri()).body(saved);
    }

    @PutMapping("/{id}")
    public Donor update(@PathVariable Long id, @Valid @RequestBody Donor donor) {
        return donorService.update(id, donor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/matches")
    public List<RecipientMatch> matches(@PathVariable Long id, @RequestParam(required = false) Double radiusKm) {
        return matchingService.findRecipientsFor(donorService.get(id), radiusKm);
    }
}