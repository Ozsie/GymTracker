package se.djupfeldt.oscar.gymtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ExercisesTableHandler {
	private static final String EXERCISES_TABLE_NAME = "exercises";
	
	public static final String KEY_NAME = "name";
	public static final String KEY_ROWID = "_id";
	
	private DatabaseHandler dbHandler;
	private SQLiteDatabase db;
	
	public static final String EXERCISES_TABLE_CREATE = "create table " + EXERCISES_TABLE_NAME +
			" (" + KEY_ROWID + " integer key autoincrement, " +
			KEY_NAME + " text not null);";
	
	private final Context context;
	
	public ExercisesTableHandler(Context context) {
		this.context = context;
	}
	
	public ExercisesTableHandler open() throws SQLException {
		dbHandler = DatabaseHandler.getInstance(context);
		db = dbHandler.getWritableDatabase();
		
		return this;
	}
	
	public long createExercise(String name) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		
		return db.insert(EXERCISES_TABLE_NAME, null, initialValues);
	}
	
}
