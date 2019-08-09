package com.hispet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;


import org.json.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author melaeke
 *
 */
public class Main {
	
	public static void main(String args) {
		
	}

	public static int startProcessing(String inputFileName, String outputFileName, ProcessWorker worker) {
		worker.showMessage("\nReading input file...");
		
		ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode temp=null;
		try {
			temp = objectMapper.readTree(new File(inputFileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Event[] events = objectMapper.convertValue(temp.get("events"), Event[].class);
		
		
		// first create a loop to iterate on every event.

		// for every event, create an Event object to save the data.

		// inside the event object make sure that the id doesn't exist,
		// if the ID exists add the values and then just move
		// if the id doesn't exist create a new object and return that object to the new
		// one.
		for(int i=0;i<events.length;i++) {
			EventProcessor.addEvent(events[i]);
		}
		System.out.println("Events which don't have evetDate : "+EventProcessor.eventDateCounter);
		
		System.out.println("finished");
		worker.showMessage("\nFinsihed reading file. Starting conversion");
		// Now I have all the Events changed I need it to change it to a JSON array and
		// export it.
		Object[] keys = EventProcessor.allEvents.keySet().toArray();
		DataValue[] finalDataValue = new DataValue[EventProcessor.allEvents.keySet().size()];
		for(int i = 0 ; i<keys.length;i++) {
			finalDataValue[i] = EventProcessor.allEvents.get((String)keys[i]);
		}
		
		
		display("all DataValues:" + EventProcessor.allDataValuesSize);
		display("All Event dataValuesFinal:" + EventProcessor.allEventsSize);
		display("All Events oridginally : " + events.length);
		display("Events which aren't imported because of DISease code : " + EventProcessor.eventsWithNoDiseaseCode);
		
		worker.showMessage("\nFinished conversion.");
		worker.showMessage("\n Converted Data Values : "+EventProcessor.allDataValuesSize);
		worker.showMessage("\n Ignored because of Disease code : "+EventProcessor.eventsWithNoDiseaseCode);
		worker.showMessage("\nWriting data to file");
		
		try {
			FileOutputStream os = new FileOutputStream(outputFileName);
			
			DataValueSet tempOutput= new DataValueSet();
			tempOutput.setDataValues(finalDataValue);
			
			ObjectMapper om = new ObjectMapper();
			om.writeValue(os, tempOutput);
			
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
			Scanner scanner = new Scanner(EventProcessor.class.getResourceAsStream(fileName));
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
	
	
	/**
	 * This class is created so that the output would have a "{dataValues:[" at the begining so that importing
	 * would be made easily
	 * @author melaeke
	 *
	 */
	static class DataValueSet{
		public DataValue [] dataValues;
		
		public DataValue[] getDataValues() {
			return dataValues;
		}
		
		public void setDataValues(DataValue[] dataValues) {
			this.dataValues=dataValues;
		}
	}
}
