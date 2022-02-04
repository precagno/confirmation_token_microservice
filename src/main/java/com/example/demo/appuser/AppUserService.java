package com.example.demo.appuser;

import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

  private static final String USER_NOT_FOUND_MESSAGE = "User with email %s was not found";
  private static final String USER_ALREADY_EXISTS_MESSAGE = "User with email %s already exists";
  private final AppUserRepository appUserRepository;
  private final ConfirmationTokenService confirmationTokenService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return appUserRepository.findByEmail(email).orElseThrow(
        () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, email)));
  }

  public String signUpUser(AppUser appUser) {
    String userEmail = appUser.getEmail();
    Optional<AppUser> userExists = appUserRepository.findByEmail(userEmail);

    if (userExists.isPresent()) {

      // TODO: check same attributes
      // TODO: if email not confirmed send confirmation email

      throw new IllegalStateException(
          String.format(USER_ALREADY_EXISTS_MESSAGE, userEmail));
    }

    String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

    appUser.setPassword(encodedPassword);

    appUserRepository.save(appUser);

    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = buildConfirmationToken(token, appUser);

    confirmationTokenService.saveConfirmationToken(confirmationToken);

    //TODO: send email

    return token;
  }

  public void enableAppUser(String email) {
    appUserRepository.enableAppUser(email);
  }

  private ConfirmationToken buildConfirmationToken(String token, AppUser appUser) {
    return new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15),
        appUser);
  }
}
