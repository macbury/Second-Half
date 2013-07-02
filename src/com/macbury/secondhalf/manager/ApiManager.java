package com.macbury.secondhalf.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.macbury.secondhalf.App;

public class ApiManager {
  private static final String TAG = "ApiManager";
  private static String HOST      = "http://second-half.macbury.pl";
  private static String AGENT     = "SecondHalf Mobile";
  
  public static AjaxCallback<JSONObject> login(String login, String password, EncryptionManager eManager) {
    AjaxCallback<JSONObject> ajax = new AjaxCallback<JSONObject>();
    ajax.setAgent(AGENT);
    ajax.url(HOST + "/api/auth.json");
    ajax.expire(-1);
    ajax.type(JSONObject.class);
    
    HashMap<String, String> params = new HashMap<String, String>();
    String deviceUID = getDeviceUID();
    Log.v(TAG, "Device uid is: "+ deviceUID);
    params.put("login", login);
    params.put("password", password);
    params.put("device", deviceUID);
    params.put("public_key", eManager.getBase64PublicKey());
    
    ajax.params(params);
    return ajax;
  }

  private static String getDeviceUID() {
    TelephonyManager telephonyManager  = (TelephonyManager)App.shared().getSystemService( Context.TELEPHONY_SERVICE );
    WifiManager wm                     = (WifiManager)App.shared().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    String androidID                   = Secure.getString(App.shared().getContentResolver(), Secure.ANDROID_ID);
    String m_szDevIDShort = "666" + 
        Build.BOARD.length()+ Build.BRAND.length() + 
        Build.CPU_ABI.length() + Build.DEVICE.length() + 
        Build.DISPLAY.length() + Build.HOST.length() + 
        Build.ID.length() + Build.MANUFACTURER.length() + 
        Build.MODEL.length() + Build.PRODUCT.length() + 
        Build.TAGS.length() + Build.TYPE.length() + 
        Build.USER.length() ; 
    return computeSHAHash(wm.getConnectionInfo().getMacAddress() + "-" +m_szDevIDShort + "-" + androidID + "-"+telephonyManager.getDeviceId() + "-" + telephonyManager.getSubscriberId());
  }
  
  private static String convertToHex(byte[] data) throws java.io.IOException {
    StringBuffer sb = new StringBuffer();
    String hex=null;
      
    hex=Base64.encodeToString(data, 0, data.length, Base64.DEFAULT);
      
    sb.append(hex);
                  
    return sb.toString();
  }
  
  public static String  computeSHAHash(String password) {
    MessageDigest mdSha1 = null;
      try
      {
        mdSha1 = MessageDigest.getInstance("SHA-1");
      } catch (NoSuchAlgorithmException e1) {
        Log.e(TAG, "Error initializing SHA1 message digest");
      }
      try {
        mdSha1.update(password.getBytes("ASCII"));
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      byte[] data = mdSha1.digest();
      String SHAHash = null;
      try {
        SHAHash=convertToHex(data);
      } catch (IOException e) {
        e.printStackTrace();
      }
         
      return SHAHash;
   }
}
