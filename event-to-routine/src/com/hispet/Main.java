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

import com.csvreader.CsvWriter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author melaeke
 *
 */
public class Main {

	public static int startProcessing(String inputFileName, String outputFileName, ProcessWorker worker) {
		worker.showMessage("\nReading input file... "+gui.currentRunningStatus);

		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		DataValue[] finalDataValue = null;
		System.out.println("Finished initializing objectMapper.");
		if (gui.currentRunningStatus == gui.RUNNING_JSON_AND_CSV_EXPORT
				|| gui.currentRunningStatus == gui.RUNNING_JSON_EXPORT_ONLY) {
			JsonNode temp = null;

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
			for (int i = 0; i < events.length; i++) {
				EventProcessor.addEvent(events[i]);
			}
			System.out.println("Events which don't have evetDate : " + EventProcessor.eventDateCounter);

			System.out.println("finished");
			worker.showMessage("\nFinsihed reading file. Starting conversion");
			// Now I have all the Events changed I need it to change it to a JSON array and
			// export it.
			Object[] keys = EventProcessor.allEvents.keySet().toArray();
			finalDataValue = new DataValue[EventProcessor.allEvents.keySet().size()];
			for (int i = 0; i < keys.length; i++) {
				finalDataValue[i] = EventProcessor.allEvents.get((String) keys[i]);
			}

			display("all DataValues:" + EventProcessor.allDataValuesSize);
			display("All Event dataValuesFinal:" + EventProcessor.allEventsSize);
			display("All Events oridginally : " + events.length);
			display("Events which aren't imported because of DISease code : " + EventProcessor.eventsWithNoDiseaseCode);

			worker.showMessage("\nFinished conversion.");
			worker.showMessage("\n Converted Data Values : " + EventProcessor.allEventsSize);
			worker.showMessage("\n Ignored because of Disease code : " + EventProcessor.eventsWithNoDiseaseCode);
			worker.showMessage("\nWriting data to file");

			try {
				FileOutputStream os = new FileOutputStream(outputFileName);

				DataValueSet tempOutput = new DataValueSet();
				tempOutput.setDataValues(finalDataValue);

				ObjectMapper om = new ObjectMapper();
				om.writeValue(os, tempOutput);

			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		} else if (gui.currentRunningStatus == gui.RUNNING_CSV_EXPORT_ONLY) {
			/**
			 * If the condition is to change the json to a csv dump the input file is an
			 * array of Datavalue so just import the array.
			 */
			try {
				
				DataValue temp = new DataValue();
				temp.getClass();
				temp = null;
				System.out.println("Before reading value");
				finalDataValue = objectMapper.readValue(new File(inputFileName), DataValue[].class);
				System.out.println("After reading values");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (gui.currentRunningStatus == gui.RUNNING_CSV_EXPORT_ONLY
				|| gui.currentRunningStatus == gui.RUNNING_JSON_AND_CSV_EXPORT) {
			/**
			 * If the state of the program allows CSV export export the result as a database
			 * csv dump to be automatically inserted to datavalue table.
			 */
			System.out.println("Writing out csv for Database insertion.");
			CsvWriter csvWriter = new CsvWriter(outputFileName + ".csv");
			csvWriter.setUseTextQualifier(false);
			csvWriter.setRecordDelimiter('\n');
			try {
				for (int i = 0; i < finalDataValue.length; i++) {

					csvWriter.write(finalDataValue[i].inSQLForm());
					csvWriter.endRecord();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			csvWriter.close();
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
	 * This class is created so that the output would have a "{dataValues:[" at the
	 * begining so that importing would be made easily
	 * 
	 * @author melaeke
	 *
	 */
	static class DataValueSet {
		public DataValue[] dataValues;

		public DataValue[] getDataValues() {
			return dataValues;
		}

		public void setDataValues(DataValue[] dataValues) {
			this.dataValues = dataValues;
		}
	}
}
