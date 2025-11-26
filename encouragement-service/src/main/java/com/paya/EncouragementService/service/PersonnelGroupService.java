package com.paya.EncouragementService.service;

import com.netflix.config.validation.ValidationException;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.PersonnelGroupSpecification;
import com.paya.EncouragementService.dto.*;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import com.paya.EncouragementService.entity.*;
import com.paya.EncouragementService.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PersonnelGroupService {

    private final PersonnelGroupRepository personnelGroupRepository;
    private final PersonnelService personnelService;
    private final RegistrarPowerLimitsRepository powerLimitsRepository;

    public PersonnelGroupService(PersonnelGroupRepository personnelGroupRepository, PersonnelService personnelService, RegistrarPowerLimitsRepository powerLimitsRepository) {
        this.personnelGroupRepository = personnelGroupRepository;
        this.personnelService = personnelService;
        this.powerLimitsRepository = powerLimitsRepository;
    }

    public List<PersonnelGroupDTO> getResults(String personnelGroupName, Boolean active, String lastName, String orgId) throws ExecutionException, InterruptedException {
        List<String> filterOrgIdList= null;
        if (orgId != null) {
            filterOrgIdList = new ArrayList<>();
            filterOrgIdList.add(orgId);
        }
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, PersonnelGroupDTO.Fields.personnelGroupUpdatedAt));
        PersonnelGroupDTO dto = PersonnelGroupDTO.builder().personnelGroupName(personnelGroupName).personnelGroupActive(active).personnelGroupOrgIdList(filterOrgIdList).build();
        if (lastName != null) {
            List<? extends BasePersonnelDTO> list = personnelService.searchWithPersonnelDTO(PersonnelFilterDTOV2.builder().personnelLastName(lastName).type("basePersonnel").build());
            if (list != null && list.size() > 0) {
                List<String> orgIdList = list.stream().map(BasePersonnelDTO::getPersonnelOrganizationID).collect(Collectors.toList());
                dto.setPersonnelGroupOrgIdList(orgIdList);
            } else return new ArrayList<>();
        }
        Specification<PersonnelGroup> groupSpecification = PersonnelGroupSpecification.findBySpecification(dto);
        Page<PersonnelGroup> page = personnelGroupRepository.findAll(groupSpecification, pageRequest);
        return page.toList().stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    private PersonnelGroupDTO convertEntityToDTO(PersonnelGroup personnelGroup) {
        try {
            PersonnelGroupDTO personnelGroupDTO = PersonnelGroupDTO.builder().personnelGroupId(personnelGroup.getPersonnelGroupId())
                    .personnelGroupName(personnelGroup.getPersonnelGroupName())
                    .personnelGroupActive(personnelGroup.isActive())
                    .build();
            if (personnelGroup.getPersonnelGroupOrgIdList() != null && !personnelGroup.getPersonnelGroupOrgIdList().isEmpty())
                personnelGroupDTO.setPersonnelGroupList(personnelService.findPageByOrgId(personnelGroup.getPersonnelGroupOrgIdList()).getPersonnelDTOList());
            return personnelGroupDTO;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PersonnelGroupDTO processPersonnelGroupRequest(PersonnelGroupDTO requestDTO) throws GeneralException {
        PersonnelGroup personnelGroup;
        if (requestDTO.getPersonnelGroupId() == null) {
            if (requestDTO.getPersonnelGroupName() != null)
                personnelGroupRepository.findByPersonnelGroupName(requestDTO.getPersonnelGroupName()).ifPresent(personnelGroup1 -> {
                    throw new ValidationException("گروهی با این نام قبلا ایجاد شده است.");
                });
            else
                throw new ValidationException("نام نمی تواند خالی باشد.");
            personnelGroup = new PersonnelGroup();
            personnelGroup.setPersonnelGroupName(requestDTO.getPersonnelGroupName());
            personnelGroup.setPersonnelGroupOrgIdList(requestDTO.getPersonnelGroupOrgIdList());
        } else {
            personnelGroup = personnelGroupRepository.findByPersonnelGroupId(requestDTO.getPersonnelGroupId()).orElseThrow(() -> new GeneralException("این گروه افراد یافت نشد."));
            if (requestDTO.getPersonnelGroupName() != null)
                personnelGroup.setPersonnelGroupName(requestDTO.getPersonnelGroupName());
            if (requestDTO.getPersonnelGroupActive() != null) {
                if (!requestDTO.getPersonnelGroupActive() && !requestDTO.getPersonnelGroupActive().equals(personnelGroup.isActive()))
                    validate(personnelGroup);
                else
                    personnelGroup.setActive(requestDTO.getPersonnelGroupActive());
            }
            if (requestDTO.getPersonnelGroupOrgIdList() != null && !requestDTO.getPersonnelGroupOrgIdList().isEmpty()) {
                List<String> list = personnelGroup.getPersonnelGroupOrgIdList();
                list.addAll(requestDTO.getPersonnelGroupOrgIdList());
                List<String> list1 = list.stream().distinct().collect(Collectors.toList());
                personnelGroup.setPersonnelGroupOrgIdList(list1);
            }
            if (requestDTO.getPersonnelGroupOrgIdListToDelete() != null && !requestDTO.getPersonnelGroupOrgIdListToDelete().isEmpty())
                personnelGroup.getPersonnelGroupOrgIdList().removeAll(requestDTO.getPersonnelGroupOrgIdListToDelete());
        }
        personnelGroupRepository.save(personnelGroup);
        return this.convertEntityToDTO(personnelGroup);
    }

    private void validate(PersonnelGroup personnelGroup) throws GeneralException {
        Boolean limitExists= powerLimitsRepository.existsRegistrarPowerLimitsByPersonnelGroup(personnelGroup);
        if (limitExists)
            throw new GeneralException("حدود اختیاری با این گروه افراد تعریف شده است.");
    }

    public void deletePersonnelGroup(UUID id) throws GeneralException {
        PersonnelGroup personnelGroup = personnelGroupRepository.findByPersonnelGroupId(id).orElseThrow(() -> new GeneralException("این گروه افراد یافت نشد."));
        validate(personnelGroup);
        personnelGroupRepository.delete(personnelGroup);
    }

    public PersonnelGroup getReferenceById(UUID personnelGroupId) {
        return personnelGroupRepository.getReferenceById(personnelGroupId);
    }

    public List<PersonnelGroup> getPersonnelGroupByOrgId(String orgId) {
        return personnelGroupRepository.findByPersonnelGroupOrgIdList(Stream.of(orgId).toList());
    }
}
