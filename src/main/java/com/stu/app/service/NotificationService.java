package com.stu.app.service;


import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.mail.Message;
import javax.mail.MessagingException;
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
import com.stu.app.model.Users;

@Service
@Log4j2
public class NotificationService {

	 @Autowired
	 private JavaMailSender mailSender;
	 @Autowired
	 private SimpleMailMessage preConfiguredMessage;
	 public static final String SCHOOL_NAME = "Adam's New Pre Uni College";
	 public static final int noOfQuickServiceThreads = 20;
	 private ScheduledExecutorService quickService = Executors.newScheduledThreadPool(noOfQuickServiceThreads);
	 
    public NotificationService() {
        
    }

    public void sendMail(String toEmail, String subject, String message) {
    	try{
	    	//SimpleMailMessage  mailMessage = new SimpleMailMessage();
	    	MimeMessage mimeMessage = mailSender.createMimeMessage();
	    	MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
	    	//mimeMessage.setContent(htmlMsg, "text/html"); /** Use this or below line **/
	    	helper.setTo(toEmail);
	    	helper.setSubject(subject);
	    	helper.setText(message, true);
	        
	    	helper.setFrom("jeeva.mca04@gmail.com");
	
	        
	        quickService.submit(new Runnable() {
				@Override
				public void run() {
					try{
						mailSender.send(mimeMessage);
						 log.info("Email sent successfully");
					}catch(Exception e){
						log.error("Exception occur while send a mail : ",e);
					}
				}
			});
	        
	        log.info("Email Content:" + message);
    	}catch(MessagingException exp){
    		log.error(message);
    		log.error(exp.getLocalizedMessage());
    	}
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
		String message = "Dear "+student.getParent().getFirstName()+",<br/>your account is created successfully"
				+ "<br/> Click on the below link to activate.<br/><br/><a href='{actlink}'>Activate your account</a>"
				+ "<br/><br/>Regards,<br/>" + SCHOOL_NAME + ",<br/>Administrator.";
		
		message = message.replace("{actlink}", Constants.ACTIVATE_URL+student.getParent().getId());
		sendMail(student.getParent().getEmail(), "Account Verification", message);	
		
	}

	public void sendToParentStudentAdded(Student student) {
		String message = "Dear Parent,<br/><br/>your account is added new Student("+student.getFirstName()+") into your account.<br/>You will be notified once its activated.<br/><br/>Regards,<br/>" + SCHOOL_NAME + ",<br/>Administrator.";
		
		sendMail(student.getParent().getEmail(), "New Student Added!", message);	
		
	}

	public void sendCredentials(Users user, String pwd) {
		String message = "Dear "+user.getFirstName()+",<br/><br/>your account is activated successfully<br/> Here is the password: "+pwd 
				+ "<br/><br/>Regards,<br/>" + SCHOOL_NAME + ",<br/>Administrator.";
		
		sendMail(user.getEmail(), "Account Verification", message);	
		
	}

	public void sendFacultyMail(Users users) {
		String message = "Dear "+users.getFirstName()+",<br/>your account is created successfully<br/> Click on the below link to activate.<br/><br/><a href='{actlink}'>Activate your account</a>"
				+ "<br/><br/>Regards,<br/>" + SCHOOL_NAME + ",<br/>Administrator.";
		
		message = message.replace("{actlink}", Constants.ACTIVATE_URL+users.getId());
		sendMail(users.getEmail(), "Account Verification", message);
		
	}

	public void sendResetPassword(String email, String firstName, String password) {
		String message = "Dear "+firstName+",<br/>your account Password is reset successfully.<br/> Use the following password for your login.<br/>"
				+"Password: "+password
				+ "<br/><br/>Regards,<br/>"+SCHOOL_NAME+",<br/>Administrator.";		
		sendMail(email, "Account Reset password", message);		
	}
    
    
}
