package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRole;
import com.example.demo.appuser.AppUserService;
import com.example.demo.email.EmailSender;
import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import com.example.demo.util.DateUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

  private static final String CONFIRMATION_TOKEN_NOT_FOUND_ERROR_MESSAGE = "No confirmation token was found with value %s";
  private static final String EMAIL_NOT_VALID_ERROR_MESSAGE = "Email %s is not valid";
  private static final String EMAIL_ALREADY_CONFIRMED_ERROR_MESSAGE = "Email %s was already confirmed";
  private static final String TOKEN_EXPIRED_ERROR_MESSAGE = "Token %s is already expired";
  private static final String CONFIRMED_OK_MESSAGE = "Email %s confirmed OK";
  private static final String CONFIRMATION_LINK = "http://localhost:8080/api/v1/registration/confirm?token=%s";

  private final AppUserService appUserService;
  private final ConfirmationTokenService confirmationTokenService;
  private final EmailSender emailSender;
  private final EmailValidator emailValidator;


  public String registerUser(final RegistrationRequest request) {
    String email = request.getEmail();
    if (isEmailNotValid(email)) {
      String emailNotValidErrorMessage = String.format(EMAIL_NOT_VALID_ERROR_MESSAGE, email);
      log.error(emailNotValidErrorMessage);
      throw new IllegalStateException(emailNotValidErrorMessage);
    }
    AppUser appUserToRegister = buildAppUserForRegistrationProcess(request);
    String generatedToken = appUserService.signUpUser(appUserToRegister);
    String confirmationLink = String.format(CONFIRMATION_LINK, generatedToken);

    emailSender.send(appUserToRegister.getEmail(),
        buildEmail(appUserToRegister.getFirstName(), confirmationLink));

    return generatedToken;
  }

  @Transactional
  public void confirmToken(final String token) {

    ConfirmationToken confirmationTokenFound = confirmationTokenService.getToken(token).orElseThrow(
        () -> new IllegalStateException(
            String.format(CONFIRMATION_TOKEN_NOT_FOUND_ERROR_MESSAGE, token)));

    String confirmationTokenAppUserEmail = confirmationTokenFound.getAppUser().getEmail();

    if (Objects.nonNull(confirmationTokenFound.getConfirmedAt())) {
      String emailAlreadyConfirmedErrorMessage = String
          .format(EMAIL_ALREADY_CONFIRMED_ERROR_MESSAGE, confirmationTokenAppUserEmail);
      log.error(emailAlreadyConfirmedErrorMessage);
      throw new IllegalStateException(emailAlreadyConfirmedErrorMessage);
    }

    LocalDateTime expiredAt = confirmationTokenFound.getExpiresAt();

    if (expiredAt.isBefore(DateUtil.getTimeNow())) {
      String confirmationTokenExpiredErrorMessage = String
          .format(TOKEN_EXPIRED_ERROR_MESSAGE, token);
      log.error(confirmationTokenExpiredErrorMessage);
      throw new IllegalStateException(confirmationTokenExpiredErrorMessage);
    }

    confirmationTokenService.setConfirmedAt(token);
    appUserService.enableAppUser(confirmationTokenAppUserEmail);
    log.info(String.format(CONFIRMED_OK_MESSAGE, confirmationTokenAppUserEmail));
  }

  private boolean isEmailNotValid(final String email) {
    return Boolean.FALSE.equals(emailValidator.test(email));
  }

  private AppUser buildAppUserForRegistrationProcess(final RegistrationRequest request) {
    return new AppUser(
        request.getFirstName(),
        request.getLastName(),
        request.getEmail(),
        request.getPassword(),
        AppUserRole.USER
    );
  }

  private String buildEmail(final String name, final String link) {
    return
        "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
            +
            "\n" +
            "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
            "\n" +
            "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
            +
            "    <tbody><tr>\n" +
            "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
            "        \n" +
            "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
            +
            "          <tbody><tr>\n" +
            "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
            "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
            +
            "                  <tbody><tr>\n" +
            "                    <td style=\"padding-left:10px\">\n" +
            "                  \n" +
            "                    </td>\n" +
            "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
            +
            "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n"
            +
            "                    </td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "              </a>\n" +
            "            </td>\n" +
            "          </tr>\n" +
            "        </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
            +
            "    <tbody><tr>\n" +
            "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
            "      <td>\n" +
            "        \n" +
            "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
            +
            "                  <tbody><tr>\n" +
            "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
            "                  </tr>\n" +
            "                </tbody></table>\n" +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
            "    </tr>\n" +
            "  </tbody></table>\n" +
            "\n" +
            "\n" +
            "\n" +
            "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
            +
            "    <tbody><tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
            +
            "        \n" +
            "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi "
            + name
            + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""
            + link
            + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>"
            +
            "        \n" +
            "      </td>\n" +
            "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td height=\"30\"><br></td>\n" +
            "    </tr>\n" +
            "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
            "\n" +
            "</div></div>";
  }
}
