package com.bloodbank.api;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.RequestStatus;
import com.bloodbank.matching.DonorMatchResult;
import com.bloodbank.matching.MatchingService;
import com.bloodbank.service.RecipientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
public class RecipientApiController {

    private final RecipientService recipientService;
    private final MatchingService matchingService;

    public RecipientApiController(RecipientService recipientService, MatchingService matchingService) {
        this.recipientService = recipientService;
        this.matchingService = matchingService;
    }

    @GetMapping
    public List<Recipient> list(@RequestParam(required = false) RequestStatus status,
                                @RequestParam(required = false) BloodGroup bloodGroup) {
        return recipientService.search(status, bloodGroup);
    }

    @GetMapping("/{id}")
    public Recipient get(@PathVariable Long id) {
        return recipientService.get(id);
    }

    @PostMapping
    public ResponseEntity<Recipient> create(@Valid @RequestBody Recipient recipient, UriComponentsBuilder uri) {
        Recipient saved = recipientService.create(recipient);
        return ResponseEntity.created(uri.path("/api/recipients/{id}").buildAndExpand(saved.getId()).toUri()).body(saved);
    }

    @PutMapping("/{id}")
    public Recipient update(@PathVariable Long id, @Valid @RequestBody Recipient recipient) {
        return recipientService.update(id, recipient);
    }

    @PatchMapping("/{id}/status")
    public Recipient updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdate update) {
        return recipientService.updateStatus(id, update.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/matches")
    public DonorMatchResult matches(@PathVariable Long id, @RequestParam(required = false) Double radiusKm) {
        return matchingService.findDonorsFor(recipientService.get(id), radiusKm);
    }

    public record StatusUpdate(@NotNull RequestStatus status) {
    }
}