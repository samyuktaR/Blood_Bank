package com.bloodbank.matching;

import com.bloodbank.domain.Donor;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.RequestStatus;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.RecipientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MatchingService {

    private final DonorRepository donorRepository;
    private final RecipientRepository recipientRepository;
    private final MatchingProperties properties;
    private final Clock clock;

    public MatchingService(DonorRepository donorRepository, RecipientRepository recipientRepository,
                           MatchingProperties properties, Clock clock) {
        this.donorRepository = donorRepository;
        this.recipientRepository = recipientRepository;
        this.properties = properties;
        this.clock = clock;
    }

    public double resolveRadius(Double requestedKm) {
        if (requestedKm == null || requestedKm.isNaN() || requestedKm <= 0) {
            return properties.defaultRadiusKm();
        }
        return Math.min(requestedKm, properties.maxRadiusKm());
    }

    public DonorMatchResult findDonorsFor(Recipient recipient, Double requestedRadiusKm) {
        double radiusKm = resolveRadius(requestedRadiusKm);
        GeoUtils.BoundingBox box = GeoUtils.boundingBox(recipient.getLatitude(), recipient.getLongitude(), radiusKm);
        LocalDate today = LocalDate.now(clock);

        List<Donor> candidates = donorRepository.findMatchCandidates(
                recipient.getBloodGroup().compatibleDonors(),
                box.minLat(), box.maxLat(), box.minLon(), box.maxLon());

        List<DonorMatch> matches = new ArrayList<>();
        int deferred = 0;
        for (Donor donor : candidates) {
            double distance = GeoUtils.distanceKm(recipient.getLatitude(), recipient.getLongitude(),
                    donor.getLatitude(), donor.getLongitude());
            if (distance > radiusKm) {
                continue;
            }
            if (!donor.isEligibleOn(today, properties.minDaysBetweenDonations())) {
                deferred++;
                continue;
            }
            matches.add(DonorMatch.of(donor, distance, donor.getBloodGroup() == recipient.getBloodGroup()));
        }

        matches.sort(Comparator.comparingDouble(DonorMatch::distanceKm)
                .thenComparing(DonorMatch::exactMatch, Comparator.reverseOrder()));
        return new DonorMatchResult(radiusKm, List.copyOf(matches), deferred);
    }

    public List<RecipientMatch> findRecipientsFor(Donor donor, Double requestedRadiusKm) {
        double radiusKm = resolveRadius(requestedRadiusKm);
        GeoUtils.BoundingBox box = GeoUtils.boundingBox(donor.getLatitude(), donor.getLongitude(), radiusKm);

        return recipientRepository.findMatchCandidates(
                        RequestStatus.OPEN,
                        donor.getBloodGroup().compatibleRecipients(),
                        box.minLat(), box.maxLat(), box.minLon(), box.maxLon())
                .stream()
                .map(r -> RecipientMatch.of(r,
                        GeoUtils.distanceKm(donor.getLatitude(), donor.getLongitude(), r.getLatitude(), r.getLongitude()),
                        r.getBloodGroup() == donor.getBloodGroup()))
                .filter(m -> m.distanceKm() <= radiusKm)
                .sorted(Comparator.comparing((RecipientMatch m) -> m.recipient().getUrgency(), Comparator.reverseOrder())
                        .thenComparingDouble(RecipientMatch::distanceKm))
                .toList();
    }

    public boolean isEligibleToday(Donor donor) {
        return donor.isEligibleOn(LocalDate.now(clock), properties.minDaysBetweenDonations());
    }

    public LocalDate nextEligibleDate(Donor donor) {
        return donor.nextEligibleDate(properties.minDaysBetweenDonations());
    }

    public MatchingProperties properties() {
        return properties;
    }
}