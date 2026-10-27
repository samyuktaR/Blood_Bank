package com.bloodbank.repository;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Recipient;
import com.bloodbank.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    @Query("""
            select r from Recipient r
            where (:status is null or r.status = :status)
              and (:bloodGroup is null or r.bloodGroup = :bloodGroup)
            order by r.createdAt desc
            """)
    List<Recipient> search(@Param("status") RequestStatus status,
                           @Param("bloodGroup") BloodGroup bloodGroup);

    List<Recipient> findTop5ByStatusOrderByCreatedAtDesc(RequestStatus status);

    long countByStatus(RequestStatus status);

    @Query("""
            select r from Recipient r
            where r.status = :status
              and r.bloodGroup in :groups
              and r.latitude between :minLat and :maxLat
              and r.longitude between :minLon and :maxLon
            """)
    List<Recipient> findMatchCandidates(@Param("status") RequestStatus status,
                                        @Param("groups") Collection<BloodGroup> groups,
                                        @Param("minLat") double minLat,
                                        @Param("maxLat") double maxLat,
                                        @Param("minLon") double minLon,
                                        @Param("maxLon") double maxLon);
}