package com.bloodbank.matching;

import java.util.List;

public record DonorMatchResult(double radiusKm, List<DonorMatch> matches, int deferredCount) {
}