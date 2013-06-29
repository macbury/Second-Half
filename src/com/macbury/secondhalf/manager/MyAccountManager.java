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
import android.util.Log;

import com.j256.ormlite.stmt.query.In;
import com.macbury.secondhalf.activity.LoginActivity;

public class MyAccountManager implements AccountManagerCallback<Bundle> {
  private static final String TAG = "MyAccountManager";
  private Context context;
  
  public MyAccountManager(Context context) {
    this.context = context;
  }
  
  public void authUnlessLoggedIn(Context c) {
    AccountManager am = AccountManager.get(c);
    Account account = getCurrentAccount();
    
    if (account == null) {
      //am.invalidateAuthToken(AccountAuthenticator.ACCOUNT_TYPE, ); TODO: Save token here
      Intent intent = new Intent(c, LoginActivity.class);
      intent.putExtra(LoginActivity.EXTRA_RETURN_TO_MAIN_ACTIVITY, true);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK);
      c.startActivity(intent);
    } else {
      am.getAuthToken(account, AccountAuthenticatorManager.ACCOUNT_TYPE, null, true, this, null);
    }
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
  
  public boolean haveConnectedAccount() {
    return getCurrentAccount() != null;
  }
  
  @Override
  public void run(AccountManagerFuture<Bundle> future) {
    try {
      String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
      if (token != null) {
        Log.i(TAG, "Token is: "+ token);
      } else {
        Log.i(TAG, "Token is null!");
      }
    } catch (OperationCanceledException e) {
      Log.i(TAG, "Account have been canceled!");
    } catch (AuthenticatorException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
