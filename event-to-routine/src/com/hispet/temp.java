package com.hispet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class temp {

	public static String catComboFile = "/home/melaeke/dhis/fmoh/data_Migration/Final/scripts/data_Testing_server/categoryoptioncombo.csv";
	public static String dataElementFile = "/home/melaeke/dhis/fmoh/data_Migration/Final/scripts/data_Testing_server/dataelement.csv";
	public static String orgUnitFile = "/home/melaeke/dhis/fmoh/data_Migration/Final/scripts/data_Testing_server/organisationunit.csv";
	public static String periodFile = "/home/melaeke/dhis/fmoh/data_Migration/Final/scripts/data_Testing_server/period.csv";
	/*
	 * public static String[] catComboHeaders=
	 * {"categoryoptioncomboid","uid"};//,"code","created","lastupdated",
	 * "lastupdatedby","name","ignoreapproval"}; public static String[]
	 * dataElementHeaders=
	 * {"dataelementid","uid"};//,"code","created","lastupdated","lastupdatedby",
	 * "name","shortname","description","formname","valuetype","domaintype",
	 * "aggregationtype","categorycomboid","url","zeroissignificant","optionsetid",
	 * "commentoptionsetid","userid","publicaccess","style"}; public static String[]
	 * organisationUnitHeaders=
	 * {"organisationunitid","uid"};//,"code","created","lastupdated",
	 * "lastupdatedby","name","shortname","parentid","path","hierarchylevel",
	 * "description","openingdate","closeddate","comment","featuretype",
	 * "coordinates","url","contactperson","address","email","phonenumber","userid"}
	 * ; public static String[] periodsHeaders=
	 * {"periodid","periodtypeid","startdate","enddate"};
	 */

	public static Map<String, Integer> allCatCombo = new HashMap<String, Integer>();
	public static Map<String, Integer> allDataElements = new HashMap<String, Integer>();
	public static Map<String, Integer> allOrganisationUnits = new HashMap<String, Integer>();
	public static Map<String, Integer> periods = new HashMap<String, Integer>();

	public static void main(String[] args) {

		readCsvAndPutOnMap(catComboFile, "uid", "categoryoptioncomboid", allCatCombo);
		readCsvAndPutOnMap(dataElementFile, "uid", "dataelementid", allDataElements);
		readCsvAndPutOnMap(orgUnitFile, "uid", "organisationunitid", allOrganisationUnits);
		readPeriod();

		gui.currentRunningStatus = 3;
		gui.databaseDumpFilesDirectory = "/home/melaeke/dhis/fmoh/data_Migration/Final/scripts/data_Testing_server";
		File temp = new File("/home/melaeke/dhis/fmoh/data_Migration/Final/Data_final_Aug_7_2019/json");
		File[] files = temp.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith("json")) {
				convertJSONtoCSV(files[i].getAbsolutePath(), files[i].getAbsolutePath() + ".csv");
			}
		}
		/**
		 * Finshed getting appropriate data from server side now start converting.
		 */

		// first read the JSON file and use that to convert it.
		//readJSON("/home/melaeke/dhis/fmoh/data_Migration/Final/Data_final_Aug_7_2019/json/Oromiya_HC_2009.json");

		return;

	}

	private static void convertJSONtoCSV(String jsonFile, String csvOutputFile) {
		System.out.println("processing : "+jsonFile);
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

		DataValue temp = new DataValue();
		temp.getClass();
		temp = null;
		DataValue[] finalDataValue = null;
		try {
			finalDataValue = objectMapper.readValue(new File(jsonFile), DataValue[].class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CsvWriter csvWriter = new CsvWriter(csvOutputFile);
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
		System.out.println("Finished : "+csvOutputFile);
	}

	private static File[] getFilesInFolder(String folderName) {
		File file = new File(folderName);
		return file.listFiles();
	}

	private static void readJSON(String fileName) {
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		JsonNode temp = null;
		System.out.println("Reading tree");
		/*
		 * try { temp = objectMapper.readValue((new File(fileName)); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		System.out.println("Reading Values");

		DataValue[] dataValues = null;
		try {
			dataValues = objectMapper.readValue(new File(fileName), DataValue[].class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < dataValues.length; i++) {
			dataValues[i].inSQLForm();
		}
		System.out.println(dataValues[0].inSQLForm());
		System.out.println(dataValues.length);

	}

	private static void readCsvAndPutOnMap(String fileName, String keyHeader, String valueHeader, Map returnMap) {
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
