package com.paya.EncouragementService.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.paya.EncouragementService.dto.BasePersonnelDTO;
import com.paya.EncouragementService.dto.PaginationResponseDTO;
import com.paya.EncouragementService.dto.PersonnelDTO;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import com.paya.EncouragementService.entity.TblUser;
import com.paya.EncouragementService.enumeration.RoleConstant;
import com.paya.EncouragementService.repository.UserRepository;
import com.paya.EncouragementService.utility.PaginationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {
    // This class is used to get current user by access token of header in each request
    private final UserRepository userRepository;
    private final PersonnelService personnelService;
    private final HttpServletRequest request;
    private final RoleMappingService  roleMappingService;

    @Value("${encouragement.typeOfPersonnelDTOSending}")
    private String typeOfPersonnelDTOSending;
    public AuthService(UserRepository userRepository, PersonnelService personnelService, HttpServletRequest request, RoleMappingService roleMappingService) {
        this.userRepository = userRepository;
        this.personnelService = personnelService;
        this.request = request;
        this.roleMappingService = roleMappingService;
    }

    private TblUser createUser(String username){
        // If there is not this current user in database, add new user to User table
        String uuid;
        TblUser user = new TblUser();
        user.setUsername(username);
        do {
            uuid = UUID.randomUUID().toString();
            uuid = uuid.replace("-", "");
        } while (this.userRepository.existsByUserId(uuid));

        user.setUserId(uuid);
        userRepository.save(user);

        return user;
    }

    public TblUser getCurrentUserProfile() throws ExecutionException, InterruptedException {
        TblUser currentUser = null;
        // This method is used to extract current user after login by access token
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof TblUser){
            currentUser = (TblUser) principal;
        }
        else{
            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                String accessToken = authorization.replace("Bearer", "").trim();
                Set<String> roles = roleMappingService.determineRoles();
                DecodedJWT tokenPayload = JWT.decode(accessToken);
                String username = tokenPayload.getClaim("preferred_username").asString();
                currentUser = userRepository.findByUsername(username);
                if (currentUser == null) {
                    currentUser = this.createUser(username);
                    //todo: remove this
                    currentUser.setCurrentRole("0");
                }
                currentUser.setRoles(roles.stream().map(r -> RoleConstant.ROLE.valueOf(r).getValue()).collect(Collectors.toSet()));
                userRepository.save(currentUser);
            }
        }

        if (currentUser.getUserInfo() == null){
            PersonnelDTO personnelDto = (PersonnelDTO) personnelService.findByOrganizationId(currentUser.getUsername(), typeOfPersonnelDTOSending);
            if (personnelDto == null){
                return null;
            }
            currentUser.setUserInfo(personnelDto);
            currentUser.setCurrentDate(LocalDate.now());
        }

        return currentUser;
    }

    public TblUser changeProfile(String newRole) throws ExecutionException, InterruptedException {
        TblUser currentUser = getCurrentUserProfile();
        if (currentUser != null){
            if (currentUser.getRoles().contains(newRole)){
                currentUser.setCurrentRole(newRole);
                userRepository.save(currentUser);
            }
        }
        return currentUser;
    }

    public PaginationResponseDTO<? extends BasePersonnelDTO> searchWithPersonnelDTO(PersonnelFilterDTOV2 filterDTOV2, Pageable pageable) throws ExecutionException, InterruptedException {
        List<? extends BasePersonnelDTO> list = personnelService.searchWithPersonnelDTO(filterDTOV2);
        return PaginationUtils.paginate(list, pageable);
    }
}
