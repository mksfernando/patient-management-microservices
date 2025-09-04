package org.pm.patientservice.model.mapper;

import org.mapstruct.*;
import org.pm.patientservice.model.dto.PatientRequestDTO;
import org.pm.patientservice.model.dto.PatientResponseDTO;
import org.pm.patientservice.model.entity.Patient;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientResponseDTO toDto(Patient patient);

    Patient toEntity(PatientRequestDTO patientResponseDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "registeredDate", ignore = true)
    void updateEntity(PatientRequestDTO source, @MappingTarget Patient destination);

}
