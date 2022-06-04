package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import com.kisman.cc.util.process.file.ClipboardImage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * @author BloomWareClient
 */
public class BetterScreenshot extends Module {
    public static BetterScreenshot instance;

    public BetterScreenshot() {
        super("BetterScreenshot", "offix sori no mne 'eto otchen nado ni termay repy poshaluysta", Category.MISC);

        instance = this;
    }

    public static Image getLatestScreenshot() throws IOException {
        return new ImageIcon(Files.list((new File(mc.mcDataDir.getAbsolutePath() + "/screenshots/")).toPath()).filter(f -> !Files.isDirectory(f)).max(Comparator.comparingLong(f -> f.toFile().lastModified())).get().toString()).getImage();
    }

    public static void copyToClipboard(Image image) {
        new Thread(() -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ClipboardImage(image), null)).start();
    }
}
