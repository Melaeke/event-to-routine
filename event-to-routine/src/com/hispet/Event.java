package com.hispet;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This is an event class that will be used to write the final result to a
 * JSONObject
 * 
 * @author melaeke
 *
 */
public class Event {

	/**
	 * This is the dataElement which contains the disease codes.
	 */
	private static String diseaseDataElement = "PiDErHjsQgX";

	public static JSONObject diseaseDEFromCode;

	public static JSONObject categoryOptionsFromEventDataElements;

	public static JSONObject allEvents = new JSONObject();
	
	/**
	 * This is used to track the size of allEvents not to use the Iterator.
	 */
	public static int allEventsSize =0;
	
	/**
	 * This counts all the dataValues in all events except the diseaseCode dataValue
	 */
	public static int allDataValuesSize=0;
	
	/**
	 * This counts the number of events which don't have Disease code in their DV array.
	 */
	public static int eventsWithNoDiseaseCode = 0;
	

	private String eventId;

	private String period;

	private String attributeOptionCombo;

	private String orgUnit;

	private String categoryOptionCombo;

	private String dataElement;

	private int value;

	private String storedBy;

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

	public static void addEvent(JSONObject obj) {

		// these are the default for all the data values in this event

		String period = obj.getString("dueDate");
		period = period.substring(0, 4) + period.substring(5, 7);
		String orgUnit = obj.getString("orgUnit");
		String attributeOptionCombo = obj.getString("attributeOptionCombo");
		JSONArray dataValues = obj.getJSONArray("dataValues");
		String dataElement = diseaseDataElementFromEvent(dataValues, obj.getString("event"));

		// from now on, Category Combo, storedBy and value are stored in the
		// dataValue array of the event so look for them inside the datavalue of the
		// event.
		for (Object dv : dataValues) {
			
			// find the catCombo using the dataValue
			JSONObject dataValue = (JSONObject) dv;
			String tempDE=dataValue.getString("dataElement");
			
			//if the dataElement in the dv is the disease DE then continue.
			if(tempDE.equals(diseaseDataElement)) {
				continue;
			}
			
			//increment dataValueSize if the dv is not disease code
			allDataValuesSize++;
			
			String categoryOptionCombo = categoryOptionsFromEventDataElements
					.getString(tempDE);

			// using all the unique fields, create an ID so that we can use this unique ID
			// to check if the event was already created or a new one needs to be created.
			String key = period + "|" + dataElement + "|" + orgUnit + "|" + categoryOptionCombo + "|"
					+ attributeOptionCombo;

			// check if this event is already registered.
			// JSONObject = allEvents.
			if (allEvents.has(key)) {
				// this means the key exists so there is already a registered event
				// so the task is just add the value on the existing event and then
				// push it back using the same key to replace the previous event.
				Event event = (Event) allEvents.get(key);

				// add the value of the previous with the new one.
				event.setValue(event.getValue() + dataValue.getInt("value"));

				// push back the new object to allEvents object.
				allEvents.put(key, event);
				
				
			} else {
				// event doesn't exist so create one and push it to the array.
				Event event = new Event();
				event.setAttributeOptionCombo(attributeOptionCombo);
				event.setCategoryOptionCombo(categoryOptionCombo);
				event.setDataElement(dataElement);
				event.setOrgUnit(orgUnit);
				event.setPeriod(period);
				event.setStoredBy(dataValue.getString("storedBy"));
				event.setValue(dataValue.getInt("value"));

				// push the new event to allEvents
				allEvents.put(key, event);
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
	public static String diseaseDataElementFromEvent(JSONArray dataValues, String eventId) {

		for (Object dv : dataValues) {
			JSONObject dataValue = (JSONObject) dv;
			// System.out.println(dataValue.getString("dataElement"));
			if (dataValue.getString("dataElement").equals(diseaseDataElement)) {
				String diseaseCode = dataValue.getString("value");
				String dataElementId = diseaseDEFromCode.getString(diseaseCode);
				return dataElementId;
			} else {
				continue;
			}
		}
		Main.displayErrorMessage("Event doesn't contain A Disease code : " + eventId);
		eventsWithNoDiseaseCode++;
		return null;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getAttributeOptionCombo() {
		return attributeOptionCombo;
	}

	public void setAttributeOptionCombo(String attributeOptionCombo) {
		this.attributeOptionCombo = attributeOptionCombo;
	}

	public String getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(String orgUnit) {
		this.orgUnit = orgUnit;
	}

	public String getCategoryOptionCombo() {
		return categoryOptionCombo;
	}

	public void setCategoryOptionCombo(String categoryOptionCombo) {
		this.categoryOptionCombo = categoryOptionCombo;
	}

	public String getDataElement() {
		return dataElement;
	}

	public void setDataElement(String dataElement) {
		this.dataElement = dataElement;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getStoredBy() {
		return storedBy;
	}

	public void setStoredBy(String storedBy) {
		this.storedBy = storedBy;
	}

}
