package com.example.demo.domain.port;

public interface GoogleTokenVerifierPort {

    record PerfilGoogle(String email, String name, boolean emailVerified) {}

    PerfilGoogle verify(String idToken);
}
