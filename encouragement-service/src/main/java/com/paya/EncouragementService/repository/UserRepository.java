package com.paya.EncouragementService.repository;

import com.paya.EncouragementService.entity.TblUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<TblUser, String> {
    TblUser findByUsername(String username);

    boolean existsByUserId(String userId);
}
