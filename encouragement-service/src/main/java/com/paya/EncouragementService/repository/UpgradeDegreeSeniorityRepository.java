package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.UpgradeDegreeSeniority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface UpgradeDegreeSeniorityRepository extends JpaRepository<UpgradeDegreeSeniority, UUID>, JpaSpecificationExecutor<UpgradeDegreeSeniority> {

    Optional<UpgradeDegreeSeniority> findByUpgradeDegreeSeniorityFromDegree(Integer fromDegree);

    Optional<UpgradeDegreeSeniority> findByUpgradeDegreeSeniorityFromDegreeAndUpgradeDegreeSeniorityToDegree(
            Integer fromDegree, Integer toDegree);


    @Query("select uds.upgradeDegreeSeniorityMaxAmount from UpgradeDegreeSeniority uds " +
            "where uds.upgradeDegreeSeniorityFromDegree = :fromDegree and uds.upgradeDegreeSeniorityToDegree = :toDegree " +
            "order by uds.createdAt desc limit 1 ")
    Integer getMaxAmountWithLastDegreeAndNewDegree(Integer fromDegree, Integer toDegree);

}