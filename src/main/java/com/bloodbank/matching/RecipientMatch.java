package com.bloodbank.matching;

import com.bloodbank.domain.Recipient;

public record RecipientMatch(Recipient recipient, double distanceKm, double distanceMiles, boolean exactMatch) {
    static RecipientMatch of(Recipient recipient, double distanceKm, boolean exactMatch) {
        return new RecipientMatch(recipient, GeoUtils.round2(distanceKm),
                GeoUtils.round2(GeoUtils.kmToMiles(distanceKm)), exactMatch);
    }
}