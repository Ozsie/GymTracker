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
	private String mDateFormat;
	private Button mNewSeries;
	private Button mAdd;
	private GraphicalView mChartView;
	
	private int mFromDay;
	private int mFromMonth;
	private int mFromYear;

	private int mToYear;
	private int mToMonth;
	private int mToDay;
	
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
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setPointSize(10);
		mRenderer.setXTitle("Time");
		mRenderer.setYTitle(selectedField);

		String seriesTitle = exercise + ", " + selectedField;
		mCurrentSeries = new XYSeries(seriesTitle);
		mDataset.addSeries(mCurrentSeries);

		mCurrentRenderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(mCurrentRenderer);
		mCurrentRenderer.setPointStyle(PointStyle.CIRCLE);
		mCurrentRenderer.setFillPoints(true);

		
		LinkedList<ExerciseData> exDataList = ExerciseHandler.getInstance().readAllData(this, currentExercise);
		for (int i = 0; i < exDataList.size(); i++) {
			Log.d(GymTrackerActivity.TAG, "Field: " + selectedField);
			Object value = exDataList.get(i).getValue(selectedField);
			Log.d(GymTrackerActivity.TAG, "Instance of " + selectedField + " " + value.getClass());
			if (value instanceof Integer) {
				Log.d(GymTrackerActivity.TAG, "Adding " + i + ", " + (Integer)exDataList.get(i).getValue(selectedField));
				mCurrentSeries.add(i, (Integer)exDataList.get(i).getValue(selectedField));
			}
			if (value instanceof Double) {
				mCurrentSeries.add(i, (Integer)exDataList.get(i).getValue(selectedField));
			}
		}
		
		if (mChartView != null) {
			mChartView.repaint();
		}
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
				mRenderer.setYTitle(selectedField);
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
					double[] xy = mChartView.toRealPoint(0);
					if (seriesSelection == null) {
						Toast.makeText(GraphActivity.this, "No chart element was clicked", Toast.LENGTH_SHORT)
						.show();
					} else {
						Toast.makeText(
								GraphActivity.this,
								"Chart element in series index " + seriesSelection.getSeriesIndex()
								+ " data point index " + seriesSelection.getPointIndex() + " was clicked"
								+ " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue()
								+ " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1], Toast.LENGTH_SHORT).show();
					}
				}
			});
			mChartView.setOnLongClickListener(new View.OnLongClickListener() {

				public boolean onLongClick(View v) {
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(GraphActivity.this, "No chart element was long pressed",
								Toast.LENGTH_SHORT);
						return false; // no chart element was long pressed, so let something
						// else handle the event
					} else {
						Toast.makeText(GraphActivity.this, "Chart element in series index "
								+ seriesSelection.getSeriesIndex() + " data point index "
								+ seriesSelection.getPointIndex() + " was long pressed", Toast.LENGTH_SHORT);
						return true; // the element was long pressed - the event has been
						// handled
					}
				}
			});
			mChartView.addZoomListener(new ZoomListener() {
				public void zoomApplied(ZoomEvent e) {
					String type = "out";
					if (e.isZoomIn()) {
						type = "in";
					}
					System.out.println("Zoom " + type + " rate " + e.getZoomRate());
				}

				public void zoomReset() {
					System.out.println("Reset");
				}
			}, true, true);
			mChartView.addPanListener(new PanListener() {
				public void panApplied() {
					System.out.println("New X range=[" + mRenderer.getXAxisMin() + ", " + mRenderer.getXAxisMax()
							+ "], Y range=[" + mRenderer.getYAxisMax() + ", " + mRenderer.getYAxisMax() + "]");
				}
			});
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			boolean enabled = mDataset.getSeriesCount() > 0;
		} else {
			mChartView.repaint();
		}
	}
}
