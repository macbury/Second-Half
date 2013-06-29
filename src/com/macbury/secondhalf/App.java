package com.macbury.secondhalf;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.macbury.secondhalf.manager.DatabaseManager;
import com.macbury.secondhalf.manager.MyAccountManager;

public class App extends Application {
  private static final String TAG = "App";
  private static App _shared;
  private MyAccountManager accountManger;
  private DatabaseManager  databaseManager;
  
  public static App shared() {
    return _shared;
  }
  
  public App() {
    _shared         = this;
  }
  
  public MyAccountManager getAccountManager() {
    return accountManger;
  }
  
  public DatabaseManager getDatabaseManager() {
    return databaseManager;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "App starting");
    Context context = getApplicationContext();
    accountManger   = new MyAccountManager(context);
    databaseManager = new DatabaseManager(context);
    
    if(!accountManger.haveConnectedAccount()) {
      databaseManager.clearData();
    }
  }
  
  
}
