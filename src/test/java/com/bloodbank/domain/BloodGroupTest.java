package com.bloodbank.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bloodbank.domain.BloodGroup.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BloodGroupTest {

    @ParameterizedTest(name = "{0} receives from {1}")
    @CsvSource({
            "O-,  O-",
            "O+,  O- O+",
            "A-,  O- A-",
            "A+,  O- O+ A- A+",
            "B-,  O- B-",
            "B+,  O- O+ B- B+",
            "AB-, O- A- B- AB-",
            "AB+, O- O+ A- A+ B- B+ AB- AB+"
    })
    void compatibleDonorsMatchChart(String recipient, String donors) {
        Set<BloodGroup> expected = Arrays.stream(donors.trim().split("\\s+"))
                .map(BloodGroup::fromString)
                .collect(Collectors.toSet());
        assertThat(BloodGroup.fromString(recipient).compatibleDonors()).isEqualTo(expected);
    }

    @Test
    void universalDonorAndRecipient() {
        assertThat(O_NEG.compatibleRecipients()).containsExactlyInAnyOrder(BloodGroup.values());
        assertThat(AB_POS.compatibleDonors()).containsExactlyInAnyOrder(BloodGroup.values());
    }

    @Test
    void parsesLabelsAndNames() {
        assertThat(BloodGroup.fromString("AB+")).isEqualTo(AB_POS);
        assertThat(BloodGroup.fromString("ab_neg")).isEqualTo(AB_NEG);
        assertThat(BloodGroup.fromString(" o- ")).isEqualTo(O_NEG);
        assertThat(BloodGroup.fromString("A ")).isEqualTo(A_POS);
        assertThatThrownBy(() -> BloodGroup.fromString("C+")).isInstanceOf(IllegalArgumentException.class);
    }
}