package com.macbury.secondhalf.manager;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.stmt.query.In;
import com.macbury.secondhalf.App;
import com.macbury.secondhalf.activity.LoginActivity;
import com.macbury.secondhalf.model.User;

public class MyAccountManager implements AccountManagerCallback<Bundle> {
  private static final String TAG         = "MyAccountManager";
  public static final String KEY_PASSWORD = "KEY_PASSWORD";
  private Context context;
  private String authToken;
  private TokenCallback callback;
  
  public MyAccountManager(Context context, TokenCallback c) {
    this.context      = context;
    this.callback     = c;
    requestAuthToken();
  }
  
  public void requestAuthToken() {
    Log.i(TAG, "requesting auth token");
    Account account   = getCurrentAccount();
    if (account != null) {
      AccountManager am = AccountManager.get(App.shared().getApplicationContext());
      am.getAuthToken(account, AccountAuthenticatorManager.ACCOUNT_TYPE, null, false, this, null);
    }
  }

  public void unRegister() {
    AccountManager am = AccountManager.get(App.shared().getApplicationContext());
    Account account   = getCurrentAccount();
    if (account != null) {
      am.removeAccount(account, null, null);
    }
  }
  
  public void authUnlessLoggedIn(Context c) {
    Account account = getCurrentAccount();
    
    if (account == null) {
      c.startActivity(getLoginIntent());
    } else {
      requestAuthToken();
    }
  }
  
  public Intent getLoginIntent() {
    Intent intent = new Intent(App.shared().getApplicationContext(), LoginActivity.class);
    intent.putExtra(LoginActivity.EXTRA_RETURN_TO_MAIN_ACTIVITY, true);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }

  public String getAuthToken() {
    return authToken;
  }
  
  public Account getCurrentAccount() {
    AccountManager am   = AccountManager.get(context);
    Account[] accounts  = am.getAccountsByType(AccountAuthenticatorManager.ACCOUNT_TYPE);
    
    if (accounts != null && accounts.length > 0) {
      return accounts[0];
    } else {
      return null;
    }
  }
  
  public User getCurrentUser() {
    Account account = getCurrentAccount();
    return App.shared().getDatabaseManager().findUserOrInitializeByName(account.name);
  }
  
  public boolean haveConnectedAccount() {
    return getCurrentAccount() != null;
  }

  @Override
  public void run(AccountManagerFuture<Bundle> future) {
    try {
      Bundle result = future.getResult();
      authToken     = result.getString(AccountManager.KEY_AUTHTOKEN);
      callback.onAuthComplete();
    } catch (OperationCanceledException e) {
      
    } catch (AuthenticatorException e) {
      
    } catch (IOException e) {
      
    }
  }
  
  public interface TokenCallback {
    public void onAuthComplete();
  }
}
