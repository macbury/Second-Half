package com.macbury.secondhalf.manager;

import com.esotericsoftware.kryo.Kryo;
import com.macbury.secondhalf.model.Post;

public class KryoManager extends Kryo {
  public KryoManager() {
    super();
    KryoManager.bootstrap(this);
  }

  public static void bootstrap(Kryo kryo) {
    kryo.register(Post.class);
  }
}
