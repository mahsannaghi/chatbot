package com.paya.EncouragementService.service;

import com.paya.EncouragementService.dto.QuranicSeniorityDTO;
import com.paya.EncouragementService.entity.QuranicEncouragement;
import com.paya.EncouragementService.entity.QuranicSeniority;
import com.paya.EncouragementService.repository.QuranicEncouragementRepository;
import com.paya.EncouragementService.repository.QuranicSeniorityRepository;
import com.paya.EncouragementService.Specification.QuranicSenioritySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
//import paya.net.exceptionhandler.Exception.GeneralException;
//import paya.net.exceptionhandler.Exception.ValidationException;

import java.util.Optional;
import java.util.UUID;

@Service
public class QuranicSeniorityService {

    @Autowired
    private QuranicSeniorityRepository repository;

    @Autowired
    private QuranicEncouragementRepository quranicEncouragementRepository;

    private static int MAX_SENIORITY = 48;


    public int getMaxSeniority() {
        return MAX_SENIORITY;
    }


    public void updateMaxSeniority(int newMaxSeniority) {
        MAX_SENIORITY = newMaxSeniority;
    }


    public QuranicSeniority createQuranicSeniority(QuranicSeniorityDTO dto) throws Exception {
        if (dto.getQuranicSeniorityType() == null || dto.getQuranicSeniorityType().trim().isEmpty()) {
            throw new Exception("نوع نباید خالی  باشد!");
        }
        if (dto.getQuranicSeniorityType() != null)
            dto.setQuranicSeniorityType(dto.getQuranicSeniorityType().replace("ی", "ي").trim());
        Optional<QuranicSeniority> optional = repository.findByQuranicSeniorityTypeAndQuranicSeniorityAmount(dto.getQuranicSeniorityType(), dto.getQuranicSeniorityAmount());
        if (optional.isPresent()) {
            QuranicSeniority seniority = optional.get();
            if (seniority.getQuranicSeniorityType().equals(dto.getQuranicSeniorityType()) && seniority.getQuranicSeniorityAmount().equals(dto.getQuranicSeniorityAmount()) &&
                    (seniority.getQuranicSeniorityIsActive().equals(dto.getQuranicSeniorityIsActive()) || seniority.getQuranicSeniorityMaxAmount().equals(dto.getQuranicSeniorityMaxAmount()))) {
                throw new Exception("نوع و میزان تکراری است!");
            } else
                return createQuranicSeniorityAfterValidation(seniority, dto);
        } else {
            QuranicSeniority entity= new QuranicSeniority();
            return createQuranicSeniorityAfterValidation(entity, dto);
        }
    }

    private QuranicSeniority createQuranicSeniorityAfterValidation(QuranicSeniority entity, QuranicSeniorityDTO dto) {
        if (dto.getQuranicSeniorityType() != null)
            entity.setQuranicSeniorityType(dto.getQuranicSeniorityType());
        if (dto.getQuranicSeniorityAmount() != null)
            entity.setQuranicSeniorityAmount(dto.getQuranicSeniorityAmount());
        if (dto.getQuranicSeniorityMaxAmount() != null)
            entity.setQuranicSeniorityMaxAmount(dto.getQuranicSeniorityMaxAmount());
        if (dto.getQuranicSeniorityIsActive() != null)
            entity.setQuranicSeniorityIsActive(dto.getQuranicSeniorityIsActive());
        return repository.save(entity);
    }


    public QuranicSeniority updateQuranicSeniority(QuranicSeniorityDTO dto) throws Exception {
        QuranicSeniority entity = repository.findById(dto.getQuranicSeniorityId())
                .orElseThrow(() -> new Exception("Entity with ID not found"));

        Optional<QuranicSeniority> optional = repository.findByQuranicSeniorityTypeAndQuranicSeniorityAmount(dto.getQuranicSeniorityType(), dto.getQuranicSeniorityAmount());
        if (optional.isPresent()) {
            QuranicSeniority seniority = optional.get();
            if (!(seniority.getQuranicSeniorityType().equals(dto.getQuranicSeniorityType()) && seniority.getQuranicSeniorityAmount().equals(dto.getQuranicSeniorityAmount()) &&
                    (seniority.getQuranicSeniorityIsActive().equals(dto.getQuranicSeniorityIsActive()) || seniority.getQuranicSeniorityMaxAmount().equals(dto.getQuranicSeniorityMaxAmount())))) {
                throw new Exception("نوع و میزان تکراری است!");
            }
            if (!seniority.getQuranicSeniorityId().equals(dto.getQuranicSeniorityId()) && seniority.getQuranicSeniorityType().equals(dto.getQuranicSeniorityType()) && seniority.getQuranicSeniorityAmount().equals(dto.getQuranicSeniorityAmount())) {
                throw new Exception("نوع و میزان تکراری است!");
            }
            Optional<QuranicEncouragement> optional1 = quranicEncouragementRepository.findByQuranicSeniorityId(dto.getQuranicSeniorityId());
            if (optional1.isPresent()) {
                if (seniority.getQuranicSeniorityIsActive().equals(dto.getQuranicSeniorityIsActive()))
                    throw new Exception("قابل ویرایش نمی باشد چرا که در تشویقات قرآنی ثبت شده است!");
            }

        }
        boolean isTypeSame = entity.getQuranicSeniorityType().equals(dto.getQuranicSeniorityType());
        boolean isActiveSame =
                (dto.getQuranicSeniorityIsActive() == null && entity.getQuranicSeniorityIsActive() == null) ||
                        (dto.getQuranicSeniorityIsActive() != null && dto.getQuranicSeniorityIsActive().equals(entity.getQuranicSeniorityIsActive()));
        boolean isAmountSame =
                (dto.getQuranicSeniorityAmount() == null && entity.getQuranicSeniorityAmount() == null) ||
                        (dto.getQuranicSeniorityAmount() != null && dto.getQuranicSeniorityAmount().equals(entity.getQuranicSeniorityAmount()));
        boolean isMaxAmountSame =
                (dto.getQuranicSeniorityMaxAmount() == null && entity.getQuranicSeniorityMaxAmount() == null) ||
                        (dto.getQuranicSeniorityMaxAmount() != null && dto.getQuranicSeniorityMaxAmount().equals(entity.getQuranicSeniorityMaxAmount()));


        if (isTypeSame && isActiveSame && isAmountSame && isMaxAmountSame) {
            throw new Exception("No changes detected");
        }


        entity.setQuranicSeniorityType(dto.getQuranicSeniorityType());
        entity.setQuranicSeniorityAmount(dto.getQuranicSeniorityAmount());
        entity.setQuranicSeniorityMaxAmount(dto.getQuranicSeniorityMaxAmount());
        entity.setQuranicSeniorityIsActive(dto.getQuranicSeniorityIsActive() != null ? dto.getQuranicSeniorityIsActive() : entity.getQuranicSeniorityIsActive());

        return repository.save(entity);
    }

    public Page<QuranicSeniority> getQuranicSeniorities(String type,String amount, Boolean isActive,Integer fromAmount, Integer toAmount, int page, int size) {
        Specification<QuranicSeniority> spec = Specification
                .where(QuranicSenioritySpecification.filterByType(type))
                .and(QuranicSenioritySpecification.filterByAmount(amount))
                .and(QuranicSenioritySpecification.filterByMaxAmountRange(fromAmount, toAmount))

                .and(QuranicSenioritySpecification.filterByStatus(isActive));

        return repository.findAll(spec, PageRequest.of(page, size));
    }

    public void deleteQuranicSeniority(UUID id) throws Exception {
        Optional<QuranicEncouragement> optional1 = quranicEncouragementRepository.findByQuranicSeniorityId(id);
        if (optional1.isPresent()) {
            throw new Exception("قابل حذف نمی باشد چرا که در تشویقات قرآنی ثبت شده است!");
        }else
            repository.deleteById(id);
    }

    public Optional<QuranicSeniority> getQuranicSeniorityById(UUID id) {
        return repository.findById(id);
    }
}
