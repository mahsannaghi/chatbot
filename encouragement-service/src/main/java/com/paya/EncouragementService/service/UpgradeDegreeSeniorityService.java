package com.paya.EncouragementService.service;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.paya.EncouragementService.Specification.UpgradeDegreeSenioritySpecification;
import com.paya.EncouragementService.dto.UpgradeDegreeSeniorityDTO;
import com.paya.EncouragementService.entity.TblBase;
import com.paya.EncouragementService.entity.UpgradeDegreeSeniority;
import com.paya.EncouragementService.repository.UpgradeDegreeSeniorityRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UpgradeDegreeSeniorityService {

    private final UpgradeDegreeSeniorityRepository repository;

    public UpgradeDegreeSeniorityService(UpgradeDegreeSeniorityRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public UpgradeDegreeSeniority createUpgradeDegreeSeniority(UpgradeDegreeSeniorityDTO dto) throws GeneralException {

        if (dto.getUpgradeDegreeSeniorityFromDegree() == null) {
            throw new GeneralException("فیلد از مقطع تحصیلی نباید خالی باشد");
        }

        if (dto.getUpgradeDegreeSeniorityToDegree() == null) {
            throw new GeneralException("فیلد به مقطع تحصیلی نباید خالی باشد");
        }
        repository.findByUpgradeDegreeSeniorityFromDegreeAndUpgradeDegreeSeniorityToDegree(
                        dto.getUpgradeDegreeSeniorityFromDegree(), dto.getUpgradeDegreeSeniorityToDegree())
                .ifPresent(e -> {
                    try {
                        throw new GeneralException("از مقطع تحصیلی به مقطح تحصیلی نباید تکراری باشد");
                    } catch (GeneralException ex) {
                        throw new RuntimeException(ex);
                    }
                });


        UpgradeDegreeSeniority entity = UpgradeDegreeSeniority.builder()
                .upgradeDegreeSeniorityId(UUID.randomUUID())
                .upgradeDegreeSeniorityFromDegree(dto.getUpgradeDegreeSeniorityFromDegree())
                .upgradeDegreeSeniorityToDegree(dto.getUpgradeDegreeSeniorityToDegree())
                .upgradeDegreeSeniorityIsActive(dto.getUpgradeDegreeSeniorityIsActive())
                .upgradeDegreeSeniorityMaxAmount(dto.getUpgradeDegreeSeniorityMaxAmount())
                .build();


        return repository.save(entity);
    }

    @Transactional
    public UpgradeDegreeSeniority updateUpgradeDegreeSeniority(UpgradeDegreeSeniorityDTO dto) throws GeneralException {
        // Validate that the ID is provided
        if (dto.getUpgradeDegreeSeniorityId() == null) {
            throw new IllegalArgumentException("UpgradeDegreeSeniority ID must not be null");
        }

        Optional<UpgradeDegreeSeniority> optional = repository.findByUpgradeDegreeSeniorityFromDegreeAndUpgradeDegreeSeniorityToDegree(
                dto.getUpgradeDegreeSeniorityFromDegree(), dto.getUpgradeDegreeSeniorityToDegree());

        if (optional.isPresent()) {
            UpgradeDegreeSeniority seniority = optional.get();
            if (seniority.getUpgradeDegreeSeniorityFromDegree().equals(dto.getUpgradeDegreeSeniorityFromDegree()) && seniority.getUpgradeDegreeSeniorityToDegree().equals(dto.getUpgradeDegreeSeniorityToDegree())
                    && (seniority.getUpgradeDegreeSeniorityMaxAmount().equals(dto.getUpgradeDegreeSeniorityMaxAmount()) && seniority.getUpgradeDegreeSeniorityIsActive().equals(dto.getUpgradeDegreeSeniorityIsActive()))) {
                throw new GeneralException("از مقطع تحصیلی به مقطح تحصیلی نباید تکراری باشد!");
            } else {
                return createUpgradeDegreeSeniorityAfterValidation(seniority, dto);
            }
        } else {
            UpgradeDegreeSeniority seniority = new UpgradeDegreeSeniority();
            return createUpgradeDegreeSeniorityAfterValidation(seniority, dto);
        }
    }

    private UpgradeDegreeSeniority createUpgradeDegreeSeniorityAfterValidation(UpgradeDegreeSeniority entity, UpgradeDegreeSeniorityDTO dto) {
        // Fetch the entity by ID or throw an exception if not found
        if (dto.getUpgradeDegreeSeniorityFromDegree() != null)
            entity.setUpgradeDegreeSeniorityFromDegree(dto.getUpgradeDegreeSeniorityFromDegree());
        if (dto.getUpgradeDegreeSeniorityToDegree() != null)
            entity.setUpgradeDegreeSeniorityToDegree(dto.getUpgradeDegreeSeniorityToDegree());
        if (dto.getUpgradeDegreeSeniorityMaxAmount() != null)
            entity.setUpgradeDegreeSeniorityMaxAmount(dto.getUpgradeDegreeSeniorityMaxAmount());
        if (dto.getUpgradeDegreeSeniorityIsActive() != null)
            entity.setUpgradeDegreeSeniorityIsActive(dto.getUpgradeDegreeSeniorityIsActive());
        return repository.save(entity);
    }

    public Page<UpgradeDegreeSeniority> getUpgradeDegreeSeniorities(Integer fromDegree, Integer toDegree, Boolean isActive, Integer fromAmount, Integer toAmount, Integer maxAmount, int page, int size) {
        Specification<UpgradeDegreeSeniority> spec = Specification
                .where(UpgradeDegreeSenioritySpecification.filterByFromDegree(fromDegree))
                .and(UpgradeDegreeSenioritySpecification.filterByToDegree(toDegree))
                .and(UpgradeDegreeSenioritySpecification.filterByStatus(isActive))
                .and(UpgradeDegreeSenioritySpecification.filterByMaxAmount(maxAmount))
                .and(UpgradeDegreeSenioritySpecification.filterByAmountRange(fromAmount, toAmount));

        return repository.findAll(spec, PageRequest.of(page, size, Sort.by(TblBase.Fields.updatedAt).descending()));
    }

    public void deleteUpgradeDegreeSeniority(UUID id) {
        repository.deleteById(id);
    }

    public Optional<UpgradeDegreeSeniority> getUpgradeDegreeSeniorityById(UUID id) {
        return repository.findById(id);
    }

    public Integer getMaxAmountWithLastDegreeAndNewDegree(Integer lastDegree, Integer newDegree) {
        return repository.getMaxAmountWithLastDegreeAndNewDegree(lastDegree, newDegree);
    }


}
