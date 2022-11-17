package com.vpbanks.timeline.util.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class SecurityUtils {

    private final ObjectMapper objectMapper;
    private static final String REALM_ACCESS = "realm_access";
    private static final String PREFERRED_USERNAME = "PREFERRED_USERNAME";
    public static final String USER_NAME = "user_name";

    private static final String TOKEN_TYPE_BEARER = "Bearer";


    public RolesDto parseToken(String token) {
        RolesDto rolesDto = new RolesDto();
        int i = token.lastIndexOf('.');
        String withoutSignature = token.substring(0, i + 1);
        Jwt<?, ?> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
        Claims body = (Claims) untrusted.getBody();

        LinkedHashMap<String, List<String>> hashMap = (LinkedHashMap<String, List<String>>) body.get(REALM_ACCESS);
        rolesDto.setRoles(hashMap.get("roles"));
        rolesDto.setName((String) body.get(PREFERRED_USERNAME));

        return rolesDto;
    }

    public static String getHeaderFromRequest(String key) {

        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return getHeaderFromRequest(request, key);
    }

    public static String getHeaderFromRequest(HttpServletRequest request, String key) {
        String agent = request.getHeader(key);
        if (org.springframework.util.StringUtils.hasText(agent)) {
            return agent;
        }
        return null;
    }

    public static HttpServletRequest getHttpServletRequest() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public boolean hasRoles(String... roleNames) {
        String accessToken = getHeaderFromRequest(HttpHeaders.AUTHORIZATION);

        return verifyRoleUser(accessToken, roleNames);
    }

    public static String getAccessToken(String accessToken) {
        if (accessToken.startsWith(TOKEN_TYPE_BEARER)) {
            accessToken = accessToken.substring(6).trim();
        }
        return accessToken;
    }

    public boolean verifyRoleUser(String accessToken, String... roleCodes) {
        try {
            if (StringUtil.isNullOrEmpty(accessToken)) return false;
            if (accessToken.startsWith(TOKEN_TYPE_BEARER)) {
                accessToken = accessToken.substring(6).trim();
            }
            RolesDto rolesDto = parseToken(accessToken);

            List<String> roleSet = rolesDto.getRoles();
            for (String roleCode : roleCodes) {
                if (roleSet.contains(roleCode)) {
                    ThreadContext.put(USER_NAME, rolesDto.getName());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new AccessDeniedException("Access Denied !");
        }

    }
}
