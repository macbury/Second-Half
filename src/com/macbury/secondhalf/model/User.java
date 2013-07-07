package com.macbury.secondhalf.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User{
  @DatabaseField(generatedId=true)
  private int    id;
  @DatabaseField(canBeNull=false)
  private String name;
  @DatabaseField(canBeNull=false)
  private String token;
  @DatabaseField(canBeNull=false)
  private String versionToken;
  
  @ForeignCollectionField(eager=true)
  private ForeignCollection<Peer> peers;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getVersionToken() {
    return versionToken;
  }

  public void setVersionToken(String versionToken) {
    this.versionToken = versionToken;
  }

  public ForeignCollection<Peer> getPeers() {
    return peers;
  }

  public void setPeers(ForeignCollection<Peer> peers) {
    this.peers = peers;
  }

}
