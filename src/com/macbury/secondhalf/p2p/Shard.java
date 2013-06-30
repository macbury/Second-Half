package com.macbury.secondhalf.p2p;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.macbury.secondhalf.model.Peer;

public class Shard implements KryoSerializable {
  private Peer peer;
  private byte[] contentBytes;
  
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
  }

  @Override
  public void write(Kryo kryo, Output output) {
    output.write(contentBytes.length);
    output.write(contentBytes);
  }
  
}
