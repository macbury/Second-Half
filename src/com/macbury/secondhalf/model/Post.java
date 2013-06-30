package com.macbury.secondhalf.model;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "posts")
public class Post implements KryoSerializable {
  private String body;
  
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public void read(Kryo kryo, Input input) {
    this.body = input.readString();
  }

  @Override
  public void write(Kryo kryo, Output output) {
    output.writeString(this.body);
  }

}
