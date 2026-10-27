package com.bloodbank.domain;

import java.util.Locale;

public enum Urgency {
    LOW("Low"), MEDIUM("Medium"), HIGH("High"), CRITICAL("Critical");

    private final String label;

    Urgency(String label) { this.label = label; }

    public String getLabel() { return label; }

    public String getCssClass() { return "urgency-" + name().toLowerCase(Locale.ROOT); }
}