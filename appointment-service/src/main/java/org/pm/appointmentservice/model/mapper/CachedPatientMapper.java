package org.pm.appointmentservice.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.pm.appointmentservice.model.entity.CachedPatient;
import patient.event.PatientEvent;

@Mapper(componentModel = "spring")
public interface CachedPatientMapper {
    @Mapping(source = "patientId", target = "id")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    CachedPatient toCachedpatient(PatientEvent patientEvent);
}
