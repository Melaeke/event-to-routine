package com.hispet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.*;

public class Main {

	public static void main(String[] args) {
		JSONObject tomJsonObj = new JSONObject();

		tomJsonObj.put("name", "Tom");
		tomJsonObj.put("birthDay", "1940-02-10");

		tomJsonObj.put("fav_food", new String[] { "cookie", "fish" });

		System.out.println(tomJsonObj.toString(1));
		
		try {
			readJSON("/home/melaeke/dhis/development/Afar.json");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readJSON(String fileName) throws IOException {
		File file=new File(fileName);
		
		//String content = FileUtils.readFileToString(file,"utf-8");
		
		InputStream is = new FileInputStream(file);
		
		BufferedReader buff= new BufferedReader(new InputStreamReader(is));
		
		String line = buff.readLine();
		StringBuilder sb = new StringBuilder();
		
		while (line!=null) {
			sb.append(line).append("\n");
			line=buff.readLine();
		}
		
		String stringFile= sb.toString();
		
		JSONObject obj = new JSONObject(stringFile);
		System.out.println("finished reading file");
		
		
	}
}
