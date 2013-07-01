package com.macbury.secondhalf.p2p;

import android.util.Base64;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.macbury.secondhalf.App;
import com.macbury.secondhalf.manager.KryoManager;
import com.macbury.secondhalf.model.Peer;

public class Shard implements KryoSerializable {
  public static final int BUFFER_SIZE = 4096;
  private Peer peer;
  private byte[] contentBytes;
  private byte[] signatureBytes;

  public Shard() {
    
  }
  
  public int size() {
    return contentBytes.length;
  }
  
  public byte[] getContentBytes() {
    return contentBytes;
  }

  public void setContentBytes(byte[] contentBytes) {
    this.contentBytes = contentBytes;
  }

  @Override
  public void read(Kryo kryo, Input input) {
    int contentLength = input.read();
    contentBytes      = input.readBytes(contentLength);
    contentLength     = input.read();
    signatureBytes    = input.readBytes(contentLength);
  }

  @Override
  public void write(Kryo kryo, Output output) {
    output.writeString(App.API_VERSION);
    output.write(contentBytes.length);
    output.write(contentBytes);
    output.write(signatureBytes.length);
    output.write(signatureBytes);
  }
  
  public byte[] getSignatureBytes() {
    return signatureBytes;
  }

  public void setSignatureBytes(byte[] signatureBytes) {
    this.signatureBytes = signatureBytes;
  }
  
  public static String getHexString(byte[] b) {
    String result = "";
    for (int i = 0; i < b.length; i++) {
      result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
    }
    return result;
  }
  
  public String toString() {
    return "Shard(signature="+getHexString(signatureBytes)+")";
  }
  
  public String toBase64() {
    KryoManager kryo = new KryoManager();
    Output output    = new Output(Shard.BUFFER_SIZE);
    kryo.writeObject(output, this);
    byte[] objectBytes = output.toBytes();
    
    return Base64.encodeToString(objectBytes, Base64.DEFAULT);
  }
}
