package com.macbury.secondhalf.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.macbury.secondhalf.model.Peer;
import com.macbury.secondhalf.p2p.Shard;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class EncryptionManager {
  private static final String TAG                  = "EncryptionManager";
  private static final String PUBLIC_KEY_NAME      = "id_rsa.pub";
  private static final String PRIVATE_KEY_NAME     = "id_rsa.priv";
  private static final int RSA_KEY_SIZE            = 1024;
  private static final String ENCRYPTION_ALGORITHM = "RSA";
  
  private PublicKey publicKey   = null;
  private PrivateKey privateKey = null;
  private Context mContext;
  private Peer peer;
  
  public EncryptionManager(Context context, Peer peer) {
    this.mContext = context;
    this.peer     = peer;
    try {
      KeyFactory keyFactory              = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
      byte[] encodedPublicKey            = peer.getPublicKey();
      X509EncodedKeySpec publicKeySpec   = new X509EncodedKeySpec(encodedPublicKey);
      publicKey                          = keyFactory.generatePublic(publicKeySpec);
      
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
      byte[] encodedPublicKey  = loadKey(PUBLIC_KEY_NAME);
      byte[] encodedPrivateKey = loadKey(PRIVATE_KEY_NAME);
      
      X509EncodedKeySpec publicKeySpec   = new X509EncodedKeySpec(encodedPublicKey);
      PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
      
      publicKey                          = keyFactory.generatePublic(publicKeySpec);
      privateKey                         = keyFactory.generatePrivate(privateKeySpec);
      //dumpKeyPair(publicKey, privateKey);
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
    Kryo kryo     = new KryoManager();
    
    Cipher cipher;
    try {
      cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] cipherData = cipher.doFinal(shard.getContentBytes());
      Input input = new Input(cipherData);
      Class klass = kryo.readClass(input).getType();
      return kryo.readObject(input, klass);
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
    }
  }
  
  public Shard encrypt(KryoSerializable object) {
    Kryo kryo     = new KryoManager();
    Output output = new Output(4096);
    kryo.writeClass(output, object.getClass());
    kryo.writeObject(output, object);
    
    Cipher cipher;
    try {
      cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] cipherData = cipher.doFinal(output.toBytes());
      
      Shard shard = new Shard();
      shard.setContentBytes(cipherData);
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
    }
  }
  
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
      saveX509Key(context, PUBLIC_KEY_NAME, pair.getPublic().getEncoded());
      savePKCS8Key(context, PRIVATE_KEY_NAME, pair.getPrivate().getEncoded());
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
  
  public String getBase64PublicKey() {
    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
    return Base64.encodeToString(x509EncodedKeySpec.getEncoded(), Base64.DEFAULT);
  }
}
