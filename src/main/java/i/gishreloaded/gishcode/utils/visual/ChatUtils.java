package i.gishreloaded.gishcode.utils.visual;

import com.kisman.cc.Kisman;

import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.util.text.*;

public class ChatUtils {
	// TODO Rewrite to LogManager
	
	public static void component(ITextComponent component) {
		if(Wrapper.INSTANCE.player() == null || Wrapper.INSTANCE.mc().ingameGUI.getChatGUI() == null)
			return;
			Wrapper.INSTANCE.mc().ingameGUI.getChatGUI()
				.printChatMessage(new TextComponentTranslation(TextFormatting.WHITE + "")
					.appendSibling(component));
	}

	public static void simpleMessage(Object message) {
		component(new TextComponentTranslation((String) message));
	}
	
	public static void message(Object message) {
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.WHITE + Kisman.getName() + TextFormatting.GRAY + "] " + message));
	}

	public static void complete(Object message) {
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.LIGHT_PURPLE + Kisman.getName() + TextFormatting.GRAY + "] " + message));
	}
	
	public static void warning(Object message) {
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.GOLD + Kisman.getName() + TextFormatting.GRAY + "] " + message));
	}
	
	public static void error(Object message) {
		component(new TextComponentTranslation(TextFormatting.GRAY + "[" + TextFormatting.RED + Kisman.getName() + TextFormatting.GRAY + "] " + message));
	}
}
