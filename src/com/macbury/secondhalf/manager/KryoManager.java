package com.macbury.secondhalf.manager;

import com.esotericsoftware.kryo.Kryo;
import com.macbury.secondhalf.model.Post;

public class KryoManager extends Kryo {
  public KryoManager() {
    super();
    this.register(Post.class);
  }
}
