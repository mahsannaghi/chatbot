//package com.paya.EncouragementService.service;
//
//import com.paya.EncouragementService.dto.PersonnelDTO;
//import com.paya.EncouragementService.dto.humanResource.*;
//import com.paya.EncouragementService.mapper.PersonnelMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class HumanResourceApiService {
//    @Value("${humanResourceKeycloak.token-url}")
//    private String tokenUrl;
//    @Value("${humanResourceKeycloak.client-id}")
//    private String clientId;
//    @Value("${humanResourceKeycloak.client-secret}")
//    private String clientSecret;
//    @Value("${humanResourceKeycloak.password}")
//    private String password;
//    @Value("${humanResourceKeycloak.user-name}")
//    private String username;
//    @Value("${humanResourceKeycloak.grant-type}")
//    private String grantType;
//    @Value("${humanResourceKeycloak.scope}")
//    private String scope;
//
//    @Value("${humanResource.personnel-infos-url}")
//    private String personnelInfoUrl;
//    @Value("${humanResource.personnel-info-by-login}")
//    private String personnelInfoByLoginUrl;
//
//    @Value("${humanResource.personnel-info}")
//    private String personnelInfo;
//    @Value("${humanResource.all-related-login-by-login}")
//    private String personnelAllRelatedLoginByLogin;
//
//    @Value("${humanResource.all-personnel-of-unit}")
//    private String allPersonnelOfUnit;
//
//    public ResponseEntity<HumanResourceTokenDTO> getToken() throws Exception {
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//            formData.add("grant_type", grantType);
//            formData.add("username", username);
//            formData.add("password", password);
//            formData.add("client_id", clientId);
//            formData.add("client_secret", clientSecret);
//            formData.add("scope", scope);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
//            return restTemplate.exchange(tokenUrl, HttpMethod.POST, request, HumanResourceTokenDTO.class);
//        } catch (HttpClientErrorException | HttpServerErrorException ex) {
//            throw new Exception("خطای احراز هویت.");
//        } catch (Exception exception) {
//            throw new Exception("خطایی رخ داده است.");
//        }
//    }
//
//    public HumanResourceBasePersonnelDTO getPersonnelInfoByOrganizationId(String organizationId) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        String accessToken = getAccessTokenFromLoginIfPresent();
//        headers.setBearerAuth(accessToken);
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);
//        String url = personnelInfoUrl + personnelInfoByLoginUrl;
//        url = url.concat("/").concat(organizationId);
//        ResponseEntity<HumanResourceBasePersonnelDTO> response = restTemplate.exchange(url, HttpMethod.GET, request, HumanResourceBasePersonnelDTO.class);
//        if (response.getStatusCode().equals(HttpStatus.OK)) {
//            HumanResourceBasePersonnelDTO humanResourceBasePersonnelDTO = response.getBody();
//            if (humanResourceBasePersonnelDTO != null) {
//                humanResourceBasePersonnelDTO.setPersonnelOrganizationID(humanResourceBasePersonnelDTO.getPersonnelLogin());
//                return humanResourceBasePersonnelDTO;
//            } else
//                throw new Exception("خطا در اطلاعات.");
//        } else
//            throw new Exception("خطا در دریافت اطلاعات از سازمان.");
//    }
//
//    public List<HumanResourceRelatedManagerDTO> getAllRelatedManagersByOrganizationId(String organizationId) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);
//        String accessToken = getAccessTokenFromLoginIfPresent();
//        headers.setBearerAuth(accessToken);
//        String url = personnelAllRelatedLoginByLogin;
//        url = url.concat(organizationId);
//        ResponseEntity<List<HumanResourceRelatedManagerDTO>> response = restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
//        });
//        if (response.getStatusCode().equals(HttpStatus.OK)) {
//            List<HumanResourceRelatedManagerDTO> humanResourceRelatedManagerDTOS = response.getBody();
//            if (humanResourceRelatedManagerDTOS != null)
//                return humanResourceRelatedManagerDTOS;
//            else
//                throw new Exception("خطا در اطلاعات.");
//        } else
//            throw new Exception("خطا در دریافت اطلاعات از سازمان.");
//    }
//
//    private String getAccessToken() throws Exception {
//        ResponseEntity<HumanResourceTokenDTO> tokenResponseEntity = getToken();
//        if (tokenResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
//            HumanResourceTokenDTO humanResourceTokenDTO = tokenResponseEntity.getBody();
//            if (humanResourceTokenDTO != null)
//                return humanResourceTokenDTO.getAccess_token();
//            else
//                throw new Exception("توکن خالی ست.");
//        }else
//            throw new Exception("خطا در دریافت توکن.");
//    }
//
//    public List<HumanResourceUnitPersonnelDTO> getAllPersonnelOfUnit(String unitName) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        String accessToken = getAccessTokenFromLoginIfPresent();
//        headers.setBearerAuth(accessToken);
//        headers.setBearerAuth(accessToken);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//        String url = allPersonnelOfUnit;
//        url = url.concat(unitName);
//        ResponseEntity<List<HumanResourceUnitPersonnelDTO>> response = restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
//        });
//        if (response.getStatusCode().equals(HttpStatus.OK)) {
//            List<HumanResourceUnitPersonnelDTO> personnelDTOList = response.getBody();
//            if (personnelDTOList != null)
//                return personnelDTOList;
//            else
//                throw new Exception("خطا در اطلاعات.");
//        } else
//            throw new Exception("خطا در دریافت اطلاعات از سازمان.");
//
//    }
//
//    public List<PersonnelDTO> getPersonnelWithFilter(HumanResourceBasePersonnelDTO basePersonnelDTO) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        String accessToken = getAccessTokenFromLoginIfPresent();
//        headers.setBearerAuth(accessToken);
//        headers.setBearerAuth(accessToken);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//        String url = personnelInfoUrl + personnelInfo;
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//        if (basePersonnelDTO.getPersonnelLogin() != null) builder.queryParam(HumanResourceBasePersonnelDTO.Fields.personnelLogin.concat(".equals"), basePersonnelDTO.getPersonnelLogin());
//        if (basePersonnelDTO.getFirstName() != null) builder.queryParam(HumanResourceBasePersonnelDTO.Fields.firstName.concat(".contains"), basePersonnelDTO.getFirstName());
//        if (basePersonnelDTO.getLastname() != null) builder.queryParam(HumanResourceBasePersonnelDTO.Fields.lastname.concat(".contains"), basePersonnelDTO.getLastname());
//        if (basePersonnelDTO.getOrganCandidate() != null) builder.queryParam(HumanResourceBasePersonnelDTO.Fields.organCandidate.concat(".equals"), basePersonnelDTO.getOrganCandidate());
//        URI uri = builder.build().toUri();
//        ResponseEntity<HumanResourceListOfPersonnelDTO> response = restTemplate.exchange(uri, HttpMethod.GET, request, HumanResourceListOfPersonnelDTO.class);
//        if (response.getStatusCode().equals(HttpStatus.OK)) {
//            HumanResourceListOfPersonnelDTO humanResourceListOfPersonnelDTO = response.getBody();
//            if (humanResourceListOfPersonnelDTO != null) {
//                return humanResourceListOfPersonnelDTO.getContent().stream().map(PersonnelMapper::convertToDTO).collect(Collectors.toList());
//            }
//            else
//                return null;
//        } else
//            throw new Exception("خطا در دریافت اطلاعات از سازمان.");
//
//    }
//
//    public PersonnelDTO getPersonnelWithOrganizationId(String orgId) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        String accessToken = getAccessTokenFromLoginIfPresent();
//        headers.setBearerAuth(accessToken);
//        headers.setBearerAuth(accessToken);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//        String url = personnelInfoUrl + personnelInfo;
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//        if (orgId != null) builder.queryParam("personnelLogin".concat(".equals"), "10871087");
//        URI uri = builder.build().toUri();
//        ResponseEntity<HumanResourceListOfPersonnelDTO> response = restTemplate.exchange(uri, HttpMethod.GET, request, HumanResourceListOfPersonnelDTO.class);
//        if (response.getStatusCode().equals(HttpStatus.OK)) {
//            HumanResourceListOfPersonnelDTO humanResourceListOfPersonnelDTO = response.getBody();
//            if (humanResourceListOfPersonnelDTO != null) {
//                List<HumanResourcePersonnelDTO> dtoList = humanResourceListOfPersonnelDTO.getContent();
//                if (!dtoList.isEmpty())
//                    return PersonnelMapper.convertToDTO(dtoList.get(0));
//                else
//                    return null;
//            }
//            else
//                return null;
//        } else
//            throw new Exception("خطا در دریافت اطلاعات از سازمان.");
//    }
//
//    public PersonnelDTO getPersonnelWithId(UUID id) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        String accessToken = getAccessTokenFromLoginIfPresent();
//        headers.setBearerAuth(accessToken);
//        headers.setBearerAuth(accessToken);
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//        String url = personnelInfoUrl + personnelInfo;
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//        if (id != null) builder.queryParam(HumanResourceBasePersonnelDTO.Fields.id.concat(".equals"), id);
//        URI uri = builder.build().toUri();
//        ResponseEntity<HumanResourceListOfPersonnelDTO> response = restTemplate.exchange(uri, HttpMethod.GET, request, HumanResourceListOfPersonnelDTO.class);
//        if (response.getStatusCode().equals(HttpStatus.OK)) {
//            HumanResourceListOfPersonnelDTO humanResourceListOfPersonnelDTO = response.getBody();
//            if (humanResourceListOfPersonnelDTO != null) {
//                List<HumanResourcePersonnelDTO> dtoList = humanResourceListOfPersonnelDTO.getContent();
//                if (!dtoList.isEmpty())
//                    return PersonnelMapper.convertToDTO(dtoList.get(0));
//                else
//                    return null;
//            }
//            else
//                return null;
//        } else
//            throw new Exception("خطا در دریافت اطلاعات از سازمان.");
//
//    }
//
//    public void updateNumberOfPersonnelEncouragement(String encouragementPersonnelOrganizationId) throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json");
//        String accessToken = getAccessTokenFromLoginIfPresent();
//        headers.setBearerAuth(accessToken);
//        headers.setBearerAuth(accessToken);
//        Map<String, String> body = new HashMap<>();
//        body.put("personnelLogin", encouragementPersonnelOrganizationId);
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
//        String url = personnelInfoUrl + personnelInfo;
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//        URI uri = builder.build().toUri();
//        restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
//    }
//
//    public String getAccessTokenFromLoginIfPresent() throws Exception {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (attributes == null) return getAccessToken();
//        HttpServletRequest request = attributes.getRequest();
//        String authorization = request.getHeader("Authorization");
//        if (authorization != null)
//            return authorization.replace("Bearer", "").trim();
//        return getAccessToken();
//    }
//
//}
