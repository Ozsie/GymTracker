package se.djupfeldt.oscar.gymtracker.xml;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import se.djupfeldt.oscar.gymtracker.GymTrackerActivity;
import se.djupfeldt.oscar.gymtracker.R;
import se.djupfeldt.oscar.gymtracker.exercises.Exercise;
import se.djupfeldt.oscar.gymtracker.exercises.ExerciseHandler;
import se.djupfeldt.oscar.gymtracker.exercises.Field;
import se.djupfeldt.oscar.gymtracker.exercises.Time;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;


public class ExerciseXMLHandler {
	private static final String EXERCISE_TAG = "Exercise";
	private static final String FIELD_TAG = "Field";
	private static final String EXERCISE_NAME_ATTRIBUTE = "name";
	private static final String FIELD_NAME_ATTRIBUTE = "name";
	private static final String FIELD_TYPE_ATTRIBUTE = "type";
	private static final String FIELD_UNIT_ATTRIBUTE = "unit";
	private static final String FIELD_POSITION_ATTRIBUTE = "position";

	public static void parse(Context context) {
		XmlResourceParser xml = context.getResources().getXml(R.xml.exercises);
		
		try {
			xml.next();
			int eventType = xml.getEventType();
			
			String nodeValue = "";
			Exercise ex = null;
			ExerciseHandler exHandler = ExerciseHandler.getInstance();
			LinkedList<Field> fields = null;
			LinkedList<Exercise> exercises = new LinkedList<Exercise>();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT)
					Log.i(GymTrackerActivity.TAG, "Start of Exercise XML");
				else if (eventType == XmlPullParser.START_TAG){
					nodeValue = xml.getName();
					Log.i(GymTrackerActivity.TAG, "Node: " + nodeValue);
					
					if (nodeValue.equals(EXERCISE_TAG)) {
						String name = xml.getAttributeValue(null, EXERCISE_NAME_ATTRIBUTE);
						ex = new Exercise(name);
						fields = new LinkedList<Field>();
						exercises.add(ex);
					}
					
					if (nodeValue.equals(FIELD_TAG)) {
						if (xml.getAttributeCount() < 2) {
							Log.e(GymTrackerActivity.TAG, "Malformed XML: " + xml.toString());
							return;
						}
						String fieldName = xml.getAttributeValue(null, FIELD_NAME_ATTRIBUTE);
						String fieldType = xml.getAttributeValue(null, FIELD_TYPE_ATTRIBUTE);
						String fieldUnit = xml.getAttributeValue(null, FIELD_UNIT_ATTRIBUTE);
						int fieldPosition = xml.getAttributeIntValue(null, FIELD_POSITION_ATTRIBUTE, 0);
						
						Log.d(GymTrackerActivity.TAG, "Found field " + fieldName + ", " + fieldType + ", " + fieldUnit + ", " + fieldPosition);
						
						if (ex != null) {
							Field field = new Field(fieldName, fieldUnit, fieldType, fieldPosition);
							Log.d(GymTrackerActivity.TAG, "Adding field " + fieldName + " to temp list");
							fields.add(field);
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					nodeValue = xml.getName();
					Log.i(GymTrackerActivity.TAG, "Node End: " + nodeValue);
					if (nodeValue.equals(EXERCISE_TAG)) {
						Collections.sort(fields);
						Log.d(GymTrackerActivity.TAG, "TEmp fields: " + fields.toString());
						for (Field f : fields) {
							ex.addField(f);
						}
						
						fields = null;
						ex = null;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					
				}
				
				eventType = xml.next();
			}
			
			Collections.sort(exercises);
			
			for (Exercise exercise : exercises) {
				exHandler.addExercise(exercise);
			}
			
			Log.d(GymTrackerActivity.TAG, exHandler.getExercises().toString());
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
