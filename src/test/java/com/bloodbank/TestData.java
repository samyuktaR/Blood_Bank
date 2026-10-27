package com.bloodbank;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Donor;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.Urgency;

import java.time.LocalDate;

public final class TestData {

    public static final double TEMPE_LAT = 33.4255, TEMPE_LON = -111.9400;

    private TestData() { }

    public static Donor donor(String name, BloodGroup group, double lat, double lon) {
        Donor d = new Donor();
        d.setName(name);
        d.setAge(30);
        d.setBloodGroup(group);
        d.setPhone("(480) 555-0100");
        d.setEmail(name.toLowerCase().replace(' ', '.') + "@example.com");
        d.setCity("Test City");
        d.setLatitude(lat);
        d.setLongitude(lon);
        d.setAvailable(true);
        return d;
    }

    public static Donor donor(String name, BloodGroup group, double lat, double lon, LocalDate lastDonation) {
        Donor d = donor(name, group, lat, lon);
        d.setLastDonationDate(lastDonation);
        return d;
    }

    public static Recipient recipient(String patient, BloodGroup group, Urgency urgency, double lat, double lon) {
        Recipient r = new Recipient();
        r.setPatientName(patient);
        r.setBloodGroup(group);
        r.setUnitsRequired(2);
        r.setUrgency(urgency);
        r.setHospital(patient + " Hospital");
        r.setContactName("Contact");
        r.setContactPhone("(602) 555-0100");
        r.setContactEmail("contact@example.com");
        r.setCity("Test City");
        r.setLatitude(lat);
        r.setLongitude(lon);
        return r;
    }
}