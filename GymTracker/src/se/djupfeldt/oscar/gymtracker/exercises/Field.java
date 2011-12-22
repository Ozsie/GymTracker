package se.djupfeldt.oscar.gymtracker.exercises;

public class Field {
	String name;
	String unit;
	String type;
	
	public Field(String name, String unit, String type) {
		this.name = name;
		this.unit = unit;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}
	
	public String getType() {
		return type;
	}
}
