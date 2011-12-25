package se.djupfeldt.oscar.gymtracker.exercises;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import se.djupfeldt.oscar.gymtracker.GymTrackerActivity;
import android.content.Context;
import android.util.Log;

public class ExerciseHandler {
	private static final String FILE_ENDING = ".ex";

	private static final String DELIMITER = "%";

	private static ExerciseHandler instance;
	
	private Exercise selectedExercise;
	
	private LinkedHashMap<String, Exercise> exercises;
	
	private ExerciseHandler() {
		exercises = new LinkedHashMap<String, Exercise>();
	}
	
	public static ExerciseHandler getInstance() {
		if (instance == null)
			instance = new ExerciseHandler();
		return instance;
	}
	
	public HashMap<String, Exercise> getExercises() {
		return exercises;
	}
	
	public void addExercise(Exercise ex) {
		exercises.put(ex.getName(), ex);
		
		if (selectedExercise == null)
			selectedExercise = ex;
	}
	
	public LinkedList<CharSequence> getExerciseNames() {
		LinkedList<CharSequence> list = new LinkedList<CharSequence>();
		for (String e : exercises.keySet()) {
			list.add(e);
		}
		
		return list;
	}
	
	public Exercise getSelectedExercise() {
		return selectedExercise;
	}
	
	public void setSelectedExercise(String name) {
		this.selectedExercise = exercises.get(name);
	}
	
	public Exercise getExercise(String name) {
		return exercises.get(name);
	}

	public ExerciseData readData(Context context, Exercise ex, int year, int month, int day) {
		if (ex == null)
			return null;
		
		String fileName = context.getFilesDir().getAbsolutePath() + "/" + ex.getName() + "." + year + "." + month + "." + day + FILE_ENDING;
		File file = new File(fileName);
		if (!file.exists())
			return null;
		else {
			try {
				return read(file);
			} catch (FileNotFoundException e) {
				Log.e(GymTrackerActivity.TAG, "File " + fileName + " was not found.");
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(GymTrackerActivity.TAG, "Exception while reading file " + fileName + ".");
				e.printStackTrace();
			}
		}
		return null;
	}

	private ExerciseData read(File file) throws FileNotFoundException, IOException {
		ExerciseData data = new ExerciseData();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		StringTokenizer st = new StringTokenizer(file.getName());
		st.nextToken(".");
		int year = Integer.parseInt(st.nextToken("."));
		int month = Integer.parseInt(st.nextToken("."));
		int day = Integer.parseInt(st.nextToken("."));
		
		data.setYear(year);
		data.setMonth(month);
		data.setDay(day);

		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(DELIMITER);
			if (parts.length != 3) {
				Log.e(GymTrackerActivity.TAG, "File " + file.getName() + " contained erroneous data: " + line);
				return null;
			} else {
				String name = parts[0];
				String value = parts[1];
				String type = parts[2];
				if (type.equals(Exercise.DOUBLE)) {
					data.addValue(name, Double.parseDouble(value));
				} else if (type.equals(Exercise.INTEGER)) {
					data.addValue(name, Integer.parseInt(value));
				} else {
					data.addValue(name, value);
				}
			}
		}
		
		reader.close();
		
		return data;
	}
	
	public boolean writeData(Context context, Exercise ex, int year, int month, int day, ExerciseData data) {
		String fileName = context.getFilesDir().getAbsolutePath() + "/" + ex.getName() + "." + year + "." + month + "." + day + FILE_ENDING;
		File file = new File(fileName);
		
		if (!file.exists()) {
			try {
				boolean createOk = file.createNewFile();
				Log.d(GymTrackerActivity.TAG, "File created " + createOk);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.d(GymTrackerActivity.TAG, "File exists");
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			for (String field : data.getValues().keySet()) {
				StringBuilder sb = new StringBuilder();
				
				sb.append(field).append(DELIMITER);
				sb.append(data.getValue(field)).append(DELIMITER);
				sb.append(ex.getFields().get(field).getType());
				sb.append("\n");
				
				Log.d(GymTrackerActivity.TAG, "Writing " + sb.toString());
				
				writer.write(sb.toString());
			}
			
			writer.close();
			
			return true;
		} catch (FileNotFoundException e) {
			Log.e(GymTrackerActivity.TAG, "File " + fileName + " was not found.");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(GymTrackerActivity.TAG, "Exception while reading file " + fileName + ".");
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void clearAllData(Context context) {
		File root = context.getFilesDir();
		if (root.isDirectory()) {
			for (File child : root.listFiles()) {
				if (child.getName().endsWith(FILE_ENDING)) {
					Log.d(GymTrackerActivity.TAG, "Deleting file " + child.getName());
					child.delete();
				}
			}
		}
	}

	public boolean clearForCurrentDate(Context context, int year, int month, int day, Exercise ex) {
		String filePath = context.getFilesDir().getAbsolutePath() + "/" + ex.getName() + "." + year + "." + month + "." + day + FILE_ENDING;
		File file = new File(filePath);
		return file.delete();
	}

	public LinkedList<ExerciseData> readAllData(Context context, Exercise ex) {
		if (ex == null)
			return null;
		
		String root = context.getFilesDir().getAbsolutePath();
		File file = new File(root);
		
		if (!file.exists())
			return null;
		
		LinkedList<ExerciseData> data = null;
		try {
			if (file.isDirectory()) {
				data = new LinkedList<ExerciseData>();
				for (File child : file.listFiles()) {
					if (child.getName().startsWith(ex.getName()) && child.getName().endsWith(FILE_ENDING)) {
						data.add(read(child));
					}
				}
			}
		} catch (FileNotFoundException e) {
			Log.e(GymTrackerActivity.TAG, "File could not be found: " + e);
		} catch (IOException e) {
			Log.e(GymTrackerActivity.TAG, "Error when reading file: " + e);
		}
		
		if (data != null) {
			Collections.sort(data);
		}
		
		return data;
	}
}
