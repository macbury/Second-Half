package com.macbury.secondhalf.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.macbury.secondhalf.App;
import com.macbury.secondhalf.model.Peer;
import com.macbury.secondhalf.p2p.Shard;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

public class EncryptionManager {
  private static final String TAG                             = "EncryptionManager";
  private static final String ENCRYPTION_PUBLIC_KEY_NAME      = "encryption.pub";
  private static final String ENCRYPTION_PRIVATE_KEY_NAME     = "encryption.priv";
  private static final String SIGNING_PUBLIC_KEY_NAME         = "signing.pub";
  private static final String SIGNING_PRIVATE_KEY_NAME        = "signing.priv";
  private static final int RSA_KEY_SIZE                       = 1024;
  private static final String ENCRYPTION_ALGORITHM            = "RSA";
  
  private PublicKey encryptionPublicKey   = null;
  private PrivateKey encryptionPrivateKey = null;
  private PublicKey signingPublicKey      = null;
  private PrivateKey signingPrivateKey    = null;
  private Signature signature   = null;
  private Context mContext;
  private Peer peer;
  
  public EncryptionManager(Context context, Peer peer) {
    this.mContext = context;
    this.peer     = peer;
    try {
      KeyFactory keyFactory              = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
      byte[] encodedPublicKey            = peer.getEncryptionKey();
      X509EncodedKeySpec publicKeySpec   = new X509EncodedKeySpec(encodedPublicKey);
      encryptionPublicKey                = keyFactory.generatePublic(publicKeySpec);
      throw new RuntimeException("Need reimplement this to use signing key");
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  
  public EncryptionManager(Context context) {
    this.mContext = context;
    
    try {
      KeyFactory keyFactory    = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
      signature                = Signature.getInstance("SHA1withRSA");
      byte[] encodedPublicKey  = loadKey(ENCRYPTION_PUBLIC_KEY_NAME);
      byte[] encodedPrivateKey = loadKey(ENCRYPTION_PRIVATE_KEY_NAME);
      
      X509EncodedKeySpec publicKeySpec   = new X509EncodedKeySpec(encodedPublicKey);
      PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
      
      encryptionPublicKey                = keyFactory.generatePublic(publicKeySpec);
      encryptionPrivateKey               = keyFactory.generatePrivate(privateKeySpec);
      
      encodedPublicKey                   = loadKey(SIGNING_PUBLIC_KEY_NAME);
      encodedPrivateKey                  = loadKey(SIGNING_PRIVATE_KEY_NAME);
      
      publicKeySpec                      = new X509EncodedKeySpec(encodedPublicKey);
      privateKeySpec                     = new PKCS8EncodedKeySpec(encodedPrivateKey);
      
      signingPublicKey                   = keyFactory.generatePublic(publicKeySpec);
      signingPrivateKey                  = keyFactory.generatePrivate(privateKeySpec);
      
      //dumpKeyPair(encryptionPublicKey, encryptionPrivateKey);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  
  public byte[] loadKey(String filename) {
    File file = new File(mContext.getFilesDir(), filename);
    Log.i(TAG, "Loading key from "+ file.getAbsolutePath());
    
    try {
      FileInputStream keyfis = new FileInputStream(file);
      byte[] encKey = new byte[keyfis.available()];  
      keyfis.read(encKey);
      keyfis.close();
      return encKey;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public Object decrypt(Shard shard) {
    return shard;
    /*Kryo kryo     = new KryoManager();
    
    Cipher cipher;
    try {
      cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, encryptionPrivateKey);
      byte[] objectBytes = shard.getContentBytes();
      byte[] cipherData  = blockCipher(objectBytes, Cipher.DECRYPT_MODE, cipher);
      
      Log.v(TAG, "Before decryption size: " + objectBytes.length + " after size: " + cipherData.length);
      
      signature.initVerify(signingPublicKey);
      signature.update(cipherData);
      
      if (signature.verify(shard.getSignatureBytes())) {
        Input input = new Input(cipherData);
        Class klass = kryo.readClass(input).getType();
        return kryo.readObject(input, klass);
      } else {
        return null;
      }
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    } catch (BadPaddingException e) {
      throw new RuntimeException(e);
    } catch (SignatureException e) {
      throw new RuntimeException(e);
    }*/
  }
  
  private byte[] blockCipher(byte[] sourceBytes, int mode, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
    int length        = (mode == Cipher.ENCRYPT_MODE) ? 100 : 128;
    int totalSize     = sourceBytes.length;
    byte[] finalBytes = new byte[0];
    byte[] tempBytes  = new byte[0];
    byte[] buffer     = new byte[length];
    
    int offset = 0;
    for (int i=0; i< sourceBytes.length; i++){
      if (i > 0 && (i % length == 0)) {
        tempBytes  = cipher.doFinal(buffer);
        finalBytes = append(finalBytes,tempBytes);
        
        offset     += length;
        int newlength = length;
        
        if (i + length > sourceBytes.length - 1) {
          newlength = sourceBytes.length - 1;
        }
        
        buffer     = new byte[newlength];
      }
      
      buffer[i%length] = sourceBytes[i];
    }
    
    int leftBytes = totalSize - offset;
    if (leftBytes > 0) {
      buffer        = new byte[leftBytes];
      
      int i = 0;
      for (int j = sourceBytes.length - leftBytes; j < sourceBytes.length; j++) {
        buffer[i] = sourceBytes[j];
        i++;
      }
      
      tempBytes  = cipher.doFinal(buffer);
      finalBytes = append(finalBytes, tempBytes);
    }
    
    return finalBytes;
  }
  
  private byte[] append(byte[] prefix, byte[] suffix){
    byte[] toReturn = new byte[prefix.length + suffix.length];
    for (int i=0; i< prefix.length; i++){
      toReturn[i] = prefix[i];
    }
    for (int i=0; i< suffix.length; i++){
      toReturn[i+prefix.length] = suffix[i];
    }
    return toReturn;
  }
  
  /*public Shard encrypt(KryoSerializable object) {
    Kryo kryo     = new KryoManager();
    Output output = new Output(Shard.BUFFER_SIZE);
    kryo.writeClass(output, object.getClass());
    kryo.writeObject(output, object);
    byte[] objectBytes = output.toBytes();
    
    Cipher cipher;
    try {
      signature.initSign(signingPrivateKey);
      signature.update(objectBytes);
      byte[] signatureBytes = signature.sign();
      
      cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, encryptionPublicKey);

      byte[] cipherData = blockCipher(objectBytes, Cipher.ENCRYPT_MODE, cipher);
      
      Log.v(TAG, "Before encryption size: " + objectBytes.length + " after size: " + cipherData.length);
      
      Shard shard = new Shard();
      shard.setContentBytes(cipherData);
      shard.setSignatureBytes(signatureBytes);
      return shard;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    } catch (BadPaddingException e) {
      throw new RuntimeException(e);
    } catch (SignatureException e) {
      throw new RuntimeException(e);
    }
  }*/
  
  public static void saveX509Key(Context context, String filename, byte[] keyContent) {
    File file = new File(context.getFilesDir(), filename);
    try {
      Log.i(TAG, "Storing key in "+ file.getAbsolutePath());
      FileOutputStream outputStream                = new FileOutputStream(file);
      X509EncodedKeySpec x509EncodedKeySpec        = new X509EncodedKeySpec(keyContent);
      outputStream.write(x509EncodedKeySpec.getEncoded());
      outputStream.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static void savePKCS8Key(Context context, String filename, byte[] keyContent) {
    File file = new File(context.getFilesDir(), filename);
    try {
      Log.i(TAG, "Storing key in "+ file.getAbsolutePath());
      FileOutputStream outputStream                = new FileOutputStream(file);
      PKCS8EncodedKeySpec pkcs8EncodedKeySpec      = new PKCS8EncodedKeySpec(keyContent);
      outputStream.write(pkcs8EncodedKeySpec.getEncoded());
      outputStream.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static EncryptionManager generatePrivAndPubKey(Context context) {
    KeyPairGenerator keyGen;
    Log.i(TAG, "Generating public and private key");
    try {
      keyGen                  = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
      SecureRandom random     = SecureRandom.getInstance("SHA1PRNG");
      keyGen.initialize(RSA_KEY_SIZE, random);
      KeyPair pair            = keyGen.generateKeyPair();
      //dumpKeyPair(pair.getPublic(), pair.getPrivate());
      saveX509Key(context, ENCRYPTION_PUBLIC_KEY_NAME, pair.getPublic().getEncoded());
      savePKCS8Key(context, ENCRYPTION_PRIVATE_KEY_NAME, pair.getPrivate().getEncoded());
      
      pair                   = keyGen.generateKeyPair();
      saveX509Key(context, SIGNING_PUBLIC_KEY_NAME, pair.getPublic().getEncoded());
      savePKCS8Key(context, SIGNING_PRIVATE_KEY_NAME, pair.getPrivate().getEncoded());
      return new EncryptionManager(context);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static void dumpKeyPair(PublicKey pub, PrivateKey priv) {
    if (pub != null) {
      Log.d(TAG, "Public Key: " + getHexString(pub.getEncoded()));
    }
    
    if (priv != null) {
      Log.d(TAG, "Private Key: " + getHexString(priv.getEncoded()));
    }
  }

  public static String getHexString(byte[] b) {
    String result = "";
    for (int i = 0; i < b.length; i++) {
      result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
    }
    return result;
  }
  
  public String keyToBase64(PublicKey key) {
    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key.getEncoded());
    return Base64.encodeToString(x509EncodedKeySpec.getEncoded(), Base64.NO_WRAP);
  }
  
  public String getBase64EncryptionPublicKey() {
    return keyToBase64(encryptionPublicKey);
  }
  
  public String getBase64SigningPublicKey() {
    return keyToBase64(signingPublicKey);
  }
  
  public static String getDeviceUID() {
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
      
    hex=Base64.encodeToString(data, 0, data.length, Base64.NO_WRAP);
      
    sb.append(hex);
                  
    return sb.toString();
  }
  
  public static String  computeSHAHash(String password) {
    MessageDigest mdSha1 = null;
    try {
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
