package se.djupfeldt.oscar.gymtracker;

import java.util.Calendar;

import se.djupfeldt.oscar.gymtracker.database.ExercisesTableHandler;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class GymTrackerActivity extends Activity {
	public static final String TAG =  "GymTrackerDebug";
	protected static final int DATE_DIALOG_ID = 0;
	LinearLayout baseLayout;
	TextView exerciseHeader;
	
	private int mYear;
	private int mMonth;
	private int mDay;
	
	private ArrayAdapter adapter;
    
    private DatePickerDialog.OnDateSetListener mDateSetListener = new OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			
			updateDateText();
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ExercisesTableHandler exTable = new ExercisesTableHandler(this);
        exTable.open();
        
        for (String s : getResources().getStringArray(R.array.Exercises)) {
        	exTable.createExercise(s);
        	Log.d(TAG, "Creating exercise " + s);
        }
        
        setContentView(R.layout.main);
        
        final Button pickDateButton = (Button) findViewById(R.id.pickDateButton);
        pickDateButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
        
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        updateDateText();
        
        Spinner exSpinner = (Spinner) findViewById(R.id.exerciseSpinner);
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.Exercises, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exSpinner.setAdapter(spinnerAdapter);
    }
    
    private void updateDateText() {
    	TextView text = (TextView) findViewById(R.id.dateText);
    	text.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear));
    }
	
	protected android.app.Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	};
}