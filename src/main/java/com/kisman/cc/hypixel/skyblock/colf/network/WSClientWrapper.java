package com.kisman.cc.hypixel.skyblock.colf.network;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.kisman.cc.Kisman;
import com.kisman.cc.hypixel.skyblock.colf.commands.Command;
import com.kisman.cc.hypixel.skyblock.colf.minecraft_integration.CoflSessionManager;
import com.neovisionaries.ws.client.WebSocketException;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;


public class WSClientWrapper {
    public WSClient socket;
   // public Thread thread;
    public boolean isRunning;
    
    private String[] uris;

    
    public WSClientWrapper(String[] uris) {
    	this.uris = uris;
    }
    
    public void restartWebsocketConnection() {
    	socket.socket.clearListeners();
    	socket.stop();
    	
    	System.out.println("Sleeping...");
		ChatUtils.warning("Lost connection to Coflnet, trying to reestablish the connection in 2 Seconds...");

    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	socket = new WSClient(socket.uri);
    	isRunning = false;    	
    	start();
    }
    
    
    public boolean startConnection() {
    	
    	if(isRunning)
    		return false;
    	
    	for(String s : uris) {
    		
    		System.out.println("Trying connection with uri=" + s);
    		
    		if(initializeNewSocket(s)) {
    			return true;
    		}
    	}

		ChatUtils.warning("Cofl could not establish a connection to any server!");
    	
    	return false;
    	//throw new Error("Could not connect to any websocket remote!");
    }
    
    
    
    public boolean initializeNewSocket(String uriPrefix) {
    	String uri = uriPrefix;
    	uri += "?version=" + Kisman.VERSION;

    	uri += "&player=" + Minecraft.getMinecraft().session.getUsername();
    	
    	//Generate a CoflSession
    	
    	try {
			CoflSessionManager.UpdateCoflSessions();
			String coflSessionID = CoflSessionManager.GetCoflSession(Minecraft.getMinecraft().session.username).SessionUUID;
			
			uri += "&SId=" + coflSessionID;	
	    	
			socket = new WSClient(URI.create(uri));
			
			boolean successfull = start();
			if(successfull) {
				socket.shouldRun = true;
			}
			return successfull;
    	} catch(IOException e) {
    		e.printStackTrace();
    	}			

		return false;   	
    	
    }
    
    private synchronized boolean start() {
    	if(!isRunning) {
    		try {
    			
				socket.start();
				isRunning = true;

				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WebSocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return false;
    	}
		return false;
    }
    
    public synchronized void stop() {
    	if(isRunning) {
    		socket.shouldRun = false;
    		socket.stop();
    		isRunning = false;
    		socket = null;
    	}
    }
    
    public synchronized void SendMessage(Command cmd){
    	if(this.isRunning) {
    		this.socket.sendCommand(cmd);
    	} else {
			ChatUtils.error("tried sending a callback to coflnet but failed. the connection must be closed.");
    	}
    	
    }

	
	public String GetStatus() {
		return "" + isRunning + " " +  
	    (this.socket!=null ? this.socket.currentState.toString() : "NOT_INITIALIZED");
	}
}
