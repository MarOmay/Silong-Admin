package com.silong.Operation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.silong.Object.Adoption;
import com.silong.admin.AdminData;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailNotif {

    //timeline stages
    public static final int CANCELLED = -1;
    public static final int SEND_REQUEST = 0;
    public static final int AWAITING_APPROVAL = 1;
    public static final int REQUEST_APPROVED = 2;
    public static final int SET_APPOINTMENT = 3;
    public static final int APPOINTMENT_CONFIRMED = 4;
    public static final int ADOPTION_SUCCESSFUL = 5;
    public static final int FINISHED = 6;
    public static final int DECLINED = 7;
    public static final int APPOINTMENT_CHANGED = 8;
    public static final int ADMIN_CREATED = 9;
    public static final int APPOINTMENT_TERMINATED = 10;

    private String EMAIL = "silong.sjdm@gmail.com";
    private String PASSWORD = "rfrdegmcruizkkuu";
    private String SENDER = "Silong Support";
    private String HOST = "smtp.gmail.com";
    private String PORT = "465";

    private int STATUS;
    private Adoption ADOPTION;

    private String SUBJECT;
    private String BODY;
    private String RECEIVER;

    public EmailNotif(String email, int status, Adoption adoption){
        this.RECEIVER = email;
        this.STATUS = status;
        this.ADOPTION = adoption;
        setContent(this.STATUS);
    }

    private String SUBMITTED_PASSWORD;

    public EmailNotif(String email, int status, String password){
        this.RECEIVER = email;
        this.STATUS = status;
        this.SUBMITTED_PASSWORD = password;
        setContent(this.STATUS);
    }

    private void setContent(int status){
        switch (status){
            case REQUEST_APPROVED:
                SUBJECT = "Silong | Request Approved";
                HTML_BODY = HTML_BODY.replace("#DATE_TODAY#", Utility.dateToday());
                HTML_BODY = HTML_BODY.replace("#PET_ID#", String.valueOf(ADOPTION.getPetID()));
                break;
            case APPOINTMENT_CONFIRMED:
                SUBJECT = "Silong | Appointment Confirmed";
                BODY = "Your adoption application for PetID#" + ADOPTION.getPetID() + " has been SCHEDULED ";
                BODY += "on " + ADOPTION.getAppointmentDate();
                BODY += ".\n\nPlease bring the following requirements: ";
                BODY += "\n - 2x2 ID Picture\n - Valid ID\n - Cage or Leash (Kulungan o Tali)";
                BODY += "\n\n- Your Silong Team";
                break;
            case ADOPTION_SUCCESSFUL:
                SUBJECT = "Silong | Adoption Successful";
                BODY = "Congratulations!";
                BODY += "\nThank you for providing home to one of our sheltered pets.";
                BODY += "\nWe look forward to serving you again.";
                BODY += "\n\n- Your Silong Team";
                break;
            case DECLINED:
                SUBJECT = "Silong | Request Declined";
                BODY = "Your adoption application for PetID#" + ADOPTION.getPetID() + " has been DECLINED.";
                BODY += "\nTo know more, please reach out to the City Veterinary Office of San Jose del Monte City.";
                BODY += "\n\n- Your Silong Team";break;
            case APPOINTMENT_CHANGED:
                SUBJECT = "Silong | Rescheduled Appointment";
                BODY = "Your adoption appointment for PetID#" + ADOPTION.getPetID() + " has been move to " + ADOPTION.getAppointmentDate() + ".";
                BODY += "\nWe apologize for any inconvenience that this might have caused you.";
                BODY += "\nTo know more, please reach out to the City Veterinary Office of San Jose del Monte City.";
                BODY += "\n\n- Your Silong Team";break;
            case ADMIN_CREATED:
                SUBJECT = "Silong | Employee Account";
                BODY = "Your Silong-Admin account is ready to use.";
                BODY += "\nThe account's default password is " + SUBMITTED_PASSWORD;
                BODY += "\nWe recommend changing your password immediately.";
                BODY += "\n\n- Your Silong Team";break;
            case APPOINTMENT_TERMINATED:
                SUBJECT = "Silong | Termination of Appointment";
                BODY = "Dear client,";
                BODY += "\nBased on your record, your appointment exceeded the allowable timeframe.";
                BODY += "\nYour adoption appointment for PetID#" + ADOPTION.getPetID() + " on " + ADOPTION.getAppointmentDate() + " is hereby CANCELLED.";
                BODY += "\n\nWe apologize for any inconvenience that this might have caused you.";
                BODY += "\nTo know more, please reach out to the City Veterinary Office of San Jose del Monte City.";
                BODY += "\n\n- Your Silong Team";break;
        }
    }

    boolean sent = false;
    public boolean sendNotif(){

        //try sending the email
        try{
            //set properties
            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", HOST);
            properties.put("mail.smtp.port", PORT);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            //compose mime
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(EMAIL, SENDER));

            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(RECEIVER)));

            mimeMessage.setSubject(SUBJECT);

            mimeMessage.setText(BODY);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        sent = true;
                    }
                    catch (Exception ex){
                        sent = false;
                        Utility.log("EmailNotif.sEA: " + ex.getMessage());
                    }
                }
            });

            thread.start();
            return sent;

        }
        catch (Exception e){
            Utility.log("EmailNotif.sendNotif: " + e.getMessage());
            return false;
        }
    }

    private Bitmap PET_PIC;
    public void sendEmailApproval(){

        //try sending the email
        try{
            //set properties
            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", HOST);
            properties.put("mail.smtp.port", PORT);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            //compose mime
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.addHeader("Content-type", "text/HTML; charset=UTF-8");
            mimeMessage.addHeader("format", "flowed");
            mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");

            mimeMessage.setFrom(new InternetAddress(EMAIL, SENDER));

            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(RECEIVER)));
            mimeMessage.setSubject(SUBJECT);

            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            //String htmlText = "<H1>Welcome to Medium!</H1>";
            messageBodyPart.setContent(HTML_BODY, "text/html");
            multipart.addBodyPart(messageBodyPart);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PET_PIC.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInByte = baos.toByteArray();

            MimeBodyPart imageBodyPart = new MimeBodyPart();
            ByteArrayDataSource bds = new ByteArrayDataSource(imageInByte, "image/jpeg");
            imageBodyPart.setDataHandler(new DataHandler(bds));
            imageBodyPart.setHeader("Content-ID", "<petphoto>");
            imageBodyPart.setFileName("petphoto.jpg");
            multipart.addBodyPart(imageBodyPart);

            mimeMessage.setContent(multipart);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        sent = true;
                    }
                    catch (Exception ex){
                        sent = false;
                        Utility.log("EmailNotif.sEA: " + ex.getMessage());
                    }
                }
            });

            thread.start();

        }
        catch (Exception e){
            Utility.log("EmailNotif.sEA: " + e.getMessage());
        }
    }

    private String HTML_BODY = "<html><head><link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Montserrat\"><style>.resize_fit_center { width: 100px; height:100px; vertical-align: middle; object-fit: cover; }</style></head><body style=\"font-family: Montserrat;\"><p>Your adoption request has been <b>approved</b>!</p><table><tr><td><img src=\"cid:petphoto\" alt=\"Pet Photo\" class=\"resize_fit_center\"></td><td style=\"padding-left: 10px;vertical-align: top;\"><p>Date: #DATE_TODAY#</p><p>Pet ID: #PET_ID#</p></td></tr></table><p>Kindly <b>set an appointment</b> using the Silong app.</p><p>Please present this email on your visit to the City Veterinary Office.</p><br><p>- Your Silong Team</p><img src=\"https://drive.google.com/uc?export=view&id=1F7k71GFicdhU2F4BUymmfox_QFNkTvEq\" alt=\"Header\" style=\"width: 150px;height: 50px;\"></body></html>";

    public void setPET_PIC(Bitmap PET_PIC) {
        this.PET_PIC = PET_PIC;
    }

    /*
    *       DIFFERENT FORM, VERY DIFFERENT USE
    *
    */

    private File file;
    private String title;
    private Activity activity;
    public EmailNotif(Activity activity, File file, String title){
        this.activity = activity;
        this.file = file;
        this.title = title;
        this.RECEIVER = AdminData.adminEmail;
    }

    public boolean sendWithAttachment(){
        try{
            //set properties
            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", HOST);
            properties.put("mail.smtp.port", PORT);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            //compose mime
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(EMAIL, SENDER));

            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(RECEIVER)));

            mimeMessage.setSubject("Silong Report | " + title);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hi! Attached is the file that you have generated.\n- Your Silong Team");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(file);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            mimeMessage.setContent(multipart);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        clean(activity, true);
                        sent = true;
                    }
                    catch (Exception ex){
                        sent = false;
                        clean(activity, false);
                        ex.printStackTrace();
                        Utility.log("EmailNotif - thread: " + ex.getMessage());
                    }
                }
            });

            thread.start();
            return sent;

        }
        catch (Exception e){
            e.printStackTrace();
            Utility.log("EmailNotif.sWA: " + e.getMessage());
            return false;
        }
    }

    private static void clean(Activity activity, boolean status){

        if (status)
            new Utility().showNotification(activity, "Report Created", "The report you requested has been sent to your email.");
        else
            new Utility().showNotification(activity, "Report Failed", "The report you requested was not successfully sent to your email.");

        for (File f : activity.getApplicationContext().getFilesDir().listFiles()){
            if (f.getAbsolutePath().endsWith(".xls"))
                f.delete();
        }

    }

}
