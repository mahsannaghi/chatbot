package com.paya.EncouragementService.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paya.EncouragementService.enumeration.RoleConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RoleMappingService {
    /**
     * Determine roles or permissions based on user info.
     *
     * @param accessToken The access token of the user.
     * @return A list of roles or permissions.
     */
    private final HttpServletRequest request;

    @Value("${objectKey.serviceRoleKey}")
    private String roleKey;


    public RoleMappingService(HttpServletRequest request) {
        this.request = request;
    }

    public Set<String> determineRoles() {
        String authorizationHeader = request.getHeader("Authorization");
        Set<String> roles = new HashSet<>();
        List<Map<String, Integer>> objectKeyValues = new ArrayList<>();
        Claim objectKeysClaim = null;

        if (authorizationHeader != null && authorizationHeader.trim().startsWith("Bearer")) {
            String accessToken = authorizationHeader.replace("Bearer", "").trim();
            if (accessToken.isEmpty() || accessToken.isBlank()) {
                return new HashSet<>();
            }
            DecodedJWT jwt = JWT.decode(accessToken);
            objectKeysClaim = jwt.getClaim("objectKeys");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectKeyValues = objectMapper.readValue(objectKeysClaim.toString(), List.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse 'objectKeys' claim from JWT", e);
            }

        }

        if (objectKeyValues instanceof List<?>) {
            if (!objectKeyValues.isEmpty() && objectKeyValues.get(0) instanceof Map<?, ?>) {
                Map<String, Integer> firstElement = objectKeyValues.get(0);

                for (String key : firstElement.keySet()) {
                    if (firstElement.get(key) == 1) {
                        RoleConstant.ROLE role = RoleConstant.ROLE.fromKeyName(key);
                        if (role != null) {
                            roles.add(role.name());
                        }
                    }
                }
            }
        }
        return roles;
    }

}
