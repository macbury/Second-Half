package com.macbury.secondhalf;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.macbury.secondhalf.manager.EncryptionManager;
import com.macbury.secondhalf.manager.DatabaseManager;
import com.macbury.secondhalf.manager.MyAccountManager;
import com.macbury.secondhalf.model.Post;
import com.macbury.secondhalf.p2p.Shard;

public class App extends Application {
  private static final String TAG = "App";
  private static App _shared;
  private MyAccountManager accountManger;
  private DatabaseManager  databaseManager;
  
  public static App shared() {
    return _shared;
  }
  
  public App() {
    _shared = this;
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
    
    EncryptionManager keyManager = EncryptionManager.generatePrivAndPubKey(this.getApplicationContext());
    Post post                    = new Post();
    post.setBody("Witaj œwiecie!");
    Shard shard = keyManager.encrypt(post);
    Log.i(TAG, "Encrypted shard " + shard.size() + ": " + new String(shard.getContentBytes()));
    
    post = (Post) keyManager.decrypt(shard);
    Log.i(TAG, "Descypted shard: " + post.getBody());
    
    Log.i(TAG, "Base64 pubKey: "+ keyManager.getBase64PublicKey());
  }
  
  
}
