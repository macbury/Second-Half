package com.macbury.secondhalf.p2p;

import java.util.HashMap;
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
  
  public static Action buildActionForRegisterOrLogin(String email, String password, String device, String signingKey, String encryptionKey) {
    Action action = new Action();
    action.setType("register-or-login");
    action.addParam("email", email);
    action.addParam("password", password);
    action.addParam("device", device);
    action.addParam("signing-key", signingKey);
    action.addParam("encryption-key", encryptionKey);
    return action;
  }

  
}
