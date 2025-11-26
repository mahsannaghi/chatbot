package com.paya.EncouragementService.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.paya.EncouragementService.dto.PersonnelDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "tbl_users")
@Getter
@Setter
@NoArgsConstructor
public class TblUser {

    @Id
    @Column(name = "user_id", length = 32)
    @JsonProperty(value = "uuid")
    private String userId;

    @PrePersist
    public void generateId() {
        if (userId == null) {
            userId = UUID.randomUUID().toString();
        }
    }

    @Column(name = "username", unique = true, length = 5)
    private String username;

    @Transient
    private PersonnelDTO userInfo;
    @Transient
    private LocalDate currentDate;
    @Transient
    private Set<String> roles;

    @Column(name = "user_role")
    private String currentRole;

}
