package com.voixdesagesse.VoixDeSagesse.entity;

import java.time.Year;

public class Data {
    public static String getMessageBody (String otp, String nom, String prenom) {
        int currentYear = Year.now().getValue();
           return "<!DOCTYPE html><html><body>" +
                "<div style='font-family:Arial,sans-serif;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
                "<h2 style='color:#4CAF50;'>Verification Code</h2>" +
                "<p>Hello, " + nom + " " + prenom + " </p>" +
                "<p>Your One-Time Password (OTP) for verification is:</p>" +
                "<div style='font-size:24px;font-weight:bold;padding:10px;background-color:#f1f1f1;" +
                "display:inline-block;border-radius:5px;letter-spacing:4px;'>" + otp + "</div>" +
                "<p>This code is valid for the next 10 minutes. Do not share it with anyone.</p>" +
                "<p>If you did not request this code, please ignore this email.</p>" +
                "<br><p style='font-size:12px;color:#888;'>&copy; " + currentYear + " Voix De Sagesse</p>" +
                "</div></body></html>";
    }
}
