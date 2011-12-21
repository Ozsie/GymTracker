package se.djupfeldt.oscar.gymtracker.exercises;

import java.util.HashMap;

import se.djupfeldt.oscar.gymtracker.GymTrackerActivity;

import android.util.Log;

public class Exercise implements Comparable<Exercise> {
	public static final String TIME = "time";
	public static final String DOUBLE = "double";
	public static final String INTEGER = "integer";
	protected String name;
	private HashMap<String, Field> fields = new HashMap<String, Field>();
	
	public Exercise(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int compareTo(Exercise another) {
		return name.compareTo(another.getName());
	}
	
	public void addField(Field field) {
		if (fields.containsKey(field.getName())) {
			Log.e(GymTrackerActivity.TAG, "Exercise " + name + " already contains a field named " + name);
			return;
		}
		fields.put(field.getName(), field);
	}
	
	public HashMap<String, Field> getFields() {
		return fields;
	}
	
	public Field getField(String name) {
		return fields.get(name);
	}
	
	@Override
	public String toString() {
		return name + " = " + fields.toString();
	}
}
