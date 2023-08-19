package com.newbies.birdy.services;

public interface EmailService {

    void sendSimpleEmail(String toEmail,
                         String subject,
                         String body
    );
}
