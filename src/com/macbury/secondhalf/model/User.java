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

}
