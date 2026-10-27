package com.bloodbank.matching;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.matching")
public record MatchingProperties(
        @DefaultValue("25") double defaultRadiusKm,
        @DefaultValue("500") double maxRadiusKm,
        @DefaultValue("56") int minDaysBetweenDonations) {
}