package com.bloodbank.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum BloodGroup {
    O_NEG("O-", false, false, false),
    O_POS("O+", false, false, true),
    A_NEG("A-", true, false, false),
    A_POS("A+", true, false, true),
    B_NEG("B-", false, true, false),
    B_POS("B+", false, true, true),
    AB_NEG("AB-", true, true, false),
    AB_POS("AB+", true, true, true);

    private final String label;
    private final boolean antigenA;
    private final boolean antigenB;
    private final boolean rhPositive;

    BloodGroup(String label, boolean antigenA, boolean antigenB, boolean rhPositive) {
        this.label = label;
        this.antigenA = antigenA;
        this.antigenB = antigenB;
        this.rhPositive = rhPositive;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    public boolean canDonateTo(BloodGroup recipient) {
        return (!antigenA || recipient.antigenA)
                && (!antigenB || recipient.antigenB)
                && (!rhPositive || recipient.rhPositive);
    }

    public Set<BloodGroup> compatibleDonors() {
        return Arrays.stream(values())
                .filter(donor -> donor.canDonateTo(this))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(BloodGroup.class)));
    }

    public Set<BloodGroup> compatibleRecipients() {
        return Arrays.stream(values())
                .filter(this::canDonateTo)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(BloodGroup.class)));
    }

    @JsonCreator
    public static BloodGroup fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Blood group is required");
        }
        String normalized = value.strip().toUpperCase(Locale.ROOT).replace(" ", "");
        boolean hasSign = normalized.endsWith("+") || normalized.endsWith("-") || normalized.contains("_");
        if (!hasSign && value.endsWith(" ")) {
            normalized = normalized + "+";   // "A+" in a URL arrives as "A "
        }
        for (BloodGroup group : values()) {
            if (group.label.equals(normalized) || group.name().equals(normalized)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Unknown blood group: " + value);
    }
}