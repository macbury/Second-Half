package com.macbury.secondhalf.model;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "posts")
public class Post {
  private String body;
  
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
