package com.macbury.secondhalf.service;

import java.io.IOException;

import com.macbury.secondhalf.App;
import com.macbury.secondhalf.R;
import com.macbury.secondhalf.activity.InviteActivity;
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
import android.widget.Toast;

public class P2PNetworkService extends Service implements ShardClientInterface {
  private static final String TAG                       = "P2PNetworkService";
  private static final int NOTIFICATION_LOGIN_ID        = 1;
  private static final int NOTIFICATION_REGISTRAION_ID  = 2;
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
  public void onConnect() {
    client.send(tokenAuthAction);
  }

  @Override
  public void onResponse(Response response) {
    if (response.isResponseForAction(tokenAuthAction)) {
      if (response.isSuccess()) {
        DatabaseManager db = App.shared().getDatabaseManager();
        User user          = db.findUserOrInitializeByName(response.getParam("email"));
        user.setInRelationShip(response.getParam(Response.IN_RELATIONSHIP_ATTR) == "true");
        db.saveUser(user);
        showRequestInviteNotification();
      } else {
        Log.i(TAG, "Invalid token");
        showLoginNotification();
      }
      
      client.disconnect();
    }
  }

  private void showRequestInviteNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(R.drawable.ic_launcher)
      .setContentTitle(getString(R.string.notification_relationship_error_title))
      .setContentText(getString(R.string.notification_relationship_error_summary))
      .setAutoCancel(true);
    
    PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, InviteActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pi);
    
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(NOTIFICATION_REGISTRAION_ID, builder.build());
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

  @Override
  public void onConnectionError(IOException e) {
    Log.e(TAG, "Connection error", e);
    Toast.makeText(this, R.string.error_connection, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onDisconnect(boolean haveError) {
    stopSelf();
  }
}
