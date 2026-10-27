package com.bloodbank.service;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.RequestStatus;
import com.bloodbank.repository.RecipientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecipientService {

    private final RecipientRepository recipientRepository;

    public RecipientService(RecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    @Transactional(readOnly = true)
    public List<Recipient> search(RequestStatus status, BloodGroup bloodGroup) {
        return recipientRepository.search(status, bloodGroup);
    }

    @Transactional(readOnly = true)
    public Recipient get(Long id) {
        return recipientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Blood request " + id + " was not found"));
    }

    public Recipient create(Recipient recipient) {
        recipient.setId(null);
        recipient.setCreatedAt(null);
        recipient.setStatus(RequestStatus.OPEN);
        return recipientRepository.save(recipient);
    }

    public Recipient update(Long id, Recipient changes) {
        Recipient existing = get(id);
        existing.updateFrom(changes);
        return existing;
    }

    public Recipient updateStatus(Long id, RequestStatus status) {
        Recipient existing = get(id);
        existing.setStatus(status);
        return existing;
    }

    public void delete(Long id) {
        recipientRepository.delete(get(id));
    }
}