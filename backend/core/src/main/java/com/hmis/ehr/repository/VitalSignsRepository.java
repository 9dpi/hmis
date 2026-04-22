package com.hmis.ehr.repository;

import com.hmis.ehr.model.VitalSigns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VitalSignsRepository extends JpaRepository<VitalSigns, UUID> {

    List<VitalSigns> findByPatientIdOrderByMeasuredAtDesc(UUID patientId);

    List<VitalSigns> findByMedicalRecordIdOrderByMeasuredAtAsc(UUID medicalRecordId);

    List<VitalSigns> findByPatientIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            UUID patientId, LocalDateTime from, LocalDateTime to);

    List<VitalSigns> findByDeviceIdOrderByMeasuredAtDesc(String deviceId);
}
