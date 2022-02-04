package com.example.demo.registration.token;

import com.example.demo.util.DateUtil;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

  private final ConfirmationTokenRepository confirmationTokenRepository;

  public void saveConfirmationToken(final ConfirmationToken confirmationToken) {
    confirmationTokenRepository.save(confirmationToken);
  }

  public Optional<ConfirmationToken> getToken(final String token) {
    return confirmationTokenRepository.findByToken(token);
  }

  public void setConfirmedAt(final String token) {
    confirmationTokenRepository.updateConfirmedAt(token, DateUtil.getTimeNow());
  }
}
