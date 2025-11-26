package com.paya.EncouragementService.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tbl_personnel_group")
public class PersonnelGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "personnel_group_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID personnelGroupId;

    @Column(name = "personnel_group_name")
    private String personnelGroupName;

    @ElementCollection
    private List<String> personnelGroupOrgIdList;

    @OneToMany(mappedBy = "personnelGroup")
    private List<RegistrarPowerLimits> registrarPowerLimits;

    @Column(name = "personnel_group_is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "personnel_group_created_at")
    @CreationTimestamp
    private LocalDateTime personnelGroupCreatedAt;

    @Column(name = "personnel_group_updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime personnelGroupUpdatedAt;
}
