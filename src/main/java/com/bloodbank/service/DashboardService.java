package com.bloodbank.service;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.RequestStatus;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.RecipientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DonorRepository donorRepository;
    private final RecipientRepository recipientRepository;

    public DashboardService(DonorRepository donorRepository, RecipientRepository recipientRepository) {
        this.donorRepository = donorRepository;
        this.recipientRepository = recipientRepository;
    }

    public Stats stats() {
        Map<BloodGroup, Long> byGroup = new EnumMap<>(BloodGroup.class);
        for (BloodGroup g : BloodGroup.values()) {
            byGroup.put(g, 0L);
        }
        donorRepository.countAvailableByBloodGroup()
                .forEach(row -> byGroup.put(row.getBloodGroup(), row.getTotal()));

        return new Stats(donorRepository.count(), donorRepository.countByAvailableTrue(),
                recipientRepository.countByStatus(RequestStatus.OPEN), byGroup,
                recipientRepository.findTop5ByStatusOrderByCreatedAtDesc(RequestStatus.OPEN));
    }

    public record Stats(long donors, long availableDonors, long openRequests,
                        Map<BloodGroup, Long> availableByGroup, List<Recipient> recentOpenRequests) {
    }
}