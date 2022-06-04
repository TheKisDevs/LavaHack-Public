package com.kisman.cc.util.protect;

import com.kisman.cc.Kisman;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class HWID {
    public static List<String> hwids = new ArrayList<>();

    /**
    *
    * @return HWID in MD5;
    *
    */
    
	public static String getHWID() {
        try{
            String toEncrypt =  System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();
            
            byte byteData[] = md.digest();
            
            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace(); 
        	return "Error";
        }
    }

    public static List<String> getHWIDList() {
        try {
            final URL url = new URL(Kisman.HWIDS_LIST);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                hwids.add(inputLine);
            }
        } catch(Exception e) {
            Kisman.LOGGER.error("Load HWID Failed!");
        }
        return hwids;
    }
}