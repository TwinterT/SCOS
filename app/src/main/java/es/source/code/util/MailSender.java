package es.source.code.util;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

/**
 * 处理邮件的类
 */
public class  MailSender extends Authenticator {
    //服务器地址
    private String mailhost = "smtp.163.com";
    //用于发送邮件的邮箱地址
    private String user;

    //用于发送邮件的邮箱密码
    private String password;

    //会话（每创建一次连接就要有一个会话）
    private Session session;


    public MailSender(String user, String password) {
        this.user = user;
        this.password = password;

        //创建连接属性
        Properties props = new Properties();

        //设置通信协议
        props.setProperty("mail.transport.protocol", "SMTP");

        //设置服务器地址
        props.setProperty("mail.smtp.host", mailhost);

        //设置是否需要SMTP身份验证
        props.put("mail.smtp.auth", "true");
        //设置SSL协议端口号
        props.put("mail.smtp.port", "25");


        session = Session.getInstance(props,this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        //创建邮件体
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);
        if (recipients.indexOf(',') > 0)
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipients));
        message.saveChanges();


        session.setDebug(true);
        //创建连接
        Transport trans = session.getTransport("smtp");
        //连接服务器
        trans.connect(mailhost,user, password);

        //发送消息
        trans.send(message);

    }
}
