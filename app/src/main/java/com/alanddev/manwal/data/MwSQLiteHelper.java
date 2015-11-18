package com.alanddev.manwal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MwSQLiteHelper extends SQLiteOpenHelper {

		  public static final String TABLE_WALLET = "wallet";
		  //public static final String COLUMN_ID = "id";
		  public static final String COLUMN_NAME = "name";
		  public static final String COLUMN_AMOUNT= "amount";
	      public static final String COLUMN_CURRENCY= "currency";
		  public static final String DATABASE_NAME = "manwal.db";
		  private static final int DATABASE_VERSION = 1;

		  // 20 fields
		  // Database creation sql statement
		  private static final String DATABASE_CREATE = "CREATE TABLE "
		      + TABLE_WALLET + "("
		      + COLUMN_NAME + " text not null, "
		      + COLUMN_AMOUNT + " real not null, "
			  + COLUMN_CURRENCY + " text not null, "
		      + "PRIMARY KEY (" + COLUMN_NAME + ") "
		      + ");";
		

		  public MwSQLiteHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		  }

		  @Override
		  public void onCreate(SQLiteDatabase database) {
		    database.execSQL(DATABASE_CREATE);
		  }

		  @Override
		  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    /*Log.w(MySQLiteHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");*/
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
		    onCreate(db);
		  }

		} 

