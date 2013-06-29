package com.macbury.secondhalf.manager;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.macbury.secondhalf.model.User;

public class DatabaseManager extends OrmLiteSqliteOpenHelper {
  private static final String DATABASE_NAME   = "sh.db";
  private static final int DATABASE_VERSION   = 1;
  private static final String TAG             = "DatabaseManager";
  private Dao<User, Integer> userDao          = null;
  
  public DatabaseManager(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      Log.i(TAG, "onCreate");
      TableUtils.createTable(connectionSource, User.class);
    } catch (SQLException e) {
      Log.e(TAG, "Can't create database", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    try {
      Log.i(TAG, "onUpgrade");
      TableUtils.dropTable(connectionSource, User.class, true);
      onCreate(db, connectionSource);
    } catch (SQLException e) {
      Log.e(TAG, "Can't drop databases", e);
      throw new RuntimeException(e);
    }
  }
  
  public Dao<User, Integer> getUserDao() {
    if (userDao == null) {
      try {
        userDao = getDao(User.class);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    
    return userDao;
  }

  public void clearData() {
    Log.i(TAG, "Clearing database!");
    try {
      getUserDao().delete(getUserDao().deleteBuilder().prepare());
    } catch (SQLException e) {
      Log.e(TAG, "Can't clear databases", e);
      throw new RuntimeException(e);
    }
  } 
}
