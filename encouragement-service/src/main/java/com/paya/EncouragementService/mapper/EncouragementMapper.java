//package com.paya.EncouragementService.mapper;
//
//import com.paya.EncouragementService.dto.EncouragementReasonTypeDetailDTO;
//import com.paya.EncouragementService.entity.Encouragement;
//import com.paya.EncouragementService.dto.EncouragementDTO;
//import com.paya.EncouragementService.repository.EncouragementReasonTypeRepository;
//import com.paya.EncouragementService.service.EncouragementReasonService;
//import com.paya.EncouragementService.service.EncouragementReasonTypeService;
//import com.paya.EncouragementService.service.EncouragementService;
//import com.paya.EncouragementService.service.EncouragementTypeService;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Component
//public class EncouragementMapper {
//
//    @Autowired
//    private EncouragementReasonService encouragementReasonService;
//
//    @Autowired
//    private EncouragementTypeService encouragementTypeService;
//
//    @Autowired
//    private EncouragementService encouragementService;
//
//    @Autowired
//    private EncouragementReasonTypeService encouragementReasonTypeService;
//
//    @Autowired
//    private EncouragementReasonTypeRepository encouragementReasonTypeRepository;
//
//    private EncouragementReasonTypeDetailDTO fetchReasonTypeDetails(UUID reasonTypeId) {
//        return encouragementReasonTypeRepository.findReasonTypeDetailsById(reasonTypeId)
//                .orElseThrow(() -> new EntityNotFoundException("Details not found for reasonTypeId: " + reasonTypeId));
//    }
//
//
//    public EncouragementDTO toDTO(Encouragement encouragement) {
//        EncouragementDTO dto = new EncouragementDTO();
//
//
//        dto.setEncouragementId(encouragement.getEncouragementId());
//
//
//        List<UUID> relatedPersonnelIds = Collections.singletonList(encouragement.getEncouragementRelatedPersonnelId());
//        dto.setEncouragementRelatedPersonnelId(relatedPersonnelIds);
//
//
//        dto.setEncouragementRegistrarPersonnelId(encouragement.getEncouragementRegistrarPersonnelId());
//        dto.setEncouragementApproverPersonnelId(encouragement.getEncouragementApproverPersonnelId());
//        dto.setEncouragementReasonTypeId(encouragement.getEncouragementReasonTypeId());
//        dto.setEncouragementNumber(encouragement.getEncouragementNumber());
//        dto.setEncouragementAmount(encouragement.getEncouragementAmount());
//        dto.setEncouragementAmountType(encouragement.getEncouragementAmountType());
//        dto.setEncouragementDescription(encouragement.getEncouragementDescription());
//        dto.setEncouragementStatus(encouragement.getEncouragementStatus());
//
//
//        dto.setEncouragementApprovedAt(encouragement.getEncouragementApprovedAt());
//        dto.setEncouragementCreatedAt(encouragement.getEncouragementCreatedAt());
//
//
//     /*   String encouragementReasonTitle = encouragementReasonService.findReasonTitleById(encouragement.getEncouragementReasonTypeId());
//        UUID encouragementReasonId = encouragementReasonService.findReasonById(encouragement.getEncouragementReasonTypeId());
//
//
//        String encouragementTypeTitle = encouragementTypeService.findTypeTitleById(encouragement.getEncouragementReasonTypeId());
//        UUID encouragementTypeId = encouragementReasonService.findTypeById(encouragement.getEncouragementReasonTypeId());
//
//        dto.setEncouragementReasonTitle(encouragementReasonTitle);
//        dto.setEncouragementTypeTitle(encouragementTypeTitle);
//        dto.setMaxAmount(encouragement.getEncouragementAmount());
//
//        dto.setEncouragementReasonId(encouragementReasonId);
//        dto.setEncouragementTypeId(encouragementTypeId);
//*/
//
//        EncouragementReasonTypeDetailDTO reasonTypeDetails = fetchReasonTypeDetails(encouragement.getEncouragementReasonTypeId());
//
//
//        dto.setEncouragementReasonId(reasonTypeDetails.getEncouragementReasonId());  // reasonId
//        dto.setEncouragementTypeId(reasonTypeDetails.getEncouragementTypeId());      // typeId
//        dto.setEncouragementReasonTitle(reasonTypeDetails.getEncouragementReasonTitle());
//        dto.setEncouragementTypeTitle(reasonTypeDetails.getEncouragementTypeTitle());
//        dto.setMaxAmount(reasonTypeDetails.getMaxAmount());
//        dto.setMaxDuration(reasonTypeDetails.getMaxDuration());
//        dto.setDurationType(reasonTypeDetails.getDurationType());
//
//
//        return dto;
//    }
//
//    public List<EncouragementDTO> toDTOList(List<Encouragement> encouragementList) {
//        return encouragementList.stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//}
