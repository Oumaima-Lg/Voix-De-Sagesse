package com.voixdesagesse.mail;

import java.time.Year;

import org.springframework.web.util.HtmlUtils;

/** HTML bodies of the emails sent by the application. User-provided values are always escaped. */
public final class EmailTemplates {

    private EmailTemplates() {
    }

    public static String otpCode(String otp, String nom, String prenom) {
        return layout(
                "<h2 style='color:#4CAF50;'>Code de vérification</h2>" +
                "<p>Bonjour " + escape(prenom) + " " + escape(nom) + ",</p>" +
                "<p>Voici votre code pour réinitialiser votre mot de passe :</p>" +
                codeBox(otp));
    }

    public static String registrationCode(String otp, String nom, String prenom) {
        return layout(
                "<h2 style='color:#4CAF50;'>Bienvenue sur Voix De Sagesse</h2>" +
                "<p>Bonjour " + escape(prenom) + " " + escape(nom) + ",</p>" +
                "<p>Voici votre code pour confirmer votre adresse email et créer votre compte :</p>" +
                codeBox(otp));
    }

    private static String codeBox(String otp) {
        return "<div style='font-size:24px;font-weight:bold;padding:10px;background-color:#f1f1f1;" +
                "display:inline-block;border-radius:5px;letter-spacing:4px;'>" + escape(otp) + "</div>" +
                "<p>Ce code est valable 10 minutes. Ne le communiquez à personne.</p>" +
                "<p>Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.</p>";
    }

    public static String articleDeleted(String nom, String prenom, String articleName) {
        return layout(
                "<h2 style='color:#E53935;'>Notification de Suppression d’Article</h2>" +
                "<p>Bonjour, " + escape(nom) + " " + escape(prenom) + ",</p>" +
                "<p>L’article suivant a été <strong>signalé</strong> par nos utilisateurs et a donc été " +
                "<span style='color:#E53935;font-weight:bold;'>supprimé</span> de notre plateforme :</p>" +
                "<div style='font-size:18px;font-weight:bold;padding:10px;background-color:#f9f9f9;" +
                "display:inline-block;border-radius:5px;'>" + escape(articleName) + "</div>" +
                "<p>Cette suppression fait suite à plusieurs signalements indiquant que l’article ne respecte pas " +
                "nos règles ou standards de qualité.</p>" +
                "<p>Nous vous invitons à examiner ce problème et à prendre les mesures nécessaires afin d’éviter " +
                "que cela ne se reproduise.</p>");
    }

    private static String layout(String content) {
        return "<!DOCTYPE html><html><body>" +
                "<div style='font-family:Arial,sans-serif;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
                content +
                "<br><p style='font-size:12px;color:#888;'>&copy; " + Year.now().getValue() + " Voix De Sagesse</p>" +
                "</div></body></html>";
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value);
    }
}
