package se.djupfeldt.oscar.gymtracker.database;

import se.djupfeldt.oscar.gymtracker.GymTrackerActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "gymTrackerDatabase";
	private static final int DATABASE_VERSION = 1;
	
	private static DatabaseHandler instance;

	private DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static DatabaseHandler getInstance(Context context) {
		if (instance == null)
			instance = new DatabaseHandler(context);
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(ExercisesTableHandler.EXERCISES_TABLE_CREATE);
		Log.d(GymTrackerActivity.TAG, "Executing " + ExercisesTableHandler.EXERCISES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		Log.d(GymTrackerActivity.TAG, "onUpgrade");
		onCreate(arg0);
	}

}
