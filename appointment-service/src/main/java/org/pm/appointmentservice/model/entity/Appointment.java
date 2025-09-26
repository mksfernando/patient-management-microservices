package org.pm.appointmentservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Patient id is required")
    @Column(nullable = false)
    private UUID patientId;

    @NotNull(message = "StartTime is required")
    @Column(nullable = false)
    @Future(message = "StartTime must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "EndTime is required")
    @Column(nullable = false)
    @Future(message = "EndTime must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "Reason is required")
    @Size(max = 255, message = "Reason must be 255 characters or less")
    @Column(nullable = false, length = 255)
    private String reason;

    @Version
    @Column(nullable = false)
    private Long version;

    public Appointment() {
    }

    public Appointment(UUID patientId, LocalDateTime startTime, LocalDateTime endTime, String reason) {
        this.patientId = patientId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
