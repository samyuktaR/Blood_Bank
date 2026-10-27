package com.bloodbank.matching;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GeoUtilsTest {

    @Test
    void knownDistances() {
        assertThat(GeoUtils.distanceKm(33.4255, -111.94, 33.4484, -112.074)).isCloseTo(12.7, within(0.3));   // Tempe to Phoenix
        assertThat(GeoUtils.distanceKm(33.4484, -112.074, 34.0522, -118.2437)).isCloseTo(574, within(3.0)); // Phoenix to LA
        assertThat(GeoUtils.distanceKm(33.4, -111.9, 33.4, -111.9)).isZero();
    }

    @Test
    void boundingBoxNearPoleCoversAllLongitudes() {
        GeoUtils.BoundingBox box = GeoUtils.boundingBox(89.9, 10, 50);
        assertThat(box.minLon()).isEqualTo(-180);
        assertThat(box.maxLon()).isEqualTo(180);
    }
}