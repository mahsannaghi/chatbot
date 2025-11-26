package com.paya.EncouragementService.feign.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "authFeign", url = "http://172.16.110.31:8080/auth/realms/TebyanWeb/protocol/openid-connect/userinfo")
public interface AuthFeign {
    @GetMapping
    Map<String, Object> getUserInfo(@RequestHeader String Authorization);
}
