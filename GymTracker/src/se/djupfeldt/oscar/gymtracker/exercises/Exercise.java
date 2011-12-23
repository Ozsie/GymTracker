package se.djupfeldt.oscar.gymtracker.exercises;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

import se.djupfeldt.oscar.gymtracker.GymTrackerActivity;

import android.util.Log;

public class Exercise implements Comparable<Exercise> {
	public static final String TIME = "time";
	public static final String DOUBLE = "double";
	public static final String INTEGER = "integer";
	public static final String STRING = "string";
	
	protected String name;
	private LinkedHashMap<String, Field> fields = new LinkedHashMap<String, Field>();
	
	private URL infoLink = null;
	
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
		
		Log.d(GymTrackerActivity.TAG, "Adding field + " + field + " to " + name);
		fields.put(field.getName(), field);
	}
	
	public void setInfoLink(URL infoLink) {
		this.infoLink = infoLink;
	}
	
	public HashMap<String, Field> getFields() {
		return fields;
	}
	
	public Field getField(String name) {
		return fields.get(name);
	}
	
	public URL getInfoLink() {
		return infoLink;
	}
	
	@Override
	public String toString() {
		return name + " = " + fields.toString();
	}
}
