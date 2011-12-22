package se.djupfeldt.oscar.gymtracker.exercises;

import java.util.HashMap;

public class ExerciseData {
	private HashMap<String, Object> values;
	
	public ExerciseData() {
		values = new HashMap<String, Object>();
	}
	
	public void addValue(String field, Object value) {
		values.put(field, value);
	}
	
	public Object getValue(String field) {
		return values.get(field);
	}
	
	public HashMap<String, Object> getValues() {
		return values;
	}
}
