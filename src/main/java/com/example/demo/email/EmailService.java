package com.example.demo.email;

import javax.mail.MessagingException;
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

  //TODO: mover estos datos a properties
  private static final String ENCODING = "utf-8";
  private static final String EMAIL_SUBJECT = "Confirm your email";
  private static final String EMAIL_FROM_ADRESS = "hello@amigoscode.com";
  private static final String SEND_EMAIL_ERROR_MESSAGE = "Failed to send email";
  private final JavaMailSender mailSender;

  @Override
  @Async
  public void send(final String receiverAddress, final String emailBody) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(mimeMessage, ENCODING);
      helper.setTo(receiverAddress);
      helper.setSubject(EMAIL_SUBJECT);
      helper.setFrom(EMAIL_FROM_ADRESS);
      helper.setText(emailBody, true);
      mailSender.send(mimeMessage);
    } catch (MessagingException messagingException) {
      log.error(SEND_EMAIL_ERROR_MESSAGE, messagingException);
      throw new IllegalStateException(SEND_EMAIL_ERROR_MESSAGE);
    }
  }
}
