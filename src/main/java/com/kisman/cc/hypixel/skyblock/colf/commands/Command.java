package com.kisman.cc.hypixel.skyblock.colf.commands;

import com.google.gson.annotations.SerializedName;

public class Command<T> {
	@SerializedName("type")
	private CommandType Type;
	@SerializedName("data")
	private T data;

	public Command() {
	}

	public Command(CommandType type, T data) {
		super();
		Type = type;
		this.data = data;
	}

	public CommandType getType() {
		return Type;
	}

	public void setType(CommandType type) {
		Type = type;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Command [Type=" + Type + ", data=" + data + "]";
	}	

}