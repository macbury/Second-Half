package com.macbury.secondhalf.service;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.macbury.secondhalf.Utils;
import com.macbury.secondhalf.manager.KryoManager;
import com.macbury.secondhalf.model.Peer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class P2PNetworkService extends Service {
  private static final int PORT_TCP = 54555;
  private static final int PORT_UDP = 54777;
  private static final String TAG   = "P2PNetworkService";
  static final String WIFILOCK      = "OPTION_PERM_WIFILOCK";
  
  private WifiManager.WifiLock wifilock;
  private Server mServer;
  private ArrayList<Peer> mPeers;
  
  @Override
  public void onCreate() {
    Log.i(TAG, "Starting service");
    super.onCreate();
    acquireWifiLock(this);
    
    //mPeers = new ArrayList<Client>();
    
    initServer();
  }

  private void initServer() {
    mServer = new Server();
    mServer.addListener(mServerListener);
    KryoManager.bootstrap(mServer.getKryo());
    mServer.start();
    try {
      mServer.bind(PORT_TCP, PORT_UDP);
    } catch (IOException e) {
      throw new RuntimeException();
    }
    
    Log.i(TAG, "Current device ip: " + Utils.getIPAddress(true));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "Stopping service");
    mServer.removeListener(mServerListener);
    mServer.stop();
    releaseWifilock();
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }
  
  public void acquireWifiLock(Context ctx) {
    WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    releaseWifilock();
    wifilock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, WIFILOCK);
    wifilock.setReferenceCounted(true);
    wifilock.acquire();
    Log.d(TAG, "WifiLock " + WIFILOCK + " aquired (FULL_MODE)");
    Log.d(TAG, "Checking if Wifilock is held:" + wifilock.isHeld()); 
  }
  
  public void releaseWifilock() {
    Log.d(TAG, "releaseWifilock called");
    if ((wifilock != null) && (wifilock.isHeld()))
    {
      wifilock.release();
      Log.d(TAG, "Wifilock " + WIFILOCK + " released");
    }
  }
  
  public boolean holdsWifiLock() {
    Log.d(TAG, "holdsWifilock called");
    if (wifilock != null) {
      return (wifilock.isHeld());
    }
    return false;
  }
  
  Listener mServerListener = new Listener() {

    @Override
    public void connected(Connection connection) {
      Log.v(TAG, "Server recived new connection");
      super.connected(connection);
    }

    @Override
    public void disconnected(Connection connection) {
      Log.v(TAG, "Client disconnected from server");
      super.disconnected(connection);
    }

    @Override
    public void idle(Connection connection) {
      Log.v(TAG, "Connection idle");
      super.idle(connection);
    }

    @Override
    public void received(Connection connection, Object object) {
      Log.v(TAG, "Recived object in server");
      super.received(connection, object);
    }
    
  };
}
