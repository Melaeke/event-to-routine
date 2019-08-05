package com.hispet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Scanner;

import org.json.*;

/**
 * 
 * @author melaeke
 *
 */
public class Main {
	
	public static void main(String args) {
		
	}

	public static int startProcessing(String inputFileName, String outputFileName) {

		JSONArray events=null;
		try {
			events = readJSON(inputFileName, false).getJSONArray("events");
		} catch (JSONException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 0;
		}

		// first create a loop to iterate on every event.

		// for every event, create an Event object to save the data.

		// inside the event object make sure that the id doesn't exist,
		// if the ID exists add the values and then just move
		// if the id doesn't exist create a new object and return that object to the new
		// one.

		for (Object obj : events) {
			Event.addEvent((JSONObject) obj);
		}

		JSONArray finalEventsArray = new JSONArray();
		// Now I have all the Events changed I need it to change it to a JSON array and
		// export it.
		Iterator<String> keys = Event.allEvents.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			Event e = (Event) Event.allEvents.get(key);
			JSONObject temp = new JSONObject(e);
			finalEventsArray.put(temp);
		}

		display("all DataValues:" + Event.allDataValuesSize);
		Event.allEvents.length();
		display("All Event dataValuesFinal:" + Event.allEventsSize);
		display("All Events oridginally : " + events.length());
		display("Events which aren't imported because of DISease code : " + Event.eventsWithNoDiseaseCode);
		
		try {
			OutputStream os = new FileOutputStream(outputFileName);

			BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os));

			buffWriter.write(finalEventsArray.toString(4));
			buffWriter.close();
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 1;

	}

	public static JSONObject readJSON(String fileName, boolean fromSamePackage) throws IOException {

		StringBuilder sb = new StringBuilder();
		System.out.println("Started reading File " + fileName + " !!!");
		if (!fromSamePackage) {
			File file = new File(fileName);

			InputStream is = new FileInputStream(file);
			BufferedReader buff = new BufferedReader(new InputStreamReader(is));
			String line = buff.readLine();
			while (line != null) {
				sb.append(line).append("\n");
				line = buff.readLine();
			}

			buff.close();

		} else {
			Scanner scanner = new Scanner(Event.class.getResourceAsStream(fileName));
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine() + "\n");
			}
			scanner.close();

		}
		display("Finished reading file. Converting to JSON...");
		String stringFile = sb.toString();

		JSONObject obj = new JSONObject(stringFile);
		display("Finished converting to JSON. Starting processing...");
		return obj;
	}

	public static void display(String message) {
		System.out.println(message);
	}

	public static void displayErrorMessage(String message) {
		System.out.println("ERROR: " + message);
	}
}
