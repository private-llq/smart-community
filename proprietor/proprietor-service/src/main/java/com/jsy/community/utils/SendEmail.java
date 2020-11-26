package com.jsy.community.utils;

import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.util.MailSSLSocketFactory;

/**
* @Description: QQ邮件测试
 * @Param:
 * @Return:
 * @Author: chq459799974
 * @Date: 2020/11/26
**/
public class SendEmail 
{
    public static void main(String [] args) throws GeneralSecurityException 
    {
        //收件人
        String to = "971410648@qq.com";
    
        //发件人
        String from = "459799974@qq.com";
    
        //设置邮件发送的服务器，这里为QQ邮件服务器
        String host = "smtp.qq.com";
    
        //获取系统属性
        Properties properties = System.getProperties();
    
        //设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        //获取默认session对象(获取发送邮件会话、获取第三方登录授权码)
        Session session = Session.getDefaultInstance(properties,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(from, "nfezhwyeduksbjjh");
            }
        });

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
    
            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));
    
            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    
            // Set Subject: 头部头字段
            message.setSubject("测试邮件标题");
    
            // 设置消息体
            message.setText("测试邮件消息体");
    
            // 发送消息
            Transport.send(message);
            System.out.println("邮件发送完成");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}