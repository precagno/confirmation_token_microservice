package com.example.demo.email;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService implements EmailSender {

  private final JavaMailSender mailSender;

  @Override
  @Async
  public void send(String receiverAddress, String emailBody) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(mimeMessage, "utf-8");
      helper.setTo(receiverAddress);
      helper.setSubject("Confirm your email");
      helper.setFrom("hello@amigoscode.com");
      helper.setText(emailBody, true);
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      log.error("failed to send email", e);
      throw new IllegalStateException("failed to send email");
    }
  }
}
