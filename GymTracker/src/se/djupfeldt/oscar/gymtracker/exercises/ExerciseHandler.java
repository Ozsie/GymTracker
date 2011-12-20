package se.djupfeldt.oscar.gymtracker.exercises;

import java.util.LinkedList;

public class ExerciseHandler {
	private static ExerciseHandler instance;
	
	private LinkedList<Exercise> exercises;
	
	private ExerciseHandler() {
		exercises = new LinkedList<Exercise>();
		
		exercises.add(new CrossTrainer());
		exercises.add(new Treadmill());
	}
	
	public static ExerciseHandler getInstance() {
		if (instance == null)
			instance = new ExerciseHandler();
		return instance;
	}
	
	public LinkedList<Exercise> getExercises() {
		return exercises;
	}
}
