package com.macbury.secondhalf.model;

import com.esotericsoftware.kryonet.Client;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.macbury.secondhalf.App;
import com.macbury.secondhalf.manager.EncryptionManager;

@DatabaseTable(tableName = "peers")
public class Peer {
  @DatabaseField(unique=true, id=true)
  private int    id;
  @DatabaseField(canBeNull = false)
  private boolean current   = false;
  @DatabaseField(canBeNull = false, dataType=DataType.BYTE_ARRAY)
  private byte[] encryptionKey;
  @DatabaseField(canBeNull = false, dataType=DataType.BYTE_ARRAY)
  private byte[] signingKey;
  @DatabaseField(canBeNull = false)
  private String name;
  @DatabaseField(canBeNull = false)
  private String ip;
  @DatabaseField(canBeNull = false, foreign = true)
  private User user;
  
  private EncryptionManager encryptionManager;
  private Client            client;
  
  public Client getClient() {
    return client;
  }
  public void setClient(Client client) {
    this.client = client;
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public boolean isCurrent() {
    return current;
  }
  public void setCurrent(boolean current) {
    this.current = current;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getIp() {
    return ip;
  }
  public void setIp(String ip) {
    this.ip = ip;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
  public byte[] getEncryptionKey() {
    return encryptionKey;
  }
  public void setEncryptionKey(byte[] encryptionKey) {
    this.encryptionKey = encryptionKey;
  }
  public byte[] getSigningKey() {
    return signingKey;
  }
  public void setSigningKey(byte[] signingKey) {
    this.signingKey = signingKey;
  }
}
