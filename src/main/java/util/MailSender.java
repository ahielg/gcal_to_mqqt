package util;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Ahielg
 * @date 29/05/2016
 */
public class MailSender {
    private MailSender() {
    }

    public static void sendMail(String encPass, String to, String subject, String body) throws Exception {
        String host = "smtp.gmail.com";
        String from = KeysCons.FROM_MAIL;
        String pass = LocalEncryptor.decrypt(encPass);

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true"); // added this line
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        //String[] to = getMailAddresses();

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));

        //message.addRecipient(Message.RecipientType.BCC, new InternetAddress(from));
        // To get the array of addresses
/*
        for (String address : to) { // changed from a while loop
            //noinspection ObjectAllocationInLoop
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(address));
        }
*/

        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(to));

        message.setSubject(subject);

        message.setText(body);
        message.setContent(body, "text/html; charset=UTF-8");
        Transport transport = session.getTransport("smtp");
        transport.connect(host, from, pass);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

}
