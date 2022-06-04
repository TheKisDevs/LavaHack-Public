package com.kisman.cc.module.chat;

import com.kisman.cc.module.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.util.text.TextFormatting;

public class Credits extends Module {
    private String msg = "";
    public Credits() {
        super("Credits", Category.CHAT);
    }

    public void onEnable() {
        msg = "\nCredits:\n";

        add("StupitDog", "Guide of the client base");
        add("CatClient base", "First ClickGui");
        add("HeroCode", "Settings system");
        add("Vega33", "BowExploitRewrite & VegaGui & Jesus -> Mode -> Matrix/Solid, & TargetHud -> Mode -> Vega");
        add("TheRealDiOnFire", "agogus?");
        add("kshk", "Charms");
        add("salam4ik", "TeleportBack & glow  & JumpCircle");
        add("fendy", "BowExploit");
        add("Neko+", "First ColorPicker");
        add("NekoPvP", "ItemFOV");
        add("cum stealer", "SwingAnimation -> Mode -> Hand");
        add("kqllqk", "Custom font");
        add("NoRules", "New CustomFont renderer from .ttf");
        add("SalHack", "First CA & First Scaffold & more utility files");
        add("sedza", "New logo of the client");
        add("superblauberee27", "New CSGO gui");
        add("Phobos 1.3.1", "more .frag files & NoGlitchBlock");
        add("Phobos 1.9.0", "RangeVisualisator");
        add("NEC", "NEC");
        add("RerHack.club", "Help me with AutoRer");
        add("Aurora", "ChatTTF");
        add("Wild 1.0", "The ideas about HotbarModifier and gradient checkbox");
        add("FamilyFunPack", "PigPOV");
        add("jewtricks", "TeamRusherLag");
        add("KamiBlue", "EyeFinder");
        add("GishReloaded", "Box1 in EntityESP");
        add("Some contributors", "some modules");

        ChatUtils.simpleMessage(msg);

        setToggled(false);
    }

    public void add(String name, String desc) {
        msg += TextFormatting.LIGHT_PURPLE + name + ": " + TextFormatting.RESET + desc + "\n";
    }
}
