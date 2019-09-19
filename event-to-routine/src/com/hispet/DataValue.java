package com.hispet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.csvreader.CsvReader;

public class DataValue {

	private static String catComboFile = "/categoryoptioncombo.csv";
	private static String dataElementFile = "/dataelement.csv";
	private static String orgUnitFile = "/organisationunit.csv";
	private static String periodFile = "/period.csv";

	private static Map<String, Integer> allCatCombo = new HashMap<String, Integer>();
	private static Map<String, Integer> allDataElements = new HashMap<String, Integer>();
	private static Map<String, Integer> allOrganisationUnits = new HashMap<String, Integer>();
	private static Map<String, Integer> periods = new HashMap<String, Integer>();
	private static String date = new Date().toString();

	// --------------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------------

	protected String dataElement;

	protected String period;

	protected String orgUnit;

	protected String categoryOptionCombo;

	protected String attributeOptionCombo;

	protected String value;

	protected String storedBy;

	protected String lastUpdated;
	
	protected String created;

	public String getDataElement() {
		return dataElement;
	}

	public void setDataElement(String dataElement) {
		this.dataElement = dataElement;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	public String getAttributeOptionCombo() {
		return attributeOptionCombo;
	}

	public void setAttributeOptionCombo(String attributeOptionCombo) {
		this.attributeOptionCombo = attributeOptionCombo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValueForced(String value) {
		this.value = value;
	}

	public String getStoredBy() {
		return storedBy;
	}

	public void setStoredBy(String storedBy) {
		this.storedBy = storedBy;
	}

	public String getlastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	// --------------------------------------------------------------------------
	// To be able to output a string that can be used to insert directly to DB
	// --------------------------------------------------------------------------

	public String inSQLForm() {
		String temp = allDataElements.get(this.dataElement) + "," + periods.get(this.period) + ","
				+ allOrganisationUnits.get(this.orgUnit) + "," + allCatCombo.get(this.categoryOptionCombo) + ","
				+ allCatCombo.get(this.attributeOptionCombo) + "," + this.value + "," + this.storedBy + "," + date + ","// created
				+ date + ","// lastUpdated
				+ "eventMigration,"// comment
				+ "false,"// followup
				+ "false";// deleted

		/*
		 * String temp = "(" + allDataElements.get(this.dataElement) + "," +
		 * periods.get(this.period) + "," + allOrganisationUnits.get(this.orgUnit) + ","
		 * + allCatCombo.get(this.categoryOptionCombo) + "," +
		 * allCatCombo.get(this.attributeOptionCombo) + "," + this.value + ",'" +
		 * this.storedBy + "','" + this.date+ "','"// created + this.date + "',"//
		 * lastUpdated + "'eventMigration',"// comment + "false,"// followup +
		 * "false)";// deleted
		 * 
		 */
		boolean errorOccured = false;
		if (allDataElements.get(this.dataElement) == null) {
			System.out.println("Error: Data Element doesn't exist in database >> " + this.dataElement);
			errorOccured = true;
		}
		if (periods.get(this.period) == null) {
			if (!this.period.endsWith("00")) {
				System.out.println("Error: Period doesn't exist in database >> " + this.period);
			}
			errorOccured = true;
		}
		if (allOrganisationUnits.get(this.orgUnit) == null) {
			System.out.println("Error: OrgUnit doesn't exist in database >> " + this.orgUnit);
			errorOccured = true;
		}
		if (allCatCombo.get(this.categoryOptionCombo) == null) {
			System.out.println("Error: Category option combo doesn't exist in database >> " + this.categoryOptionCombo);
			errorOccured = true;
		}
		if (allCatCombo.get(this.attributeOptionCombo) == null) {
			System.out
					.println("Error: attribute Option combo doesn't exist in database >> " + this.attributeOptionCombo);
			errorOccured = true;
		}
		if (errorOccured) {
			return null;
		} else {
			return temp;
		}
	}

	static {
		if (gui.currentRunningStatus == gui.RUNNING_CSV_EXPORT_ONLY
				|| gui.currentRunningStatus == gui.RUNNING_JSON_AND_CSV_EXPORT) {
			/**
			 * If the conversion requires to output csv database dump, read the database
			 * table dump files.
			 */
			catComboFile = gui.databaseDumpFilesDirectory + catComboFile;
			orgUnitFile = gui.databaseDumpFilesDirectory + orgUnitFile;
			dataElementFile = gui.databaseDumpFilesDirectory + dataElementFile;
			periodFile = gui.databaseDumpFilesDirectory + periodFile;
			readCsvAndPutOnMap(catComboFile, "uid", "categoryoptioncomboid", allCatCombo);
			readCsvAndPutOnMap(dataElementFile, "uid", "dataelementid", allDataElements);
			readCsvAndPutOnMap(orgUnitFile, "uid", "organisationunitid", allOrganisationUnits);
			readPeriod();
			System.out.println("Finished initialization");
		}
		System.out.println("Initializing static method in main finished");
	}

	private static void readCsvAndPutOnMap(String fileName, String keyHeader, String valueHeader,
			Map<String, Integer> returnMap) {
		try {
			CsvReader reader = new CsvReader(fileName);
			reader.readHeaders();
			while (reader.readRecord()) {
				returnMap.put(reader.get(keyHeader), Integer.parseInt(reader.get(valueHeader)));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void readPeriod() {
		try {
			CsvReader reader = new CsvReader(periodFile);
			reader.readHeaders();
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
			while (reader.readRecord()) {
				if (Integer.parseInt(reader.get("periodtypeid")) == 7) {
					// This means that it is a monthly period type
					String startDate = reader.get("startdate");
					DateTime timeInEthiopian = formatter.parseDateTime(startDate)
							.withChronology(EthiopicChronology.getInstance());
					String periodName = timeInEthiopian.getYear() + ""
							+ String.format("%02d", timeInEthiopian.getMonthOfYear());
					periods.put(periodName, Integer.parseInt(reader.get("periodid")));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
