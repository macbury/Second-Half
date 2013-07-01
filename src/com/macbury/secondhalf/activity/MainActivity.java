package com.macbury.secondhalf.activity;

import com.macbury.secondhalf.App;
import com.macbury.secondhalf.R;
import com.macbury.secondhalf.service.P2PNetworkService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    startService(new Intent(this, P2PNetworkService.class));
    App.shared().getAccountManager().authUnlessLoggedIn(this);
  }
}
