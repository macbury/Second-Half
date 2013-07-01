package com.macbury.secondhalf;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.macbury.secondhalf.manager.EncryptionManager;
import com.macbury.secondhalf.manager.DatabaseManager;
import com.macbury.secondhalf.manager.MyAccountManager;
import com.macbury.secondhalf.model.Post;
import com.macbury.secondhalf.p2p.Shard;
import com.macbury.secondhalf.service.P2PNetworkService;

public class App extends Application {
  public static final String API_VERSION = "0.0.1";
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
    post.setBody("Lorêm ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ornare commodo nibh, sed accumsan massa mollis sit amet. Vivamus iaculis tristique quam, sed tempus ipsum varius sed. Sed congue ac turpis sed ornare. Morbi eu rhoncus urna. Cras egestas mollis rutrum. Nunc rhoncus placerat enim vel commodo. Integer viverra diam tempor feugiat iaculis. Vivamus turpis ipsum, suscipit a imperdiet id, fermentum sed mi. Fusce dictum orci arcu, pharetra sollicitudin libero gravida a. Duis hendrerit scelerisque suscipit. Sed egestas arcu eget tortor tempus dictum id in sem. Donec sit amet adipiscing magna.");
    Shard shard = keyManager.encrypt(post);
    Log.i(TAG, "Encrypted shard " + shard.size() + ": " + new String(shard.getContentBytes()));
    //Log.i(TAG, "Debug: " + shard.toBase64());
    post = (Post) keyManager.decrypt(shard);
    Log.i(TAG, "Descypted shard: " + post.getBody());
    
    shard.setSignatureBytes(new byte[] { 0,1 });
    post = (Post) keyManager.decrypt(shard);
    
    if (post == null) {
      Log.i(TAG, "Post have invalid signature!");
    } else {
      Log.i(TAG, "This should not happen!");
    }
    
    Log.i(TAG, "Base64 pubKey: "+ keyManager.getBase64PublicKey());
    
  }
  
  
}
