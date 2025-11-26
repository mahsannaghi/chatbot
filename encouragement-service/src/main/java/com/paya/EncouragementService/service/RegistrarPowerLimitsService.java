package com.paya.EncouragementService.service;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.dto.PowerTypeDTO;
import com.paya.EncouragementService.dto.PowerTypesDTO;
import com.paya.EncouragementService.dto.RegistrarPowerLimitsDTO;
import com.paya.EncouragementService.dto.RegistrarPowerLimitsRequestDTO;
import com.paya.EncouragementService.entity.EncouragementType;
import com.paya.EncouragementService.entity.PersonnelGroup;
import com.paya.EncouragementService.entity.RegistrarPowerLimits;
import com.paya.EncouragementService.entity.TblBase;
import com.paya.EncouragementService.repository.EncouragementTypeRepository;
import com.paya.EncouragementService.repository.RegistrarPowerLimitsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrarPowerLimitsService {
    private final RegistrarPowerLimitsRepository repository;
    private final PersonnelGroupService personnelGroupService;
    private final EncouragementTypeRepository encouragementTypeRepository;

    public List<RegistrarPowerLimitsDTO> getRegistrarPowerLimitsWithExternalData(RegistrarPowerLimitsRequestDTO requestDTO) {
        Pageable pageable = PageRequest.of(requestDTO.getPageNumber() != null ? requestDTO.getPageNumber() : 0,
                requestDTO.getPageSize() != null ? requestDTO.getPageSize() : 9000, Sort.by(TblBase.Fields.updatedAt).descending());
        String typeTitle = null;
        if (requestDTO.getTypeTitleList() != null && requestDTO.getTypeTitleList().size() > 0)
            typeTitle= requestDTO.getTypeTitleList().get(0).replace("ی", "ي").trim();
        Page<RegistrarPowerLimits> page = repository.findByFilter(
                typeTitle, requestDTO.getPersonnelGroupIdList(),
                pageable
        );
        LinkedHashMap<UUID, List<RegistrarPowerLimits>> hashMap = page.stream().collect(Collectors.groupingBy(RegistrarPowerLimits::getEncouragementTypeId, LinkedHashMap::new, Collectors.toList()));
        Map<String, RegistrarPowerLimitsDTO> groupedResults = new LinkedHashMap<>();
        for (RegistrarPowerLimits registrarPowerLimits : page) {
            UUID encouragementTypeIdFromDb = registrarPowerLimits.getEncouragementTypeId();
            PersonnelGroup personnelGroup = registrarPowerLimits.getPersonnelGroup();
            String key = (personnelGroup != null) ? personnelGroup.getPersonnelGroupName() : "UNKNOWN";

            RegistrarPowerLimitsDTO registrarPowerLimitsDTO = groupedResults.get(key);
            if (registrarPowerLimitsDTO == null) {
                registrarPowerLimitsDTO = new RegistrarPowerLimitsDTO();
                if (personnelGroup != null) {
                    registrarPowerLimitsDTO.setPersonnelGroupId(personnelGroup.getPersonnelGroupId());
                    registrarPowerLimitsDTO.setPersonnelGroupName(personnelGroup.getPersonnelGroupName());
                }
                registrarPowerLimitsDTO.setId(registrarPowerLimits.getId());
                registrarPowerLimitsDTO.setTypes(new ArrayList<>());
                groupedResults.put(key, registrarPowerLimitsDTO);
            }

            PowerTypesDTO powerTypesDTO = getPowerTypesDTO(registrarPowerLimits, encouragementTypeIdFromDb);

            registrarPowerLimitsDTO.getTypes().add(powerTypesDTO);
        }
        return new ArrayList<>(groupedResults.values());
    }

    private PowerTypesDTO getPowerTypesDTO(RegistrarPowerLimits registrarPowerLimits, UUID encouragementTypeIdFromDb) {
        String typeTitleFromDb = encouragementTypeRepository.findById(encouragementTypeIdFromDb)
                .map(EncouragementType::getEncouragementTypeTitle)
                .orElse("Unknown Type");

        PowerTypesDTO powerTypesDTO = new PowerTypesDTO();
        powerTypesDTO.setTypeId(encouragementTypeIdFromDb);
        powerTypesDTO.setTypeTitle(typeTitleFromDb);
        powerTypesDTO.setMaxAmount(registrarPowerLimits.getMaxAmount() == null ? BigDecimal.valueOf(0) : registrarPowerLimits.getMaxAmount());
        powerTypesDTO.setMaxDuration(registrarPowerLimits.getMaxDuration() == null ? 0 : registrarPowerLimits.getMaxDuration());
        powerTypesDTO.setDurationType(registrarPowerLimits.getDurationType());
        return powerTypesDTO;
    }

    @Transactional
    public void saveRegistrarPowerLimits(RegistrarPowerLimitsRequestDTO requestDTO) throws GeneralException {
        PersonnelGroup personnelGroup = null;
        if (requestDTO.getPersonnelGroupId() != null) {
            personnelGroup = personnelGroupService.getReferenceById(requestDTO.getPersonnelGroupId());
            if (personnelGroup == null)
                throw new GeneralException("گروه یافت نشد.");
        }
        if (personnelGroup != null && !personnelGroup.isActive()) {
            throw new GeneralException("گروه فعال نمی باشد.");
        }
        if (requestDTO.getTypesToAddOrUpdate() != null || requestDTO.getTypesToAddOrUpdate().size() == 0) {
            for (PowerTypeDTO type : requestDTO.getTypesToAddOrUpdate()) {
                Optional<RegistrarPowerLimits> existingLimit = repository
                        .findByPersonnelGroupAndEncouragementTypeId(
                                personnelGroup, type.getTypeId()
                        );
                if (existingLimit.isPresent()) {
                    RegistrarPowerLimits limit = existingLimit.get();
                    limit.setPersonnelGroup(personnelGroup);
                    if (type.getMaxAmount() != null)
                        limit.setMaxAmount(type.getMaxAmount());
                    if (type.getMaxDuration() != null)
                        limit.setMaxDuration(type.getMaxDuration());
                    if (type.getDurationType() != null)
                        limit.setDurationType(type.getDurationType());
                    repository.save(limit);
                } else {
                    RegistrarPowerLimits newLimit = new RegistrarPowerLimits();
                    newLimit.setPersonnelGroup(personnelGroup);
                    newLimit.setEncouragementTypeId(type.getTypeId());
                    newLimit.setMaxAmount(type.getMaxAmount());
                    newLimit.setMaxDuration(type.getMaxDuration());
                    newLimit.setDurationType(type.getDurationType());
                    repository.save(newLimit);
                }
            }
        } else
            throw new GeneralException("حداقل یک نوع و مقدار را انتخاب کنید.");

        if (requestDTO.getDeleteTheseTypes() != null) {
            for (UUID typeId : requestDTO.getDeleteTheseTypes()) {
                Optional<RegistrarPowerLimits> limitToDelete = repository
                        .findByPersonnelGroupAndEncouragementTypeId(
                                personnelGroup, typeId
                        );
                if (limitToDelete.isPresent()) {
                    repository.delete(limitToDelete.get());
                } else {
                    log.warn("Type with ID {} does not exist for this gradeId and positionId, skipping deletion.", typeId);
                }
            }
        }
    }

    public Optional<RegistrarPowerLimits> getThisPersonnelOrganizationIdGroupAndEncouragementTypeId(String orgId, UUID typeId) throws GeneralException {
        if (orgId != null && typeId != null) {
            List<PersonnelGroup> groupList = personnelGroupService.getPersonnelGroupByOrgId(orgId);
            return groupList.stream()
                    .map(personnelGroup -> personnelGroupService.getReferenceById(personnelGroup.getPersonnelGroupId()))
                    .map(group -> repository.findByPersonnelGroupAndEncouragementTypeId(group, typeId))
                    .flatMap(Optional::stream)
                    .findFirst();
        } else throw new GeneralException("پارامتر ورودی صحیح نمی باشد .");
    }
}
