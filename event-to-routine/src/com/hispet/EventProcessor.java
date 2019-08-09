package com.hispet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * This is an event class that will be used to write the final result to a
 * JSONObject
 * 
 * @author melaeke
 *
 */
public class EventProcessor {

	/**
	 * This is the dataElement which contains the disease codes.
	 */
	private static String diseaseDataElement = "PiDErHjsQgX";

	public static JSONObject diseaseDEFromCode;

	public static JSONObject categoryOptionsFromEventDataElements;

	public static Map<String,DataValue> allEvents = new HashMap<String,DataValue>();

	/**
	 * This is used to track the size of allEvents not to use the Iterator.
	 */
	public static int allEventsSize = 0;

	/**
	 * This counts all the dataValues in all events except the diseaseCode dataValue
	 */
	public static int allDataValuesSize = 0;

	/**
	 * This counts the number of events which don't have Disease code in their DV
	 * array.
	 */
	public static int eventsWithNoDiseaseCode = 0;

	
	/**
	 * this is the initialization
	 */
	static {
		try {
			diseaseDEFromCode = Main.readJSON("diseaseDataElementFromDiseaseCode.json", true);
			categoryOptionsFromEventDataElements = Main.readJSON("categoryOptionsFromEventDataElements.json", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int eventDateCounter=0;
	public static void addEvent(Event event) {

		// these are the default for all the data values in this event

		String period = event.getEventDate();
		if(period==null) {
			eventDateCounter++;
			period=event.getDueDate();
		}
		period = period.substring(0, 4) + period.substring(5, 7);
		String dataElement = diseaseDataElementFromEvent(event);

		// from now on, Category Combo, storedBy and value are stored in the
		// dataValue array of the event so look for them inside the datavalue of the
		// event.
		for (DataValue dataValue : event.getDataValues()) {

			// if the dataElement in the dv is the disease DE then continue.
			if (dataValue.getDataElement().equals(diseaseDataElement)) {
				continue;
			}

			// increment dataValueSize if the dv is not disease code
			allDataValuesSize++;

			String categoryOptionCombo = categoryOptionsFromEventDataElements.getString(dataValue.getDataElement());

			// using all the unique fields, create an ID so that we can use this unique ID
			// to check if the event was already created or a new one needs to be created.
			String key = period + "|" + dataElement + "|" + event.getOrgUnit() + "|" + categoryOptionCombo + "|"
					+ event.getAttributeOptionCombo();

			// check if this event is already registered.
			// JSONObject = allEvents.
			if (allEvents.get(key)!=null) {
				// this means the key exists so there is already a registered event
				// so the task is just add the value on the existing event and then
				// push it back using the same key to replace the previous event.
				DataValue dv= allEvents.get(key);

				// add the value of the previous with the new one.
				dv.setValue(""+(Integer.parseInt(dv.getValue()) + Integer.parseInt(dataValue.getValue())));

				// push back the new object to allEvents object.
				allEvents.put(key, dv);

			} else {
				// event doesn't exist so create one and push it to the array.
				DataValue newDataValue= new DataValue();
				try {
					newDataValue.setAttributeOptionCombo(event.getAttributeOptionCombo());
					newDataValue.setCategoryOptionCombo(categoryOptionCombo);
					newDataValue.setDataElement(dataElement);
					newDataValue.setOrgUnit(event.getOrgUnit());
					newDataValue.setPeriod(period);
					newDataValue.setStoredBy(dataValue.getStoredBy());
					newDataValue.setValue(dataValue.getValue());
				} catch (Exception e) {
					eventsWithNoDiseaseCode++;
					System.out.println("JSON Error: "+e);
					e.printStackTrace();
					continue;
				}

				// push the new event to allEvents
				allEvents.put(key, newDataValue);
				allEventsSize++;
			}
		}

	}

	/**
	 * This method returns the routine disease data element from a jsonEvent object
	 * it searches through all the values of the event and find the dataValue which
	 * contains the disease code. and then using the mapping it changes that disease
	 * code to the routine data element id.
	 * 
	 * @param dataValues the dataValues array to look for the disease code from.
	 * @param eventID    used to display the id if the data Element doesn't exist in
	 *                   the dVs.
	 * @return the uid of the dataElement
	 * 
	 */
	public static String diseaseDataElementFromEvent(Event event) {

		for (DataValue dv : event.getDataValues()) {
			// System.out.println(dataValue.getString("dataElement"));
			if (dv.getDataElement().equals(diseaseDataElement)) {
				String diseaseCode = dv.getValue();
				String dataElementId = diseaseDEFromCode.getString(diseaseCode);
				return dataElementId;
			} else {
				continue;
			}
		}
		Main.displayErrorMessage("Event doesn't contain A Disease code : " + event.getEvent());
		eventsWithNoDiseaseCode++;
		return null;
	}

}
