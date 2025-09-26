package org.pm.appointmentservice.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentRequestDto {

    @NotNull(message = "Patient id is required")
    private UUID patientId;

    @NotNull(message = "StartTime is required")
    @Future(message = "StartTime must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "EndTime is required")
    @Future(message = "EndTime must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "Reason is required")
    @Size(max = 255, message = "Reason must be 255 characters or less")
    private String reason;

    // Optional, if not sent, default to 0
    private Long version = 0L;

    public AppointmentRequestDto() {
    }

    public AppointmentRequestDto(UUID patientId, LocalDateTime startTime, LocalDateTime endTime, String reason) {
        this.patientId = patientId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
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
