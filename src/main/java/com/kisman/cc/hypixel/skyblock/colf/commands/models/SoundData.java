package com.kisman.cc.hypixel.skyblock.colf.commands.models;

import com.google.gson.annotations.SerializedName;

public class SoundData {
	@SerializedName("name")
	public String Name;
	
	@SerializedName("pitch")
	public float Pitch;	
	
	public SoundData() {
		super();
	}

	public SoundData(String name, float pitch) {
		super();
		Name = name;
		Pitch = pitch;
	}
	
	
}
