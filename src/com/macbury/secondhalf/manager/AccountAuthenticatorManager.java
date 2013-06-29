package com.macbury.secondhalf.manager;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.macbury.secondhalf.App;
import com.macbury.secondhalf.activity.LoginActivity;

public class AccountAuthenticatorManager extends AbstractAccountAuthenticator {
  public static final String ACCOUNT_TYPE = "com.macbury.secondhalf";
  private static final String TAG = "AccountAuthenticatorManager";
  private Context mContext;
  
  public AccountAuthenticatorManager(Context context) {
    super(context);
    mContext = context;
  }

  @Override
  public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
    Log.d(TAG, "addAccount");
    
    if (App.shared().getAccountManager().haveConnectedAccount()) {
      //Toast.makeText(mContext, "You can only bind one account!", Toast.LENGTH_SHORT).show();
      return null;
    } else {
      Intent intent = new Intent(this.mContext, LoginActivity.class);
      intent.putExtra(LoginActivity.EXTRA_AUTH_TOKEN_TYPE, authTokenType);
      intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, authTokenType);
      
      Bundle result = new Bundle();
      result.putParcelable(AccountManager.KEY_INTENT, intent);
      
      return result;
    }
  }

  @Override
  public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
    Log.d(TAG, "confirmCredentials");
    return null;
  }

  @Override
  public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
    Log.d(TAG, "editProperties");
    return null;
  }

  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
    
    Bundle result = new Bundle();
    
    AccountManager am = AccountManager.get(mContext);
    String username = account.name;
    String password = am.getPassword(account);
    Log.d("Auth5000", password);
    
    result.putString(AccountManager.KEY_AUTHTOKEN, "ABCD");
    result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
    result.putString(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
    
    return result;
  }

  @Override
  public String getAuthTokenLabel(String authTokenType) {
    Log.d(TAG, "getAuthTokenLabel");
    return null;
  }

  @Override
  public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
    Log.d(TAG, "hasFeatures");
    return null;
  }

  @Override
  public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
    Log.d(TAG, "updateCredentials");
    return null;
  }
}
