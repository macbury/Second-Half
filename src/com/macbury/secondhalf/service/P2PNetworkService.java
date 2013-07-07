package com.macbury.secondhalf.service;

import com.macbury.secondhalf.App;
import com.macbury.secondhalf.R;
import com.macbury.secondhalf.manager.DatabaseManager;
import com.macbury.secondhalf.model.User;
import com.macbury.secondhalf.p2p.Action;
import com.macbury.secondhalf.p2p.NodeTransformer;
import com.macbury.secondhalf.p2p.Response;
import com.macbury.secondhalf.p2p.ShardClient;
import com.macbury.secondhalf.p2p.ShardClient.ShardClientInterface;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class P2PNetworkService extends Service implements ShardClientInterface {
  private static final String TAG   = "P2PNetworkService";
  private static final int NOTIFICATION_LOGIN_ID = 1;
  private ShardClient client;
  private Action tokenAuthAction;
  
  @Override
  public void onCreate() {
    Log.i(TAG, "Starting service");
    super.onCreate();
    client = new ShardClient(getApplicationContext());
    client.setDelegate(this);
    
    String token    = App.shared().getAccountManager().getAuthToken();
    if (token == null || token.length() <= 3) {
      throw new RuntimeException("No auth token avalible!");
    }
    tokenAuthAction = Action.buildAuthAction(token);
    
    client.connect();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "Stopping service");
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }

  @Override
  public void onAction(Action action) {
    //Log.i(TAG, NodeTransformer.nodeToXml(action));
  }

  @Override
  public void onDisconnect() {
    stopSelf();
  }

  @Override
  public void onConnect() {
    client.send(tokenAuthAction);
  }

  @Override
  public void onResponse(Response response) {
    if (response.isResponseForAction(tokenAuthAction)) {
      if (response.isSuccess()) {
        DatabaseManager db = App.shared().getDatabaseManager();
        User user          = db.findUserOrInitializeByName(response.getParam("email"));
        db.saveUser(user);
      } else {
        Log.i(TAG, "Invalid token");
        showLoginNotification();
        client.disconnect();
      }
    }
  }

  private void showLoginNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(R.drawable.ic_launcher)
      .setContentTitle(getString(R.string.notification_login_error_title))
      .setContentText(getString(R.string.notification_login_error_summary))
      .setAutoCancel(true);
 
    PendingIntent pi = PendingIntent.getActivity(this, 0, App.shared().getAccountManager().getLoginIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pi);
    
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(NOTIFICATION_LOGIN_ID, builder.build());
  }
}
