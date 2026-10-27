package com.bloodbank.repository;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.domain.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    @Query("""
            select d from Donor d
            where (:bloodGroup is null or d.bloodGroup = :bloodGroup)
            order by d.createdAt desc
            """)
    List<Donor> search(@Param("bloodGroup") BloodGroup bloodGroup);

    long countByAvailableTrue();

    @Query("""
            select d from Donor d
            where d.available = true
              and d.bloodGroup in :groups
              and d.latitude between :minLat and :maxLat
              and d.longitude between :minLon and :maxLon
            """)
    List<Donor> findMatchCandidates(@Param("groups") Collection<BloodGroup> groups,
                                    @Param("minLat") double minLat,
                                    @Param("maxLat") double maxLat,
                                    @Param("minLon") double minLon,
                                    @Param("maxLon") double maxLon);

    @Query("""
            select d.bloodGroup as bloodGroup, count(d) as total
            from Donor d
            where d.available = true
            group by d.bloodGroup
            """)
    List<GroupCount> countAvailableByBloodGroup();

    interface GroupCount {
        BloodGroup getBloodGroup();
        Long getTotal();
    }
}