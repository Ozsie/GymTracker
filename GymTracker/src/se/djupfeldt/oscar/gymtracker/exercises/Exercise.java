package se.djupfeldt.oscar.gymtracker.exercises;

public class Exercise implements Comparable<Exercise> {
	protected String name;	
	
	public String getName() {
		return name;
	}

	public int compareTo(Exercise another) {
		return name.compareTo(another.getName());
	}
}
