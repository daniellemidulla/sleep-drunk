package edu.dartmouth.cs.SleepDrunk;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * http://developer.android.com/resources/samples/NotePad/src/com/example/android/notepad/NotePadProvider.html
 */
public class ReactHighScoreDatabase {

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + "_id INTEGER PRIMARY KEY autoincrement," + "name TEXT NOT NULL,"
					+ "score INTEGER NOT NULL, bac real,drinks real,sleeptime real" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(getClass().getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

	}

	public  class HighScoreEntry {
		public  int id;
		public  String name;
		public  int score;
		public double bac;
		public double drinks;
        public double sleeptime;
		
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public double getBac() {
			return bac;
		}

		public void setBac(double bac) {
			this.bac = bac;
		}
		
		public double getDrinks() {
			return drinks;
		}

		public void setDrinks(double drinks) {
			this.drinks = drinks;
		}
        public double getSleeptime() {
            return sleeptime;
        }

        public void setSleeptime(double sleeptime) {
            this.sleeptime = sleeptime;
        }
		
	}

	private static final String DATABASE_NAME = "reaction_highscore.db";
	private static final int DATABASE_VERSION = 1;
	public static final int MAX_ENTRIES = 100;
	private static final String TABLE_NAME = "reaction_highscore";

	public static ReactHighScoreDatabase getDatabase(Context context) {
		return new ReactHighScoreDatabase(context);
	}

	private final DatabaseHelper databaseHelper;

	public ReactHighScoreDatabase(Context context) {
		databaseHelper = new DatabaseHelper(context);
	}

	public void addEntry(HighScoreEntry entry) {
		ContentValues values = new ContentValues();
		values.put("name", entry.getName());
		values.put("score", entry.getScore());
		values.put("bac", entry.getBac());
		values.put("drinks", entry.getDrinks());
        values.put("sleeptime", entry.getSleeptime());

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		try {
			long insertid = db.insert(TABLE_NAME, null, values);
			System.out.println(insertid);
		} finally {
			db.close();
		}
	}

	public Cursor createCursor(String[] cols, String args) {

		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		return db.query(TABLE_NAME, cols, args, null,
				null, null, null);

	}
	
	public void deleteEntry(HighScoreEntry entry) {
				
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.delete(TABLE_NAME, 
				" _id = " +entry.getId(), null);
		List<HighScoreEntry> list = getAllEntries();
		System.out.println();
	}
	
	public HighScoreEntry getEntry(int _id) {
		HighScoreEntry entry = new HighScoreEntry();
		SQLiteDatabase db = databaseHelper.getWritableDatabase();

		Cursor cursor = db.query(TABLE_NAME,
				new String[]{"_id","name","score","bac","drinks","sleeptime"},"_id = " + _id, null, null,
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			entry.setId(cursor.getInt(0));
			entry.setName(cursor.getString(1));
			entry.setScore(cursor.getInt(2));
			entry.setBac(cursor.getDouble(3));
			entry.setDrinks(cursor.getDouble(4));
            entry.setSleeptime(cursor.getDouble(5));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return entry;
	}
	
	public List<HighScoreEntry> getAllEntries() {
		List<HighScoreEntry> entries = new ArrayList<HighScoreEntry>();
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		//HighScoreEntry entry = new HighScoreEntry();

		Cursor cursor = db.query(TABLE_NAME,
				new String[]{"_id","name","score","bac","drinks","sleeptime"}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			HighScoreEntry entry = new HighScoreEntry();
			entry.setId(cursor.getInt(0));
			entry.setName(cursor.getString(1));
			entry.setScore(cursor.getInt(2));
			entry.setBac(cursor.getDouble(3));
			entry.setDrinks(cursor.getDouble(4));
            entry.setSleeptime(cursor.getDouble(5));
			entries.add(entry);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return entries;
	}
	
	

}
