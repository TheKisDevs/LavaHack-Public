package com.kisman.cc.hypixel.skyblock.colf.minecraft_integration;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.kisman.cc.hypixel.skyblock.colf.network.WSClient;
import net.minecraft.client.Minecraft;

public class PlayerDataProvider {
	 private static class UUIDHelper {
	    	public String id;
	    	public String name;
	 }
	 
	public static String getActivePlayerUUID() {
		try {
			URL url = new URL("https://api.mojang.com/profiles/minecraft");
	    	HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoInput(true);
			con.setDoOutput(true);

			// ...

			OutputStream os = con.getOutputStream();
			byte[] bytes = ("[\"" + Minecraft.getMinecraft().session.getUsername() + "\"]").getBytes("UTF-8");
			os.write(bytes);
			os.close();
			
			 InputStream in = new BufferedInputStream(con.getInputStream());
			 ByteArrayOutputStream result = new ByteArrayOutputStream();
			 byte[] buffer = new byte[1024];
			 for (int length; (length = in.read(buffer)) != -1; ) {
			     result.write(buffer, 0, length);
			 }
			 // StandardCharsets.UTF_8.name() > JDK 7
			 String resString =  result.toString("UTF-8");
			 
			 System.out.println("Result= " + resString);
			 UUIDHelper[] helpers = WSClient.gson.fromJson(resString, UUIDHelper[].class);
			 if(helpers.length == 1) {
				 return helpers[0].id;
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return UUID.randomUUID().toString();
	}
}
