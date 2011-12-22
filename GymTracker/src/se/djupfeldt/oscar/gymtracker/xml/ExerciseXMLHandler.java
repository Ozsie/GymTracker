package se.djupfeldt.oscar.gymtracker.xml;

import java.io.IOException;

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
	private static final int EXERCISE_NAME_ATTRIBUTE = 0;
	private static final int FIELD_NAME_ATTRIBUTE = 0;
	private static final int FIELD_TYPE_ATTRIBUTE = 1;
	private static final int FIELD_UNIT_ATTRIBUTE = 2;

	public static void parse(Context context) {
		XmlResourceParser xml = context.getResources().getXml(R.xml.exercises);
		
		try {
			xml.next();
			int eventType = xml.getEventType();
			
			String nodeValue = "";
			Exercise ex = null;
			ExerciseHandler exHandler = ExerciseHandler.getInstance();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT)
					Log.i(GymTrackerActivity.TAG, "Start of Exercise XML");
				else if (eventType == XmlPullParser.START_TAG){
					nodeValue = xml.getName();
					Log.i(GymTrackerActivity.TAG, nodeValue);
					
					if (nodeValue.equals(EXERCISE_TAG)) {
						String name = xml.getAttributeValue(EXERCISE_NAME_ATTRIBUTE);
						ex = new Exercise(name);
						exHandler.addExercise(ex);
					}
					
					if (nodeValue.equals(FIELD_TAG)) {
						if (xml.getAttributeCount() < 2) {
							Log.e(GymTrackerActivity.TAG, "Malformed XML: " + xml.toString());
							return;
						}
						String fieldName = xml.getAttributeValue(FIELD_NAME_ATTRIBUTE);
						String fieldType = xml.getAttributeValue(FIELD_TYPE_ATTRIBUTE);
						String fieldUnit = "";
						if (xml.getAttributeCount() == 3)
							fieldUnit = xml.getAttributeValue(FIELD_UNIT_ATTRIBUTE);
						
						
						
						if (ex != null) {
							Field field = new Field(fieldName, fieldUnit, fieldType);							
							ex.addField(field);
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					if (nodeValue.equals(EXERCISE_TAG)) {
						ex = null;
					}
					
				} else if (eventType == XmlPullParser.TEXT) {
					
				}
				
				eventType = xml.next();
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
