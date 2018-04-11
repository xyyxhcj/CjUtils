package com.github.xyyxhcj.utils;


import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


/**
 * 邮件工具类
 * @author CJ
 */
public class MailUtils {
    private static String smtp_host = "smtp.163.com"; // 网易
    private static String username = "18817237197@163.com"; // 邮箱账户
    private static String password = "as123456"; // 邮箱授权码
    private static String from = "18817237197@163.com"; // 使用当前账户

    /**
     *
     * @param subject subject
     * @param content content
     * @param to to
     */
    public static void sendMail(String subject, String content, String to) {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtp_host);
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(props);
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(content, "text/html;charset=utf-8");
            Transport transport = session.getTransport();
            transport.connect(smtp_host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("邮件发送失败...");
        }
    }

    public static void main(String[] args) {
        sendMail("quilllingstory异常记录20180329", "quilllingstory异常记录:<br/>", "18817237197@qq.com");
    }
}