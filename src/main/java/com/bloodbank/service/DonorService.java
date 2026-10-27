package com.bloodbank.service;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Donor;
import com.bloodbank.repository.DonorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DonorService {

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    @Transactional(readOnly = true)
    public List<Donor> search(BloodGroup bloodGroup) {
        return donorRepository.search(bloodGroup);
    }

    @Transactional(readOnly = true)
    public Donor get(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Donor " + id + " was not found"));
    }

    public Donor create(Donor donor) {
        donor.setId(null);
        donor.setCreatedAt(null);
        return donorRepository.save(donor);
    }

    public Donor update(Long id, Donor changes) {
        Donor existing = get(id);
        existing.updateFrom(changes);
        return existing;   // saved automatically when the transaction commits
    }

    public void delete(Long id) {
        donorRepository.delete(get(id));
    }
}