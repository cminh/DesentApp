package com.example.desent.desent.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by magnu on 19.03.2018.
 */

public class GMail {
    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";
    final String fileName = "DesentData.csv";

    String fromEmail;
    String fromPassword;
    List toEmailList;
    String emailSubject;
    String emailBody;
    String csv;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public GMail() {

    }

    public GMail(String fromEmail, String fromPassword, String emailSubject, String emailBody, String csv) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        //this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.csv = csv;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail)); // Change last to get name in sender
        /*
        for (int i=0; i<toEmailList.size(); i++) {
            String toEmail = toEmailList.get(i).toString();
            Log.i("GMail","toEmail: "+toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }
        */

        String toEmail = "desent.data.collect@gmail.com";
        Log.i("GMail","toEmail: "+toEmail);
        emailMessage.addRecipient(Message.RecipientType.TO,
                new InternetAddress(toEmail));

        emailMessage.setSubject(emailSubject);
        //emailMessage.setContent(emailBody, "text/html");// for a html email
        emailMessage.setText(emailBody + "\n" + csv);// for a text email
        Log.i("GMail", "Email Message created.");
        emailMessage.setFileName(fileName);
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail","allrecipients: "+emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }
}
