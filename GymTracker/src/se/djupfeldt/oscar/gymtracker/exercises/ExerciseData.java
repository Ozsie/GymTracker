package se.djupfeldt.oscar.gymtracker.exercises;

import java.util.HashMap;

public class ExerciseData implements Comparable<ExerciseData> {
	private HashMap<String, Object> values;
	private int year;
	private int month;
	private int day;
	
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
	
	public int getDay() {
		return day;
	}
	public int getMonth() {
		return month;
	}
	public int getYear() {
		return year;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public int compareTo(ExerciseData another) {
		if ((year - another.getYear()) != 0) {
			return year - another.getYear();
		}
			
		if ((month - another.getMonth()) != 0) {
			return month - another.getMonth();
		}
		
		return day - another.getDay();
	}
	
	
}
