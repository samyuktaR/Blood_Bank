package com.bloodbank.config;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Donor;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.Urgency;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.RecipientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final DonorRepository donorRepository;
    private final RecipientRepository recipientRepository;
    private final Clock clock;

    public DataSeeder(DonorRepository donorRepository, RecipientRepository recipientRepository, Clock clock) {
        this.donorRepository = donorRepository;
        this.recipientRepository = recipientRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (donorRepository.count() > 0 || recipientRepository.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now(clock);

        donorRepository.saveAll(List.of(
                donor("Priya Sharma", 27, BloodGroup.O_NEG, "Tempe", 33.4255, -111.9400, today.minusDays(140), "(480) 555-0101"),
                donor("Marcus Lee", 34, BloodGroup.O_POS, "Phoenix", 33.4484, -112.0740, null, "(602) 555-0102"),
                donor("Ana Gutierrez", 41, BloodGroup.A_POS, "Mesa", 33.4152, -111.8315, today.minusDays(90), "(480) 555-0103"),
                donor("David Kim", 29, BloodGroup.A_NEG, "Scottsdale", 33.4942, -111.9261, null, "(480) 555-0104"),
                donor("Fatima Noor", 23, BloodGroup.B_POS, "Chandler", 33.3062, -111.8413, null, "(480) 555-0105"),
                donor("Mei Chen", 31, BloodGroup.AB_POS, "Tempe", 33.4148, -111.9093, today.minusDays(70), "(480) 555-0106"),
                donor("Luis Ortega", 49, BloodGroup.O_NEG, "Mesa", 33.4373, -111.7660, today.minusDays(20), "(480) 555-0107"),
                donor("Grace Park", 33, BloodGroup.O_NEG, "Los Angeles", 34.0522, -118.2437, null, "(213) 555-0108")));

        recipientRepository.saveAll(List.of(
                recipient("Arjun Mehta", BloodGroup.O_NEG, 2, Urgency.CRITICAL, "Valley General Hospital", "Phoenix",
                        33.4650, -112.0560, today.plusDays(1), "Neha Mehta", "(602) 555-0201"),
                recipient("Elena Rossi", BloodGroup.A_POS, 1, Urgency.HIGH, "Tempe Community Medical Center", "Tempe",
                        33.4200, -111.9300, today.plusDays(3), "Marco Rossi", "(480) 555-0202"),
                recipient("Michael Brooks", BloodGroup.B_NEG, 3, Urgency.MEDIUM, "Westside Medical Center", "Los Angeles",
                        34.0400, -118.4400, today.plusDays(7), "Laura Brooks", "(310) 555-0203")));
    }

    private static Donor donor(String name, int age, BloodGroup group, String city, double lat, double lon,
                               LocalDate lastDonation, String phone) {
        Donor d = new Donor();
        d.setName(name);
        d.setAge(age);
        d.setBloodGroup(group);
        d.setCity(city);
        d.setLatitude(lat);
        d.setLongitude(lon);
        d.setLastDonationDate(lastDonation);
        d.setAvailable(true);
        d.setPhone(phone);
        d.setEmail(emailFor(name));
        return d;
    }

    private static Recipient recipient(String patient, BloodGroup group, int units, Urgency urgency, String hospital,
                                       String city, double lat, double lon, LocalDate neededBy,
                                       String contact, String phone) {
        Recipient r = new Recipient();
        r.setPatientName(patient);
        r.setBloodGroup(group);
        r.setUnitsRequired(units);
        r.setUrgency(urgency);
        r.setHospital(hospital);
        r.setCity(city);
        r.setLatitude(lat);
        r.setLongitude(lon);
        r.setNeededBy(neededBy);
        r.setContactName(contact);
        r.setContactPhone(phone);
        r.setContactEmail(emailFor(contact));
        return r;
    }

    private static String emailFor(String name) {
        return name.toLowerCase().replaceAll("[^a-z ]", "").trim().replace(' ', '.') + "@example.com";
    }
}