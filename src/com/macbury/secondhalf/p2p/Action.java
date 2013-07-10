package com.macbury.secondhalf.p2p;

import java.util.UUID;

public class Action extends Node {
  static final String ACTION_TAG  = "action";
  private String type;
  
  public Action() {
    super(ACTION_TAG);
    this.setId(UUID.randomUUID().toString());
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public static Action buildActionForRegisterOrLogin(String email, String password, String device, String signingKey, String encryptionKey, String captcha) {
    Action action = new Action();
    action.setType("register-or-login");
    action.addParam("email", email);
    action.addParam("password", password);
    action.addParam("device", device);
    action.addParam("signing-key", signingKey);
    action.addParam("encryption-key", encryptionKey);
    action.addParam("captcha", captcha);
    return action;
  }

  public static Action buildAuthAction(String token) {
    Action action = new Action();
    action.setType("auth");
    action.addParam("token", token);
    return action;
  }
  
  public static Action buildCaptchaAction() {
    Action action = new Action();
    action.setType("captcha");
    return action;
  }

  public static String buildPing() {
    return "<p/>";
  }
  
}
