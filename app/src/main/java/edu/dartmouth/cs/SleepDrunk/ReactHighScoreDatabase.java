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
					+ "score INTEGER NOT NULL, bac real,drinks real" + ");");
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
				new String[]{"_id","name","score","bac","drinks"},"_id = " + _id, null, null,
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			entry.setId(cursor.getInt(0));
			entry.setName(cursor.getString(1));
			entry.setScore(cursor.getInt(2));
			entry.setBac(cursor.getDouble(3));
			entry.setDrinks(cursor.getDouble(4));
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
				new String[]{"_id","name","score","bac","drinks"}, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			HighScoreEntry entry = new HighScoreEntry();
			entry.setId(cursor.getInt(0));
			entry.setName(cursor.getString(1));
			entry.setScore(cursor.getInt(2));
			entry.setBac(cursor.getDouble(3));
			entry.setDrinks(cursor.getDouble(4));
			entries.add(entry);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return entries;
	}
	
	
	public int getPositionForScore(int score) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		try {
			Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE score <= " + score, null);
			try {
				c.moveToFirst();
				return c.getInt(0) + 1;
			} finally {
				c.close();
			}
		} finally {
			db.close();
		}
	}

	public List<HighScoreEntry> getSortedHighScores() {
		List<HighScoreEntry> result = new ArrayList<HighScoreEntry>();

		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		try {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			queryBuilder.setTables(TABLE_NAME);

			String[] projection = null; // list of columns, null:=all
			String selection = null; // where clause excluding where, null:=all rows
			String[] selectionArgs = null; // replaces ?s in selection
			String groupBy = null; // SQL GROUP BY
			String having = null; // SQL HAVING
			String sortOrder = "_id"; // SQL ORDER BY

			int count = 0;
			Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
			try {
				if (!cursor.moveToFirst())
					return result;
				int idIndex = cursor.getColumnIndex("_id");
				int nameIndex = cursor.getColumnIndex("name");
				int scoreIndex = cursor.getColumnIndex("score");
				do {
					String name = cursor.getString(nameIndex);
					int score = cursor.getInt(scoreIndex);
					count++;
					if (count > MAX_ENTRIES) {
						int id = cursor.getInt(idIndex);
						db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id = " + id);
					} else {
						
						HighScoreEntry entry = new HighScoreEntry();
						entry.setScore(score);
						entry.setName(name);
						result.add(entry);
					}
				} while (cursor.moveToNext());

				return result;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
}
