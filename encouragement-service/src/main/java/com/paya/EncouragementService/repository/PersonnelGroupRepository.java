package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.PersonnelGroup;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonnelGroupRepository extends JpaRepository<PersonnelGroup, UUID>, JpaSpecificationExecutor<PersonnelGroup> {

    Optional<PersonnelGroup> findByPersonnelGroupId(UUID uuid);
    Optional<PersonnelGroup> findByPersonnelGroupName(String name);

    @Query("select pg from PersonnelGroup pg join pg.personnelGroupOrgIdList orgId where orgId in :orgIdList")
    List<PersonnelGroup> findByPersonnelGroupOrgIdList(@Param("orgIdList") List<String> orgIdList);
}
