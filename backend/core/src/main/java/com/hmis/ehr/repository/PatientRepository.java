package com.hmis.ehr.repository;

import com.hmis.ehr.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Page<Patient> findByTenantId(UUID tenantId, Pageable pageable);

    Optional<Patient> findByTenantIdAndCode(UUID tenantId, String code);

    Optional<Patient> findByTenantIdAndIdentityNumber(UUID tenantId, String identityNumber);

    @Query("""
        SELECT p FROM Patient p
        WHERE p.tenantId = :tenantId
          AND p.status = 'ACTIVE'
          AND (LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR p.phone LIKE CONCAT('%', :keyword, '%')
               OR p.code LIKE CONCAT('%', :keyword, '%')
               OR p.identityNumber LIKE CONCAT('%', :keyword, '%')
               OR p.insuranceNumber LIKE CONCAT('%', :keyword, '%'))
        ORDER BY p.fullName
        """)
    List<Patient> searchByKeyword(@Param("tenantId") UUID tenantId,
                                  @Param("keyword") String keyword);

    boolean existsByTenantIdAndCode(UUID tenantId, String code);

    long countByTenantIdAndStatus(UUID tenantId, String status);
}
