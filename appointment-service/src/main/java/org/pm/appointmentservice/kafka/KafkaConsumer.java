package org.pm.appointmentservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.pm.appointmentservice.model.entity.CachedPatient;
import org.pm.appointmentservice.model.mapper.CachedPatientMapper;
import org.pm.appointmentservice.repository.CachedPatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final CachedPatientRepository cachedPatientRepository;
    private final CachedPatientMapper cachedPatientMapper;

    public KafkaConsumer(CachedPatientRepository cachedPatientRepository, CachedPatientMapper cachedPatientMapper) {
        this.cachedPatientRepository = cachedPatientRepository;
        this.cachedPatientMapper = cachedPatientMapper;
    }

    @KafkaListener(topics = {"patient.created", "patient.updated"}, groupId = "appointment-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received Patient Event: {}", patientEvent);
            CachedPatient cachedpatient = cachedPatientMapper.toCachedpatient(patientEvent);
            cachedPatientRepository.save(cachedpatient);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing Patient Event: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error consuming Patient Event: {}", e.getMessage());
        }
    }
}
