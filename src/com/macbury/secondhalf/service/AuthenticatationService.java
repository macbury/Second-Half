package com.macbury.secondhalf.service;

import com.macbury.secondhalf.manager.AccountAuthenticatorManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatationService extends Service {

  @Override
  public IBinder onBind(Intent arg0) {
    return new AccountAuthenticatorManager(this).getIBinder();
  }

}
