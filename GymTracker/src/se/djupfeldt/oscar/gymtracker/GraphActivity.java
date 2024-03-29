package se.djupfeldt.oscar.gymtracker;

import java.util.LinkedList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import se.djupfeldt.oscar.gymtracker.exercises.Exercise;
import se.djupfeldt.oscar.gymtracker.exercises.ExerciseData;
import se.djupfeldt.oscar.gymtracker.exercises.ExerciseHandler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GraphActivity extends Activity {

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private GraphicalView mChartView;
	
	private Exercise currentExercise;
	private String selectedField = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.graph);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String exercise = extras.getString("Exercise");
		
		currentExercise = ExerciseHandler.getInstance().getExercise(exercise);
		
		TextView text = (TextView) findViewById(R.id.exerciseGraphText);
		text.setText(exercise);
		
		setupFieldPicker();

		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 40, 30, 15, 40 });
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setPointSize(10);
		mRenderer.setXTitle("Date");
		String unit = " (" + currentExercise.getField(selectedField).getUnit() + ")";
		Log.d(GymTrackerActivity.TAG, unit);
		if (unit.equals(" ()"))
			unit = "";
		mRenderer.setYTitle(selectedField + unit);

		String seriesTitle = exercise + ", " + selectedField;
		mCurrentSeries = new XYSeries(seriesTitle);
		mDataset.addSeries(mCurrentSeries);

		mCurrentRenderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(mCurrentRenderer);
		mCurrentRenderer.setPointStyle(PointStyle.CIRCLE);
		mCurrentRenderer.setFillPoints(true);
		mCurrentRenderer.setLineWidth(2);
		
		populateDataset();
		
		if (mChartView != null) {
			mChartView.repaint();
		}
	}

	private void populateDataset() {
		mCurrentSeries.clear();
		LinkedList<ExerciseData> exDataList = ExerciseHandler.getInstance().readAllData(this, currentExercise);
		for (int i = 0; i < exDataList.size(); i++) {
			ExerciseData data = exDataList.get(i);
			Log.d(GymTrackerActivity.TAG, "Field: " + selectedField);
			Object value = data.getValue(selectedField);
			Log.d(GymTrackerActivity.TAG, "Instance of " + selectedField + " " + value.getClass());
			if (value instanceof Integer) {
				Log.d(GymTrackerActivity.TAG, "Adding " + i + ", " + (Integer) data.getValue(selectedField));
				mCurrentSeries.add(i, (Integer) data.getValue(selectedField));
			}
			if (value instanceof Double) {
				mCurrentSeries.add(i, (Integer) data.getValue(selectedField));
			}
			mRenderer.addXTextLabel(i, data.getYear() + "." + data.getMonth() + "." + data.getDay());
		}
		mRenderer.setXLabels(0);
	}

	private void setupFieldPicker() {
		Spinner spinner = (Spinner) findViewById(R.id.plotSpinner);
		
		LinkedList<CharSequence> fieldNames = new LinkedList<CharSequence>();
		for (String key : currentExercise.getFields().keySet()) {
			if (!currentExercise.getField(key).getType().equals(Exercise.STRING))
				fieldNames.add(key);
		}
		
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, fieldNames);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.d(GymTrackerActivity.TAG, ((Spinner) arg0).getSelectedItem().toString());
				String name = ((Spinner) arg0).getSelectedItem().toString();
				selectedField = name;
				
				String unit = " (" + currentExercise.getField(selectedField).getUnit() + ")";
				Log.d(GymTrackerActivity.TAG, unit);
				if (unit.equals(" ()"))
					unit = "";
				mRenderer.setYTitle(selectedField + unit);
				populateDataset();
				mChartView.repaint();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				Log.d(GymTrackerActivity.TAG, "Nothing was selected");
			}
		});
		
		selectedField = spinner.getSelectedItem().toString();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.graph_layout);
			mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(100);

			mChartView.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
					if (seriesSelection != null) {
						Toast.makeText(GraphActivity.this, currentExercise.getName() + ", " +
								mRenderer.getXTextLabel(seriesSelection.getXValue()) + ": " +
								seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			mChartView.addZoomListener(new ZoomListener() {
				public void zoomApplied(ZoomEvent e) {
				}

				public void zoomReset() {
				}
			}, true, true);
			mChartView.addPanListener(new PanListener() {
				public void panApplied() {
				}
			});
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}
}
