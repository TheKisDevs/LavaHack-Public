package com.kisman.cc.hypixel.skyblock.colf.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kisman.cc.hypixel.skyblock.colf.MainColf;


public class QueryServerCommands {
	
	private static Gson gson = new GsonBuilder().create();
	
	public static String QueryCommands() {
		String queryResult = GetRequest(MainColf.commandUri);
		
		if(queryResult != null) {
			CommandInfo[] commands = gson.fromJson(queryResult, CommandInfo[].class);
			
			System.out.println(">>> "+Arrays.toString(commands));
			
			StringBuilder sb = new StringBuilder();
			
			if(commands.length>0) {
				for(CommandInfo cm : commands) {
					sb.append(cm + "\n");
				}
			}
			return sb.toString().trim();
			
		}
		
		return "§4ERROR: Could not connect to command server!";
	}
	
	private static class CommandInfo {
		
		public String subCommand;
		public String description;
		
		public CommandInfo() {}
		
		public CommandInfo(String subCommand, String description) {
			super();
			this.subCommand = subCommand;
			this.description = description;
		}

		@Override
		public String toString() {
			return subCommand + ": " + description;
		}
	}

	private static String GetRequest(String uri) {
		try {
			System.out.println("Get request");
			URL url = new URL(uri);
	    	HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			//con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("User-Agent", "CoflMod");
			//con.setDoInput(true);
			con.setDoInput(true);

			// ...

			/*OutputStream os = con.getOutputStream();
			byte[] bytes = ("[\"" + getUsername() + "\"]").getBytes("UTF-8");
			os.write(bytes);
			os.close();
			*/
			System.out.println("InputStream");
			 InputStream in = new BufferedInputStream(con.getInputStream());
			 ByteArrayOutputStream result = new ByteArrayOutputStream();
			 byte[] buffer = new byte[1024];
			 for (int length; (length = in.read(buffer)) != -1; ) {
			     result.write(buffer, 0, length);
			 }
			 // StandardCharsets.UTF_8.name() > JDK 7
			 String resString =  result.toString("UTF-8");
			 
			 System.out.println("Result= " + resString);
			 return resString;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
