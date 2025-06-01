package com.artist.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class SkillNameAlreadyExistsException extends RuntimeException {
  public SkillNameAlreadyExistsException(String message) {
    super(message);
  }
}