package org.pm.patientservice.service;

import org.pm.patientservice.exception.EmailAlreadyExistsException;
import org.pm.patientservice.exception.PatientNotFoundException;
import org.pm.patientservice.grpc.BillingServiceGrpcClient;
import org.pm.patientservice.kafka.KafkaProducer;
import org.pm.patientservice.model.dto.PagedPatientResponseDTO;
import org.pm.patientservice.model.dto.PatientRequestDTO;
import org.pm.patientservice.model.dto.PatientResponseDTO;
import org.pm.patientservice.model.entity.Patient;
import org.pm.patientservice.model.mapper.PatientMapper;
import org.pm.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          PatientMapper patientMapper,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    @Cacheable(
            value = "patients",
            key = "#page + '-' + #size + '-' + #sort + '-' + #sortField",
            condition = "#searchValue == ''"
    )
    public PagedPatientResponseDTO getPatients(int page, int size, String sort, String sortField, String searchValue) {
        log.info("[REDIS]: Cache missed - fetching from DB");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                "desc".equalsIgnoreCase(sort) ?
                        Sort.by(sortField).descending() :
                        Sort.by(sortField).ascending());

        Page<Patient> patientPage;
        if (searchValue == null || searchValue.isBlank())
            patientPage = patientRepository.findAll(pageable);
        else
            patientPage = patientRepository.findByNameContainingIgnoreCase(searchValue, pageable);

        List<PatientResponseDTO> patientResponseDTOS = patientPage.getContent().stream().map(patientMapper::toDto).toList();
        return new PagedPatientResponseDTO(
                patientResponseDTOS,
                patientPage.getNumber() + 1,
                patientPage.getSize(),
                patientPage.getTotalPages(),
                (int) patientPage.getTotalElements()
        );
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(String.format("A patient with this Email already exists %s", patientRequestDTO.getEmail()));
        }
        Patient newPatient = patientRepository.save(patientMapper.toEntity(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail());
        kafkaProducer.sendEvent(newPatient);

        return patientMapper.toDto((newPatient));
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(String.format("Patient not found with id %s", id)));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException(String.format("A patient with this Email already exists %s", patientRequestDTO.getEmail()));
        }
        patientMapper.updateEntity(patientRequestDTO, patient);
        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toDto(updatedPatient);
    }

    public boolean deletePatient(UUID id) {
        if (patientRepository.findById(id).isPresent()) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
