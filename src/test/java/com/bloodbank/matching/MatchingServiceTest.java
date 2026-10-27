package com.bloodbank.matching;

import com.bloodbank.domain.Donor;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.RequestStatus;
import com.bloodbank.domain.Urgency;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.RecipientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.bloodbank.TestData.*;
import static com.bloodbank.domain.BloodGroup.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MatchingServiceTest {

    @Autowired MatchingService matchingService;
    @Autowired DonorRepository donorRepository;
    @Autowired RecipientRepository recipientRepository;

    @Test
    void findsCompatibleEligibleDonorsWithinRadiusNearestFirst() {
        LocalDate today = LocalDate.now();
        Donor near = donorRepository.save(donor("Tempe O Neg", O_NEG, 33.4148, -111.9093));         // ~3 km
        Donor exact = donorRepository.save(donor("Mesa A Pos", A_POS, 33.4152, -111.8315));         // ~10 km
        donorRepository.save(donor("Chandler B Pos", B_POS, 33.3062, -111.8413));                   // incompatible
        donorRepository.save(donor("Tucson A Pos", A_POS, 32.2226, -110.9747));                     // ~161 km
        Donor optedOut = donor("Phoenix O Pos", O_POS, 33.4484, -112.0740);
        optedOut.setAvailable(false);
        donorRepository.save(optedOut);
        donorRepository.save(donor("Mesa O Neg", O_NEG, 33.4373, -111.7660, today.minusDays(10)));  // waiting period
        Donor day56 = donorRepository.save(donor("Phoenix A Neg", A_NEG, 33.4484, -112.0740, today.minusDays(56))); // ~12.7 km

        Recipient patient = recipientRepository.save(recipient("Patient", A_POS, Urgency.HIGH, TEMPE_LAT, TEMPE_LON));
        DonorMatchResult result = matchingService.findDonorsFor(patient, 25.0);

        assertThat(result.matches()).extracting(DonorMatch::donor).containsExactly(near, exact, day56);
        assertThat(result.matches()).extracting(DonorMatch::exactMatch).containsExactly(false, true, false);
        assertThat(result.deferredCount()).isEqualTo(1);
    }

    @Test
    void openRequestsForDonorAreMostUrgentFirst() {
        Donor donor = donorRepository.save(donor("O Neg", O_NEG, TEMPE_LAT, TEMPE_LON));
        Recipient nearLow = recipientRepository.save(recipient("Near low", AB_POS, Urgency.LOW, 33.42, -111.93));
        Recipient farCritical = recipientRepository.save(recipient("Far critical", B_NEG, Urgency.CRITICAL, 33.465, -112.056));
        Recipient done = recipient("Done", O_NEG, Urgency.CRITICAL, 33.42, -111.93);
        done.setStatus(RequestStatus.FULFILLED);
        recipientRepository.save(done);

        assertThat(matchingService.findRecipientsFor(donor, 25.0))
                .extracting(RecipientMatch::recipient).containsExactly(farCritical, nearLow);
    }

    @Test
    void radiusDefaultsAndCap() {
        assertThat(matchingService.resolveRadius(null)).isEqualTo(25.0);
        assertThat(matchingService.resolveRadius(-3.0)).isEqualTo(25.0);
        assertThat(matchingService.resolveRadius(10_000.0)).isEqualTo(500.0);
    }
}