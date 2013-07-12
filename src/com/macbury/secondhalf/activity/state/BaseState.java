package com.macbury.secondhalf.activity.state;

import com.macbury.secondhalf.p2p.ShardClient.ShardClientInterface;

public abstract class BaseState implements ShardClientInterface {
  public abstract void onStateEnter();
  public abstract void onStateExit();
  
  
}
