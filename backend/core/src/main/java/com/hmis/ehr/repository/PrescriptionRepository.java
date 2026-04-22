package com.hmis.ehr.repository;

import com.hmis.ehr.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByMedicalRecordIdOrderByCreatedAtAsc(UUID medicalRecordId);

    List<Prescription> findByTenantIdAndStatus(UUID tenantId, String status);

    void deleteByMedicalRecordId(UUID medicalRecordId);
}
