package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.TblQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QueueRepository extends JpaRepository<TblQueue, String> {


}
