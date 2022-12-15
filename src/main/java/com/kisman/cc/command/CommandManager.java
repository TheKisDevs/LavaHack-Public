package com.kisman.cc.command;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.commands.*;

import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class CommandManager {
    public static ArrayList<Command> commands = new ArrayList<Command>();
	
	public char cmdPrefix = ';';
	public String cmdPrefixStr = "" + cmdPrefix;

	public CommandManager()
	{
		addCommands();
	}

	public void addCommands() {
		commands.add(new AntiSpammerCommand());
		commands.add(new Bind());
		commands.add(new ColfCmd());
		commands.add(new Credits());
		commands.add(new DDOSCommand());
		commands.add(new Flip());
		commands.add(new FriendCommand());
		commands.add(new Help());
		commands.add(new LoadConfigCommand());
		commands.add(new LuaCommand());
        commands.add(new Slider());
        commands.add(new OpenDir());
		commands.add(new Peek());
        commands.add(new SaveConfigCommand());
		commands.add(new SetKey());
        commands.add(new Toggle());
		commands.add(new Tp());
	}

	public void runCommands(String s) {
		String readString = s.trim().substring(Character.toString(cmdPrefix).length()).trim();
		boolean commandResolved = false;
		boolean hasArgs = readString.trim().contains(" ");
		String commandName = hasArgs ? readString.split(" ")[0] : readString.trim();
		String[] args = hasArgs ? readString.substring(commandName.length()).trim().split(" ") : new String[0];

		for(Command command : commands) {
			if(command.getCommand().trim().equalsIgnoreCase(commandName.trim())) {
				command.runCommand(readString, args);
				commandResolved = true;
				break;
			}
		}
		if(!commandResolved) ChatUtils.error("Cannot resolve internal command: \u00a7c" + commandName);
	}

	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent event) {
		if (Wrapper.INSTANCE.mc().currentScreen != null) return;
		for(Command cmd : commands) if(cmd.getKey() == Keyboard.getEventKey()) Kisman.instance.commandManager.runCommands("." + cmd.getExecute());
	}
}
