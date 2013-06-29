package com.macbury.secondhalf.activity;

import com.macbury.secondhalf.App;
import com.macbury.secondhalf.R;

import android.os.Bundle;
import android.app.Activity;
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
    App.shared().getAccountManager().authUnlessLoggedIn(this);
  }

  
}
