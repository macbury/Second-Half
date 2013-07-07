package com.macbury.secondhalf.service;

import com.macbury.secondhalf.p2p.Action;
import com.macbury.secondhalf.p2p.NodeTransformer;
import com.macbury.secondhalf.p2p.Response;
import com.macbury.secondhalf.p2p.ShardClient;
import com.macbury.secondhalf.p2p.ShardClient.ShardClientInterface;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class P2PNetworkService extends Service implements ShardClientInterface {
  private static final String TAG   = "P2PNetworkService";

  private ShardClient client;
  
  @Override
  public void onCreate() {
    Log.i(TAG, "Starting service");
    super.onCreate();
    client = new ShardClient(getApplicationContext());
    client.setDelegate(this);
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
    Log.i(TAG, NodeTransformer.nodeToXml(action));
  }

  @Override
  public void onDisconnect() {
    stopSelf();
  }

  @Override
  public void onConnect() {
    Action action = new Action();
    action.setType("auth");
    client.send(action);
  }

  @Override
  public void onResponse(Response response) {
    
  }
}
