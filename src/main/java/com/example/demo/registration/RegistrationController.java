package com.example.demo.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "api/v1/registration")
public class RegistrationController {

  private final RegistrationService registrationService;

  @PostMapping
  public String register(@RequestBody RegistrationRequest request) {
    return registrationService.register(request);
  }

  @GetMapping("/confirm")
  public String confirm(@RequestParam("token") String token) {
    return registrationService.confirmToken(token);
  }
}
