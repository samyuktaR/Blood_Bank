package com.bloodbank.domain;

import java.util.Locale;

public enum RequestStatus {
    OPEN("Open"), FULFILLED("Fulfilled"), CANCELLED("Cancelled");

    private final String label;

    RequestStatus(String label) { this.label = label; }

    public String getLabel() { return label; }

    public String getCssClass() { return "status-" + name().toLowerCase(Locale.ROOT); }
}