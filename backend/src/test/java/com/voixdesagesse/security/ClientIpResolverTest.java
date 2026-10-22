package com.voixdesagesse.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientIpResolverTest {

    @Test
    @DisplayName("Derrière le proxy de l'hébergeur, l'adresse vient de l'en-tête configuré")
    void usesConfiguredHeader() {
        MockHttpServletRequest request = request("10.0.0.5");
        request.addHeader("True-Client-IP", "203.0.113.7");

        assertEquals("203.0.113.7", new ClientIpResolver("True-Client-IP").resolve(request));
    }

    @Test
    @DisplayName("Sans en-tête configuré, il est ignoré même si le client l'envoie")
    void ignoresHeaderWhenNotConfigured() {
        MockHttpServletRequest request = request("192.0.2.10");
        request.addHeader("True-Client-IP", "203.0.113.7");

        assertEquals("192.0.2.10", new ClientIpResolver("").resolve(request));
    }

    @Test
    @DisplayName("Si l'en-tête configuré manque, l'adresse de la connexion est utilisée")
    void fallsBackToConnection() {
        assertEquals("192.0.2.10", new ClientIpResolver("True-Client-IP").resolve(request("192.0.2.10")));
    }

    private static MockHttpServletRequest request(String remoteAddr) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        return request;
    }
}
