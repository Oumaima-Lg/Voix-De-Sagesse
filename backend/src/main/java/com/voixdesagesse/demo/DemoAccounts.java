package com.voixdesagesse.demo;

import java.util.Set;

public final class DemoAccounts {

    public static final String DEMO_EMAIL = "demo@voixdesagesse.local";
    public static final String KARIM_EMAIL = "karim@voixdesagesse.local";
    public static final String AMINA_EMAIL = "amina@voixdesagesse.local";
    public static final String YOUSSEF_EMAIL = "youssef@voixdesagesse.local";
    public static final String ADMIN_EMAIL = "admin@voixdesagesse.local";

    private static final Set<String> SHARED = Set.of(DEMO_EMAIL, KARIM_EMAIL, AMINA_EMAIL, YOUSSEF_EMAIL);

    private DemoAccounts() {
    }

    public static boolean isShared(String email) {
        return email != null && SHARED.contains(email.toLowerCase());
    }
}
