package com.kisman.cc.hypixel.skyblock.colf.commands.models;

import com.google.gson.annotations.SerializedName;

public class ChatMessageData {
	@SerializedName("text")
	public String Text;
	@SerializedName("onClick")
	public String OnClick;
	
	@SerializedName("hover")
	public String Hover;
	
	public ChatMessageData() {

	}

	public ChatMessageData(String text, String onClick, String hover) {
		super();
		Text = text;
		OnClick = onClick;
		Hover = hover;
	}	
	
}
