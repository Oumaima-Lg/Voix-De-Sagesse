package com.voixdesagesse.VoixDeSagesse.entity;

import java.time.Year;

public class Report {

    public static String getDeletedArticleMessage(String nom, String prenom, String articleName) {
        int currentYear = Year.now().getValue();
        return "<!DOCTYPE html><html><body>" +
                "<div style='font-family:Arial,sans-serif;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
                "<h2 style='color:#E53935;'>Notification de Suppression d’Article</h2>" +
                "<p>Bonjour, " + nom + " " + prenom + ",</p>" +
                "<p>L’article suivant a été <strong>signalé</strong> par nos utilisateurs et a donc été " +
                "<span style='color:#E53935;font-weight:bold;'>supprimé</span> de notre plateforme :</p>" +
                "<div style='font-size:18px;font-weight:bold;padding:10px;background-color:#f9f9f9;" +
                "display:inline-block;border-radius:5px;'>" + articleName + "</div>" +
                "<p>Cette suppression fait suite à plusieurs signalements indiquant que l’article ne respecte pas " +
                "nos règles ou standards de qualité.</p>" +
                "<p>Nous vous invitons à examiner ce problème et à prendre les mesures nécessaires afin d’éviter " +
                "que cela ne se reproduise.</p>" +
                "<br><p style='font-size:12px;color:#888;'>&copy; " + currentYear + " Voix De Sagesse</p>" +
                "</div></body></html>";
    }

    
}
