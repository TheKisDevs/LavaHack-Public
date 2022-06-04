package com.kisman.cc.hypixel.skyblock.colf.network;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kisman.cc.hypixel.skyblock.colf.MainColf;
import com.kisman.cc.hypixel.skyblock.colf.commands.Command;
import com.kisman.cc.hypixel.skyblock.colf.commands.JsonStringCommand;
import com.neovisionaries.ws.client.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.Minecraft;

public class WSClient extends WebSocketAdapter {
	public static Gson gson;

	static {
		gson = new GsonBuilder()/*.setFieldNamingStrategy(new FieldNamingStrategy() {
			@Override
			public String translateName(Field f) {
				
				String name = f.getName();
				char firstChar = name.charAt(0);
				return Character.toLowerCase(firstChar) + name.substring(1);
			}
		})*/.create();
	}
	public URI uri;
	public WebSocket socket;
	public boolean shouldRun = false;
	public WebSocketState currentState = WebSocketState.CLOSED;
	
	public WSClient(URI uri) {
		this.uri = uri;
		
	}
	
	public void start() throws IOException, WebSocketException, NoSuchAlgorithmException {
		WebSocketFactory factory = new WebSocketFactory();
		
		/*// Create a custom SSL context.
		SSLContext context = NaiveSSLContext.getInstance("TLS");

		// Set the custom SSL context.
		factory.setSSLContext(context);

		// Disable manual hostname verification for NaiveSSLContext.
		//
		// Manual hostname verification has been enabled since the
		// version 2.1. Because the verification is executed manually
		// after Socket.connect(SocketAddress, int) succeeds, the
		// hostname verification is always executed even if you has
		// passed an SSLContext which naively accepts any server
		// certificate. However, this behavior is not desirable in
		// some cases and you may want to disable the hostname
		// verification. You can disable the hostname verification
		// by calling WebSocketFactory.setVerifyHostname(false).
		factory.setVerifyHostname(false);
		factory.*/
		factory.setVerifyHostname(false);
		factory.setSSLContext(NaiveSSLContext.getInstance("TLSv1.2"));
		factory.setConnectionTimeout(10*1000);
		this.socket = factory.createSocket(uri);
		this.socket.addListener(this);
		this.socket.connect();
	}
	
	public void stop() {
		System.out.println("Closing Socket");
	//	socket.sendClose();
		socket.clearListeners();
	
		socket.disconnect();
		/*try {
			socket.getConnectedSocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WebSocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println("Socket closed");
	}
	
	@Override
	public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
		System.out.println("WebSocket Changed state to: " + newState);
		currentState = newState;
		
		if(newState == WebSocketState.CLOSED && shouldRun) {
			MainColf.wrapper.restartWebsocketConnection();
		}
		
		super.onStateChanged(websocket, newState);
	}

	 @Override
	    public void onTextMessage(WebSocket websocket, String text) throws Exception{
		//super.onTextMessage(websocket, text);
		 System.out.println("Received: "+ text);
		JsonStringCommand cmd = gson.fromJson(text, JsonStringCommand.class);
		//System.out.println(cmd);
		 ChatUtils.message(cmd);
		
	}

	public void sendCommand(Command cmd) {
		String json = gson.toJson(cmd);
		this.socket.sendText(json);
	}
}