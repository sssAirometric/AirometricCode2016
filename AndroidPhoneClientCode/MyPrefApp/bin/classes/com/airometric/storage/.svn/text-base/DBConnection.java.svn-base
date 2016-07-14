package com.airometric.storage;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.airometric.AppActivity;
import com.airometric.config.Constants;
import com.airometric.utility.L;

public class DBConnection extends SQLiteOpenHelper {

	private static final String TAG = "DBConnection";

	private static String DB_PATH = "";

	private static final String DB_NAME = Constants.APP_NAME;

	public static final String LIST_TABLE = "tblList";

	private static final String CREATE_LIST_TABLE = "create table IF NOT EXISTS "
			+ LIST_TABLE
			+ " (ID integer primary key autoincrement, Name text);";

	private SQLiteDatabase myDataBase;

	private AppActivity myContext;
	private Context context;

	public String errorMsg;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DBConnection(AppActivity context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;

		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	}

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DBConnection(Context context) {

		super(context, DB_NAME, null, 1);
		this.context = context;

		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (!dbExist) {
			this.getReadableDatabase();
			try {
				createNewDataBase();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Error("Error creating database");
			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		try {
			String myPath = DB_PATH + DB_NAME;
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
			if (!isAllTableAvailable())
				createTables();
		} catch (SQLiteException e) {
		} catch (Exception ee) {
			Log.i("Log", "Exception while checkDatabase()...");
		}
		if (myDataBase != null) {
			myDataBase.close();
		}

		return myDataBase != null ? true : false;
	}

	private boolean isAllTableAvailable() {
		boolean isAllTableExists = false;
		String sTableName = "'" + CREATE_LIST_TABLE + "'";
		boolean exists = isTableExists(sTableName, false);
		L.debug("sTableName --> " + sTableName + "; All tables exists ? "
				+ exists);
		if (exists)
			isAllTableExists = true;
		return isAllTableExists;
	}

	public boolean isTableExists(String tableName, boolean openDb) {
		if (openDb) {
			if (myDataBase == null || !myDataBase.isOpen()) {
				myDataBase = getReadableDatabase();
			}

			if (!myDataBase.isReadOnly()) {
				myDataBase.close();
				myDataBase = getReadableDatabase();
			}
		}
		String sQry = "select count(tbl_name) from sqlite_master where tbl_name IN ("
				+ tableName + ")";
		L.debug("sQry --> " + sQry);
		Cursor cursor = myDataBase.rawQuery(sQry, null);
		if (cursor != null && cursor.moveToNext()) {
			int count = cursor.getInt(0);
			L.debug("count --> " + count);
			if (count == 4)
				return true;
		}
		return false;
	}

	public void createTables() {
		L.debug("Creating neccessary tables....");

		executeUpdate(CREATE_LIST_TABLE);
	}

	void createNewDataBase() {
		try {
			String MY_DATABASE_NAME = DB_NAME; // DB_PATH +
			SQLiteDatabase myDB = null;
			if (myContext != null)
				myDB = this.myContext.openOrCreateDatabase(MY_DATABASE_NAME, 0,
						null);
			else
				myDB = this.context.openOrCreateDatabase(MY_DATABASE_NAME, 0,
						null);
			myDB.close();
			open();
			createTables();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void open() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	public Cursor executeQuery(String sql) throws SQLException {

		L.debug("sql --> " + sql);
		Cursor c = null;
		try {
			this.errorMsg = "";
			c = myDataBase.rawQuery(sql, null);
		} catch (Exception e) {
			this.errorMsg = "" + e.toString();
			e.printStackTrace();
		}

		return c;
	}

	public boolean executeUpdate(String sql) throws SQLException {
		L.debug(TAG + "::executeUpdate()::sql --> " + sql);
		boolean qryExecuted = false;
		try {
			this.errorMsg = "";
			myDataBase.execSQL(sql);
			qryExecuted = true;
		} catch (Exception e) {
			this.errorMsg = "" + e.toString();
			e.printStackTrace();
			qryExecuted = false;
		}
		return qryExecuted;
	}

	public int countRecords(String tblName) {
		return countRecords(tblName, "");
	}

	public int countRecords(String tblName, String creteria) {
		String sCountQuery = "SELECT COUNT(*) FROM " + tblName;
		if (creteria != null && creteria.trim().length() > 0) {
			sCountQuery += " WHERE " + creteria;
		}
		int iCount = -1;
		Cursor recordSet = executeQuery(sCountQuery);
		if (recordSet != null) {
			recordSet.moveToNext();
			iCount = recordSet.getInt(0);
		}
		recordSet.close();
		recordSet = null;
		return iCount;
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
