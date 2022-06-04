package com.kisman.cc.hypixel.skyblock.colf.commands.models;

import com.google.gson.annotations.SerializedName;

public class AuctionData {
	@SerializedName("auctionId")
	private String auctionId;
	@SerializedName("itemId")
	private String itemId;
	public String getAuctionId() {
		return auctionId;
	}
	public void setAuctionId(String auctionId) {
		this.auctionId = auctionId;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public AuctionData(String auctionId, String itemId) {
		super();
		this.auctionId = auctionId;
		this.itemId = itemId;
	}
	
	public AuctionData() {}
	
}
