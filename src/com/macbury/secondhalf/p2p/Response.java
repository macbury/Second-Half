package com.macbury.secondhalf.p2p;

public class Response extends Node {
  public static final String TAG = "response";
  public static final String IN_RELATIONSHIP_ATTR = "in-relationship";
  private String forType;
  private String status;
  
  public Response() {
    super("response");
  }

  public String getForType() {
    return forType;
  }

  public void setForType(String forType) {
    this.forType = forType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
  public boolean isSuccess() {
    return "SUCCESS".equals(status);
  }
  
  public boolean isInvalidPassword() {
    return "INVALID_PASSWORD_ERROR".equals(status);
  }

  public boolean isResponseForAction(Action tokenAuthAction) {
    return (tokenAuthAction.getId().equals(this.getId()) && tokenAuthAction.getType().equals(this.getForType()));
  }
}
