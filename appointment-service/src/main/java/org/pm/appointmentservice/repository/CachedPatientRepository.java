package org.pm.appointmentservice.repository;

import org.pm.appointmentservice.model.entity.CachedPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CachedPatientRepository extends JpaRepository<CachedPatient, UUID> {
}
