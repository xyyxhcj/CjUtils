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
    /**
     * 邮箱地址
     */
    private static String smtp_host = "smtp.163.com";
    /**
     * 邮箱账户登录名
     */
    private static String username = "18817237197@163.com";
    /**
     * 邮箱授权码
     */
    private static String password = "******";
    /**
     * 当前邮箱
     */
    private static String from = "18817237197@163.com";

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

    /**
     * 测试
     * @param args args
     */
    public static void main(String[] args) {
        sendMail("异常记录", "异常记录:<br/>", "xyyxhcj@qq.com");
    }
}