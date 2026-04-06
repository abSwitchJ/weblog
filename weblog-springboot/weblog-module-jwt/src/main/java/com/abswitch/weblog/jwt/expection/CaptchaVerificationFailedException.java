package com.abswitch.weblog.jwt.expection;

/**
 * @Author：abSwitch
 * @url：
 * @date：2026-04-06 11:52
 * @Description：  
 */ 
    public class CaptchaVerificationFailedException extends RuntimeException {
  public CaptchaVerificationFailedException(String message) {
    super(message);
  }
}
