package se.djupfeldt.oscar.gymtracker;

import java.util.Calendar;
import java.util.LinkedList;

import se.djupfeldt.oscar.gymtracker.exercises.Exercise;
import se.djupfeldt.oscar.gymtracker.exercises.ExerciseData;
import se.djupfeldt.oscar.gymtracker.exercises.ExerciseHandler;
import se.djupfeldt.oscar.gymtracker.exercises.Field;
import se.djupfeldt.oscar.gymtracker.exercises.Time;
import se.djupfeldt.oscar.gymtracker.xml.ExerciseXMLHandler;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class GymTrackerActivity extends Activity {
	public static final String TAG =  "GymTrackerDebug";
	protected static final int DATE_DIALOG_ID = 0;
	LinearLayout baseLayout;
	TextView exerciseHeader;
	
	private int mYear;
	private int mMonth;
	private int mDay;
	
	private ExerciseHandler exHandler;
	
	private Context thisContext;
    
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
        
        thisContext = this;
        
        createExercises();
                
        setContentView(R.layout.main);
        
        setupDatePicker();
        
        setupSpinner();
        
        final Button eraseAll = (Button) findViewById(R.id.button1);
        eraseAll.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				exHandler.clearAllData(thisContext);
			}
		});
    }

	private void setupDatePicker() {
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
	}

	private void setupSpinner() {
		Spinner exSpinner = (Spinner) findViewById(R.id.exerciseSpinner);
		
		LinkedList<CharSequence> exercises = exHandler.getExerciseNames();
		
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, exercises);
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exSpinner.setAdapter(adapter);
        
        exSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.d(TAG, ((Spinner) arg0).getSelectedItem().toString());
				String name = ((Spinner) arg0).getSelectedItem().toString();
				exHandler.setSelectedExercise(name);
				
				buildExerciseGui();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				Log.d(TAG, "Nothing was selected");
			}
		});
	}
    
    protected void buildExerciseGui() {
    	final Exercise ex = exHandler.getSelectedExercise();
    	
    	ExerciseData exData = exHandler.readData(this, ex, mYear, mMonth, mDay);
    	boolean dataExists = true;
    	
    	if (exData == null) {
    		Log.d(TAG, "Data for " + ex.getName() + ", " + mYear + "." + mMonth + "." + mDay + " did not exist.");
    		exData = new ExerciseData();
    		dataExists = false;
    	} else {
    		dataExists = exData.getValues().size() > 0;
    	}
    	
		TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
		table.removeAllViews();

		final ExerciseData tempFinalExData = exData;
		for (final String fieldName : ex.getFields().keySet()) {
			TableRow row = new TableRow(this);
			table.addView(row);
			
			TextView fieldNameText = new TextView(this);
			fieldNameText.setText(new StringBuilder().append(fieldName));
			row.addView(fieldNameText);
			
			Field field = ex.getField(fieldName);
			String unit = field.getUnit();
			String type = field.getType();
			Log.d(TAG, "Type for " + fieldName + ": " + field.getType());
			if (type.equals(Exercise.TIME)) {
				TimePicker tp = new TimePicker(this);
				tp.setOnTimeChangedListener(new OnTimeChangedListener() {
					
					public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
						tempFinalExData.addValue(fieldName, hourOfDay + ":" + minute);
					}
				});
				row.addView(tp);
				
				if (dataExists) {
					String timeData = (String) exData.getValue(fieldName);
					if (timeData != null) {
						String[] parts = timeData.split(":");
						if (parts.length == 2) {
							tp.setCurrentHour(Integer.parseInt(parts[0]));
							tp.setCurrentMinute(Integer.parseInt(parts[1]));
						}
					}
				}
			}
			if (type.equals(Exercise.DOUBLE)) {
				Log.d(TAG, Exercise.DOUBLE);
				EditText text = new EditText(this);
				text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				
				text.addTextChangedListener(new TextWatcher() {
					private CharSequence currentText;
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						currentText = s;
					}
					
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// TODO Auto-generated method stub
						
					}
					
					public void afterTextChanged(Editable s) {
						Log.d(TAG, "D: writing " + currentText.toString());
						tempFinalExData.addValue(fieldName, Double.parseDouble(currentText.toString()));
					}
				});
				
				row.addView(text);
				if (dataExists) {
					if (exData.getValue(fieldName) != null) {
						double data = (Double) exData.getValue(fieldName);
						text.setText(new StringBuilder().append(data));
					}
				}
			}
			if (type.equals(Exercise.INTEGER)) {
				EditText text = new EditText(this);
				text.setInputType(InputType.TYPE_CLASS_NUMBER);
				
				text.addTextChangedListener(new TextWatcher() {
					private CharSequence currentText;
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						currentText = s;
					}
					
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// TODO Auto-generated method stub
						
					}
					
					public void afterTextChanged(Editable s) {
						Log.d(TAG, "I: writing " + currentText.toString());
						tempFinalExData.addValue(fieldName, Integer.parseInt(currentText.toString()));
					}
				});
				
				row.addView(text);
				if (dataExists) {
					if (exData.getValue(fieldName) != null) {
						int data = (Integer) exData.getValue(fieldName);
						Log.d(TAG, "Data was " + data);
						text.setText(new StringBuilder().append(data));
					}
				}
			}
			
			TextView txt = new TextView(this);
			txt.setText(new StringBuilder().append(unit));
			row.addView(txt);
		}
		TableRow row = new TableRow(this);
		table.addView(row);
		
		Button save = new Button(this);
		save.setText("Save Exercise");
		save.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.d(TAG, tempFinalExData.getValues().toString());
				exHandler.writeData(thisContext, exHandler.getSelectedExercise(), mYear, mMonth, mDay, tempFinalExData);
			}
		});
		row.addView(save);
	}

	private void createExercises() {
    	exHandler = ExerciseHandler.getInstance();
    	ExerciseXMLHandler.parse(this);
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