package com.macbury.secondhalf.activity;

import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.macbury.secondhalf.R;
import com.macbury.secondhalf.R.id;
import com.macbury.secondhalf.R.layout;
import com.macbury.secondhalf.R.menu;
import com.macbury.secondhalf.R.string;
import com.macbury.secondhalf.manager.AccountAuthenticatorManager;
import com.macbury.secondhalf.manager.ApiManager;
import com.macbury.secondhalf.manager.EncryptionManager;
import com.macbury.secondhalf.manager.MyAccountManager;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AccountAuthenticatorActivity {
  public static final String EXTRA_EMAIL                    = "EXTRA_EMAIL";
  public static final String EXTRA_AUTH_TOKEN_TYPE          = "EXTRA_AUTH_TOKEN_TYPE";
  public static final String EXTRA_RETURN_TO_MAIN_ACTIVITY  = "EXTRA_RETURN_TO_MAIN_ACTIVITY";

  private String mEmail;
  private String mPassword;

  private boolean startedFromMainActivity = false;
  
  private boolean working = false;
  private EditText mEmailView;
  private EditText mPasswordView;
  private View mLoginFormView;
  private View mLoginStatusView;
  private TextView mLoginStatusMessageView;
  private AQuery query;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    query = new AQuery(this);
    setContentView(R.layout.activity_login);

    mEmail          = getIntent().getStringExtra(EXTRA_EMAIL);
    mEmailView      = (EditText) findViewById(R.id.email);
    mEmailView.setText(mEmail);

    mPasswordView = (EditText) findViewById(R.id.password);
    mPasswordView
        .setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView textView, int id,
              KeyEvent keyEvent) {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
              attemptLogin();
              return true;
            }
            return false;
          }
        });

    mLoginFormView = findViewById(R.id.login_form);
    mLoginStatusView = findViewById(R.id.login_status);
    mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

    findViewById(R.id.sign_in_button).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            attemptLogin();
          }
        });
    
    Intent intent = getIntent();
    if (intent != null && intent.getBooleanExtra(EXTRA_RETURN_TO_MAIN_ACTIVITY, false)) {
      startedFromMainActivity = true;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }

  public void attemptLogin() {
    if (working) {
      return;
    }
    mEmailView.setError(null);
    mPasswordView.setError(null);

    mEmail    = mEmailView.getText().toString();
    mPassword = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    if (TextUtils.isEmpty(mPassword)) {
      mPasswordView.setError(getString(R.string.error_field_required));
      focusView = mPasswordView;
      cancel = true;
    } else if (mPassword.length() < 4) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    if (TextUtils.isEmpty(mEmail)) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if (!mEmail.contains("@")) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if (cancel) {
      focusView.requestFocus();
    } else {
      mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
      showProgress(true);
      
      String accountType = this.getIntent().getStringExtra(EXTRA_AUTH_TOKEN_TYPE);
      if (accountType == null) {
        accountType = AccountAuthenticatorManager.ACCOUNT_TYPE;
      }
      
      showProgress(true);
      EncryptionManager eManager = new EncryptionManager(this);
      working = true;
      query.ajax(ApiManager.login(mEmail, mPassword, eManager).handler(this, "onLoginRequestCallback").timeout(10000));
    }
  }
  
  public void onLoginRequestCallback(String url, JSONObject json, AjaxStatus status){
    working = false;
    showProgress(false);
    
    //mPasswordView.setError(getString(R.string.error_incorrect_password));
   // mPasswordView.requestFocus();
    
    /*AccountManager accMgr = AccountManager.get(this);
    Account account = new Account(mEmail, accountType);
    accMgr.addAccountExplicitly(account, mPassword, null);
    Intent intent = new Intent();
    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mEmail);
    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
    intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType); 
    intent.putExtra(MyAccountManager.KEY_PASSWORD, mPassword); 
    //this.setAccountAuthenticatorResult(intent.getExtras());mPassword
    this.setAccountAuthenticatorResult(intent.getExtras());
    this.setResult(RESULT_OK, intent);
    if (startedFromMainActivity) {
      Intent mainIntent = new Intent(this, MainActivity.class);
      mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(mainIntent);
    } else {
      finish();
    }*/
  }
  
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(
          android.R.integer.config_shortAnimTime);

      mLoginStatusView.setVisibility(View.VISIBLE);
      mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
          });

      mLoginFormView.setVisibility(View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
          .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
          });
    } else {
      mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

}
