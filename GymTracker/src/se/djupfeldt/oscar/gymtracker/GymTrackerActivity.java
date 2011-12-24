package se.djupfeldt.oscar.gymtracker;

import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;

import se.djupfeldt.oscar.gymtracker.exercises.Exercise;
import se.djupfeldt.oscar.gymtracker.exercises.ExerciseData;
import se.djupfeldt.oscar.gymtracker.exercises.ExerciseHandler;
import se.djupfeldt.oscar.gymtracker.exercises.Field;
import se.djupfeldt.oscar.gymtracker.xml.ExerciseXMLHandler;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	}

	private void setupDatePicker() {		
		final TextView dateText = (TextView) findViewById(R.id.dateText);
		dateText.setOnClickListener(new OnClickListener() {
			
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
			Log.d(TAG, "Data for " + ex.getName() + ", " + mYear + "." + mMonth + "." + mDay + " did exist.");
			dataExists = exData.getValues().size() > 0;
		}

		TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
		table.removeAllViews();

		final ExerciseData tempFinalExData = exData;
		for (final String fieldName : ex.getFields().keySet()) {
			TableRow row = new TableRow(this);
			table.addView(row);

			TextView fieldNameText = new TextView(this, null, android.R.style.TextAppearance_Large);
			fieldNameText.setText(new StringBuilder().append(fieldName));
			row.addView(fieldNameText);

			Field field = ex.getField(fieldName);
			String unit = field.getUnit();
			String type = field.getType();
			Log.d(TAG, "Type for " + fieldName + ": " + field.getType());
			if (type.equals(Exercise.TIME)) {
				buildTimeStampUI(exData, dataExists, tempFinalExData, fieldName, row);
			}
			if (type.equals(Exercise.DOUBLE)) {
				buildDoubleUI(exData, dataExists, tempFinalExData, fieldName, row);
			}
			if (type.equals(Exercise.INTEGER)) {
				buildIntegerUI(exData, dataExists, tempFinalExData, fieldName, row);
			}
			if (type.equals(Exercise.STRING)) {
				buildStringUI(exData, dataExists, tempFinalExData, fieldName, row);
			}

			TextView txt = new TextView(this, null, android.R.style.TextAppearance_Large);
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
		
		final URL infoLink = ex.getInfoLink();
		if (infoLink != null) {
			Button infoButton = new Button(this);
			infoButton.setText("Exercise Info");
			infoButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(infoLink.toString()));
					startActivity(i);
				}
			});
			row.addView(infoButton);
		}
	}

	private void buildStringUI(ExerciseData exData, boolean dataExists,
			final ExerciseData tempFinalExData, final String fieldName, TableRow row) {
		EditText text = new EditText(this);

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
				tempFinalExData.addValue(fieldName, currentText.toString());
			}
		});

		row.addView(text);
		if (dataExists) {
			if (exData.getValue(fieldName) != null) {
				String data = exData.getValue(fieldName).toString();
				Log.d(TAG, "Data was " + data);
				text.setText(new StringBuilder().append(data));
			}
		}
	}

	private void buildIntegerUI(ExerciseData exData, boolean dataExists,
			final ExerciseData tempFinalExData, final String fieldName,
			TableRow row) {
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
				Log.d(TAG, "DATA: " + exData.getValue(fieldName).toString());
				int data = Integer.parseInt(exData.getValue(fieldName).toString());
				Log.d(TAG, "Data was " + data);
				text.setText(new StringBuilder().append(data));
			}
		}
	}

	private void buildDoubleUI(ExerciseData exData, boolean dataExists, final ExerciseData tempFinalExData, final String fieldName, TableRow row) {
		Log.d(TAG, Exercise.DOUBLE);
		EditText text = new EditText(this);
		text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

		text.addTextChangedListener(new TextWatcher() {
			private CharSequence currentText;
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				currentText = s;
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				Log.d(TAG, "D: writing " + currentText.toString());
				tempFinalExData.addValue(fieldName, Double.parseDouble(currentText.toString()));
			}
		});

		row.addView(text);
		if (dataExists) {
			if (exData.getValue(fieldName) != null) {
				double data = Double.parseDouble(exData.getValue(fieldName).toString());
				text.setText(new StringBuilder().append(data));
			}
		}
	}

	private void buildTimeStampUI(ExerciseData exData, boolean dataExists,
			final ExerciseData tempFinalExData, final String fieldName,
			TableRow row) {
		TimePicker tp = new TimePicker(this);
		
		tp.setOnTimeChangedListener(new OnTimeChangedListener() {
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				tempFinalExData.addValue(fieldName, hourOfDay + ":" + minute);
			}
		});
		
		row.addView(tp);

		if (dataExists) {
			if (exData.getValue(fieldName) != null) {
				String timeData = exData.getValue(fieldName).toString();
				String[] parts = timeData.split(":");
				if (parts.length == 2) {
					tp.setCurrentHour(Integer.parseInt(parts[0]));
					tp.setCurrentMinute(Integer.parseInt(parts[1]));
				}
			}
		}
	}

	private void createExercises() {
		exHandler = ExerciseHandler.getInstance();
		ExerciseXMLHandler.parse(this);
	}

	private void updateDateText() {
		TextView text = (TextView) findViewById(R.id.dateText);
		text.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear));
		buildExerciseGui();
	}

	protected android.app.Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.eraseAllData:
			exHandler.clearAllData(thisContext);
			return true;
		case R.id.eraseCurrentData:
			exHandler.clearForCurrentDate(thisContext, mYear, mMonth, mDay, exHandler.getSelectedExercise());
			buildExerciseGui();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}