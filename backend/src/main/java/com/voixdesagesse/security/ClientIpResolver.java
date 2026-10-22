package com.voixdesagesse.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ClientIpResolver {

    private final String header;

    public ClientIpResolver(@Value("${app.client-ip-header:}") String header) {
        this.header = header == null ? "" : header.trim();
    }

    public String resolve(HttpServletRequest request) {
        if (!header.isEmpty()) {
            String value = request.getHeader(header);
            if (value != null && !value.isBlank())
                return value.trim();
        }
        return request.getRemoteAddr();
    }
}
