package com.example.demo.registration.token;

import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

  Optional<ConfirmationToken> findByToken(String token);

  @Transactional
  @Modifying
  @Query("UPDATE ConfirmationToken c set c.confirmedAt=:now where c.token=:token")
  int updateConfirmedAt(String token, LocalDateTime now);
}
