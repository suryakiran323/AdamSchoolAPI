package com.stu.app.service;


import java.io.File;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.stu.app.config.Constants;
import com.stu.app.model.Student;

@Service
@Log4j2
public class NotificationService {

	 @Autowired
	 private JavaMailSender mailSender;
	 @Autowired
	 private SimpleMailMessage preConfiguredMessage;
	 
    public NotificationService() {
        
    }

    public void sendMail(String toEmail, String subject, String message) {

    	SimpleMailMessage  mailMessage = new SimpleMailMessage();

        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailMessage.setFrom("jeeva.mca04@gmail.com");

        mailSender.send(mailMessage);
        log.info("Email sent successfully"
        		);
    }
    
    @Bean
    public SimpleMailMessage emailTemplate()
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("sanjeeva@computenext.com");
        message.setFrom("jeeva.mca04@gmail.com");
        message.setSubject("Important email");
        message.setText("FATAL - Application crash. error in sending email !!");
        return message;
    }
    
    public void sendMailWithAttachment(String to, String subject, String body, String fileToAttach) 
    {
        MimeMessagePreparator preparator = new MimeMessagePreparator() 
        {
            public void prepare(MimeMessage mimeMessage) throws Exception 
            {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom(new InternetAddress("admin@gmail.com"));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);
                 
                FileSystemResource file = new FileSystemResource(new File(fileToAttach));
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.addAttachment("logo.jpg", file);
            }
        };
         
        try {
            mailSender.send(preparator);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

	public void sendToParentMail(Student student) {
		String message = "Dear Parent,\nyour account is created successfully\n Click on the below link to activate.\n\n<a href='{actlink}'>Activate your account</a>"
				+ "\n\nRegards,\nAdams school,\nAdministrator.";
		
		message = message.replace("{actlink}", Constants.ACTIVATE_URL+student.getParent().getId());
		sendMail(student.getParent().getEmail(), "Account Verification", message);	
		
	}

	public void sendToParentStudentAdded(Student student) {
		String message = "Dear Parent,\nyour account is added new Student into your account\n\nRegards,\nAdams school,\nAdministrator.";
		
		sendMail(student.getParent().getEmail(), "New Student Added to your Account", message);	
		
	}
    
    
}
