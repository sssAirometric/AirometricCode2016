package com.airometric.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBUtil extends SQLiteOpenHelper {

	private static final String Database_Name = "Airometric.db";
	private static final String Table_name = "AirometricTestdata";
	
	public DBUtil(Context context) {
		super(context, Database_Name, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		 String query1,query2,query3;
		    query1 = "CREATE TABLE " + Table_name +  "( TestID INTEGER PRIMARY KEY AUTOINCREMENT, fname TEXT, lname TEXT, FathersName TEXT, village TEXT, contactNo TEXT, gender BOOLEAN, stream TEXT, regStatus BOOLEAN, imagePath TEXT,Reg_By TEXT,TwelfthRollNo TEXT,Caste TEXT,Remark TEXT,timestamp TEXT)";
		    System.out.println(query1);
		    database.execSQL(query1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 String query;
		    query = "DROP TABLE IF EXISTS "+Table_name+"";
		    database.execSQL(query);
		    onCreate(database);
	}
}
