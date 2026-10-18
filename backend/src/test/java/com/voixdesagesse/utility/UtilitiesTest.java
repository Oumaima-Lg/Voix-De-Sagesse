package com.voixdesagesse.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UtilitiesTest {

    @Test
    @DisplayName("Extrait : coupé entre deux mots, avec des points de suspension")
    void excerptCutsBetweenWords() {
        assertEquals("La patience est un arbre dont la racine est amère, mais dont…",
            Utilities.excerpt("La patience est un arbre dont la racine est amère, mais dont les fruits sont doux.", 60));
        assertEquals("Court et entier.", Utilities.excerpt("  Court et entier.  ", 60));
        assertEquals("Une virgule juste avant…", Utilities.excerpt("Une virgule juste avant, la coupe", 24));
        assertEquals("", Utilities.excerpt(null, 60));
    }
}
