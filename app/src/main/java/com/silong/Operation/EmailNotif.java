package com.silong.Operation;

import android.util.Log;

import com.silong.Object.Adoption;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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

    private String EMAIL = "silong.sjdm@gmail.com";
    private String PASSWORD = "sfajljaebmquggxs";
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

    private void setContent(int status){
        switch (status){
            case REQUEST_APPROVED:
                SUBJECT = "Silong | Request Approved";
                BODY = "Your adoption application for PetID#" + ADOPTION.getPetID() + " has been APPROVED.";
                BODY += "\nPlease set an appointment for your visit using the Silong App.";
                BODY += "\n\n- Your Silong Team";
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
        }
    }

    boolean sent = false;
    public boolean sendNotif(){

        Log.d("DEBUGGER>>>", "Sending email");

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
                        Log.d("DEBUGGER>>>", "EmailNotif - thread: " + ex.getMessage());
                    }
                }
            });

            thread.start();
            return sent;

        }
        catch (Exception e){
            return false;
        }
    }

}
