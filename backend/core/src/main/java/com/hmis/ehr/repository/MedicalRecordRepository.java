package com.hmis.ehr.repository;

import com.hmis.ehr.model.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(UUID patientId);

    Page<MedicalRecord> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<MedicalRecord> findByTenantIdAndRecordNumber(UUID tenantId, String recordNumber);

    @Query("""
        SELECT mr FROM MedicalRecord mr
        WHERE mr.tenantId = :tenantId
          AND mr.visitDate BETWEEN :from AND :to
        ORDER BY mr.visitDate DESC
        """)
    List<MedicalRecord> findByTenantIdAndVisitDateBetween(
            @Param("tenantId") UUID tenantId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
        SELECT mr FROM MedicalRecord mr
        WHERE mr.tenantId = :tenantId
          AND mr.doctorId = :doctorId
          AND mr.status = 'ACTIVE'
        ORDER BY mr.visitDate DESC
        """)
    List<MedicalRecord> findActiveByDoctor(@Param("tenantId") UUID tenantId,
                                            @Param("doctorId") UUID doctorId);

    long countByTenantIdAndStatus(UUID tenantId, String status);

    boolean existsByTenantIdAndRecordNumber(UUID tenantId, String recordNumber);
}
