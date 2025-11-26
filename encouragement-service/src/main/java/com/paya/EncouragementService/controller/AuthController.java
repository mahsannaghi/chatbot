package com.paya.EncouragementService.controller;

import com.paya.EncouragementService.dto.BasePersonnelDTO;
import com.paya.EncouragementService.dto.PaginationResponseDTO;
import com.paya.EncouragementService.dto.v2.PersonnelFilterDTOV2;
import com.paya.EncouragementService.entity.TblUser;
import com.paya.EncouragementService.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
public class AuthController {

    private final AuthService authService;
    private final HttpServletRequest request;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientID;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.logout-url}")
    private String logoutUri;
    public AuthController(AuthService authService, HttpServletRequest request) {
        this.authService = authService;
        this.request = request;
    }


    @GetMapping("/oauth2/callback/")
    public ResponseEntity<?> handleOAuth2Callback(@RequestParam String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // create headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            // create request body
            String body = "grant_type=authorization_code" +
                    "&code=" + code +
                    "&redirect_uri=" + redirectUri +
                    "&client_id=" + clientID +
                    "&client_secret=" + clientSecret;

            // Send request to Keycloak
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            return restTemplate.exchange(tokenUri, HttpMethod.POST, request, String.class);
        }
        catch (Exception exception){
            ErrorResponse errorResponse = new ErrorResponse("کد ارسال شده نامعتبر است", "400");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/get-current-user/")
    public ResponseEntity<?> getCurrentUser() {
        try{
            TblUser loginUser = authService.getCurrentUserProfile();
            return ResponseEntity.ok(loginUser);
        }
        catch (Exception exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("searchWithPersonnelDTO")
    public PaginationResponseDTO<? extends BasePersonnelDTO> searchWithPersonnelDTO(@RequestBody PersonnelFilterDTOV2 basePersonnelDTO,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "100") int size,
                                                                                    @RequestParam(defaultValue = "personnelFirstName") String sortBy,
                                                                                    @RequestParam(defaultValue = "asc") String direction) throws ExecutionException, InterruptedException {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return authService.searchWithPersonnelDTO(basePersonnelDTO, pageable);
    }

    @PatchMapping("/change-role/")
    public ResponseEntity<?> changeProfile(@RequestParam String newRole) {
        try{
            TblUser loginUser = authService.changeProfile(newRole);
            return ResponseEntity.ok(loginUser);
        }
        catch (Exception exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/logout/")
    public ResponseEntity<?> logout() throws ServletException, IOException, InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        request.logout();

        // Redirect to Keycloak logout
        String keycloakLogoutUrl = logoutUri
                + "?redirect_uri=http://10.16.113.20:8080/oauth2/authorization/keycloak";

        // Create an entity with the headers
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

        SecurityContextHolder.clearContext();
        return restTemplate.exchange(keycloakLogoutUrl, HttpMethod.GET, entity, String.class);
    }

}
