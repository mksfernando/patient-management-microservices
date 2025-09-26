package org.pm.appointmentservice.service;

import org.pm.appointmentservice.model.dto.AppointmentResponseDto;
import org.pm.appointmentservice.model.entity.CachedPatient;
import org.pm.appointmentservice.model.mapper.AppointmentMapper;
import org.pm.appointmentservice.repository.AppointmentRepository;
import org.pm.appointmentservice.repository.CachedPatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CachedPatientRepository cachedPatientRepository;
    private final AppointmentMapper mapper;

    public AppointmentService(AppointmentRepository appointmentRepository, CachedPatientRepository cachedPatientRepository, AppointmentMapper mapper) {
        this.appointmentRepository = appointmentRepository;
        this.cachedPatientRepository = cachedPatientRepository;
        this.mapper = mapper;
    }

    public List<AppointmentResponseDto> getAppointmentByDateRange(LocalDateTime from, LocalDateTime to) {
        return appointmentRepository.findByStartTimeBetween(from, to).stream().map(appointment -> {
            AppointmentResponseDto dto = mapper.toDto(appointment);
            dto.setPatientName(cachedPatientRepository.findById(appointment.getPatientId())
                    .map(CachedPatient::getName)
                    .orElse("Unknown"));
            return dto;
        }).toList();
    }
}
