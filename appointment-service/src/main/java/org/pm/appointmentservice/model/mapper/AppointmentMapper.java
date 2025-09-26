package org.pm.appointmentservice.model.mapper;

import org.mapstruct.Mapper;
import org.pm.appointmentservice.model.dto.AppointmentRequestDto;
import org.pm.appointmentservice.model.dto.AppointmentResponseDto;
import org.pm.appointmentservice.model.entity.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentResponseDto toDto(Appointment entity);

    Appointment toEntity(AppointmentRequestDto dto);
}
