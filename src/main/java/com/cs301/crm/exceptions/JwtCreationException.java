package com.cs301.crm.exceptions.handlers;

public class JwtCreationException extends RuntimeException {
  public JwtCreationException(String message) {
    super(message);
  }
}
