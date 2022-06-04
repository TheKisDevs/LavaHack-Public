package com.kisman.cc.friend;

import com.kisman.cc.module.client.Config;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class FriendManager {
    public static FriendManager instance;

    private ArrayList<String> friendsName;

    public FriendManager() {
        friendsName = new ArrayList<>();

        instance = this;
    }

    public String getFriendsNames() {
        StringBuilder str = new StringBuilder();

        for(String friend : friendsName) {
            if(friend.isEmpty()) continue;

            str.append(friend).append("\n");
        }

        return str.toString();
    }

    public ArrayList<String> getFriends() {return friendsName;}
    public void addFriend(String name) {if(!friendsName.contains(name.toLowerCase())) friendsName.add(name.toLowerCase());}
    public void removeFriend(String name) {if(!friendsName.isEmpty() && friendsName.contains(name.toLowerCase())) friendsName.remove(name.toLowerCase());}
    public boolean isFriend(EntityPlayer player) {return friendsName.contains(player.getName().toLowerCase()) && Config.instance.friends.getValBoolean();}
    public boolean isFriend(String name) {return friendsName.contains(name.toLowerCase()) && Config.instance.friends.getValBoolean();}
    public void setFriendsList(ArrayList<String> list) {friendsName = list;}
}
