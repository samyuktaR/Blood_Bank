package com.bloodbank.matching;

import com.bloodbank.domain.Donor;

public record DonorMatch(Donor donor, double distanceKm, double distanceMiles, boolean exactMatch) {
    static DonorMatch of(Donor donor, double distanceKm, boolean exactMatch) {
        return new DonorMatch(donor, GeoUtils.round2(distanceKm),
                GeoUtils.round2(GeoUtils.kmToMiles(distanceKm)), exactMatch);
    }
}