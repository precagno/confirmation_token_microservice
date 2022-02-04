package com.example.demo.registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

  @Override
  public boolean test(final String s) {
    //TODO: regex to validate
    return true;
  }
}
