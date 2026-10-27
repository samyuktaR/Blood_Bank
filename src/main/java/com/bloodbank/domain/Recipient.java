package com.bloodbank.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "recipients",
        indexes = @Index(name = "idx_recipients_matching", columnList = "blood_group, status, latitude, longitude"))
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Patient name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String patientName;

    @NotNull(message = "Blood group is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false, length = 8)
    private BloodGroup bloodGroup;

    @NotNull(message = "Units required is required")
    @Min(value = 1, message = "At least 1 unit")
    @Max(value = 20, message = "At most 20 units per request")
    @Column(nullable = false)
    private Integer unitsRequired;

    @NotNull(message = "Urgency is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Urgency urgency = Urgency.MEDIUM;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate neededBy;

    @NotBlank(message = "Hospital is required")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String hospital;

    @NotBlank(message = "Contact name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String contactName;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = ValidationPatterns.PHONE, message = "Enter a valid phone number")
    @Column(nullable = false, length = 20)
    private String contactPhone;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String contactEmail;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String city;

    @NotNull(message = "Latitude is required: pick the hospital on the map")
    @DecimalMin("-90.0") @DecimalMax("90.0")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude is required: pick the hospital on the map")
    @DecimalMin("-180.0") @DecimalMax("180.0")
    @Column(nullable = false)
    private Double longitude;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RequestStatus status = RequestStatus.OPEN;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = RequestStatus.OPEN;
        if (urgency == null) urgency = Urgency.MEDIUM;
    }

    public Set<BloodGroup> compatibleDonorGroups() {
        return bloodGroup == null ? Set.of() : bloodGroup.compatibleDonors();
    }

    public void updateFrom(Recipient other) {
        this.patientName = other.patientName;
        this.bloodGroup = other.bloodGroup;
        this.unitsRequired = other.unitsRequired;
        this.urgency = other.urgency;
        this.neededBy = other.neededBy;
        this.hospital = other.hospital;
        this.contactName = other.contactName;
        this.contactPhone = other.contactPhone;
        this.contactEmail = other.contactEmail;
        this.city = other.city;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Integer getUnitsRequired() {
        return unitsRequired;
    }

    public void setUnitsRequired(Integer unitsRequired) {
        this.unitsRequired = unitsRequired;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public LocalDate getNeededBy() {
        return neededBy;
    }

    public void setNeededBy(LocalDate neededBy) {
        this.neededBy = neededBy;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }


}