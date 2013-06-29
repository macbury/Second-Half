package com.macbury.secondhalf.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
  @DatabaseField(generatedId=true)
  private int    id;
  @DatabaseField(canBeNull=false)
  private String name;
  @DatabaseField(canBeNull=false)
  private String token;
}
