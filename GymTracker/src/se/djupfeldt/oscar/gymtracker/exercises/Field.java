package se.djupfeldt.oscar.gymtracker.exercises;

public final class Field implements Comparable<Field> {
	private String name;
	private String unit;
	private String type;
	private int position;
	
	public Field(String name, String unit, String type, int position) {
		this.name = name;
		this.unit = unit;
		this.type = type;
		this.position = position;
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
	
	public int getPosition() {
		return position;
	}

	public int compareTo(Field another) {
		return position - another.getPosition();
	}
}
