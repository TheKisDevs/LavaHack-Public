package com.kisman.cc.gui.alts.microsoft;

import java.util.*;

import com.kisman.cc.util.process.web.microsoft.AuthSys;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import java.io.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;

public class MSAuthScreen extends GuiScreen {
    public static final String[] symbols;
    public GuiScreen prev;
    public List<String> text;
    public boolean endTask;
    public int tick;
    
    public MSAuthScreen(final GuiScreen prev) {
        this.text = new ArrayList<>();
        this.endTask = false;
        this.prev = prev;
        AuthSys.start(this);
    }
    
    public void initGui() {
        this.addButton(new GuiButton(0, this.width / 2 - 50, this.height - 24, 100, 20, "Cancel"));
    }
    
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.id == 0) this.mc.displayGuiScreen(this.prev);
    }
    
    public void updateScreen() {
        ++this.tick;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float delta) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Microsoft Auth", this.width / 2, 10, -1);
        for (int i = 0; i < this.text.size(); ++i) this.drawCenteredString(this.fontRenderer, this.text.get(i), this.width / 2, this.height / 2 + i * 10, -1);
        if (!this.endTask) this.drawCenteredString(this.fontRenderer, MSAuthScreen.symbols[this.tick % MSAuthScreen.symbols.length], this.width / 2, this.height / 3 * 2, -256);
        super.drawScreen(mouseX, mouseY, delta);
    }
    
    public void onGuiClosed() {
        AuthSys.stop();
        super.onGuiClosed();
    }
    
    public void setState(final String s) {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> {
            text = mc.fontRenderer.listFormattedStringToWidth(s, this.width);
        });
    }
    
    public void error(final String error) {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() ->    {
            this.text = mc.fontRenderer.listFormattedStringToWidth(TextFormatting.RED + "Error: " + error , this.width);
            this.endTask = true;
        });
    }
    
    static {
        symbols = new String[] { "\u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453", "_ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e", "_ _ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026", "_ _ _ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020", "_ _ _ _ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021", "_ _ _ _ _ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac", "_ _ _ _ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021", "_ _ _ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020", "_ _ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026", "_ \u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e", "\u0432\u2013\u0453 \u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453", "\u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _", "\u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _ _", "\u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _ _ _", "\u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _ _ _ _", "\u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _ _ _ _ _", "\u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _ _ _ _", "\u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _ _ _", "\u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _ _", "\u0432\u2013\u201e \u0432\u2013\u2026 \u0432\u2013\u2020 \u0432\u2013\u2021 \u0432\u2013\u20ac \u0432\u2013\u2021 \u0432\u2013\u2020 \u0432\u2013\u2026 \u0432\u2013\u201e \u0432\u2013\u0453 _" };
    }
}
