package com.kisman.cc.util.protect.autoupdate;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.autoupdate.UpdateGui;
import com.kisman.cc.util.Globals;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class AutoUpdateProcess implements Globals {
    public static boolean needsUpdating() {
        String[] data = getData();
        if (!data[0].equalsIgnoreCase(Kisman.VERSION)) {

            mc.displayGuiScreen(new UpdateGui());
            (new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);

                        downloadClient(data[1]);
                    } catch (Exception ignored) {}
                }
            }).start();
            return true;
        }
        return false;
    }

    private static String[] getData() {
        String version = "";
        String url = "";
        try {
            String body = readString("https://pastebin.com/raw/kNK2fi63");
            version = body.split(" ")[0];
            url = body.split(" ")[1];
        } catch (Exception ignored) {}
        return new String[] { version.replaceAll("[\\\r\\\n]+", ""), url };
    }

    private static String readString(String url) throws IOException {
        try (InputStream inputStream = new URL(url).openStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private static void downloadClient(String url) {
        try {
            String path = JarDirectory.getCurrentJARDirectory();
            File file = new File(path, JarDirectory.getJarName());
            if (Downloader.download(new URL(url), file) != null) selfDestructionJar();
        } catch (Exception e) {
            e.printStackTrace();
            mc.shutdown();
        }
    }

    private static void selfDestructWindowsJARFile() throws Exception {
        String currentJARFilePath = JarDirectory.getCurrentJARFilePath().toString();
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd /c ping localhost -n 2 > nul && del \"" + currentJARFilePath + "\"");
    }

    private static void selfDestructionJar() throws Exception {
        if (SystemUtils.IS_OS_WINDOWS) selfDestructWindowsJARFile();
        else {
            File directoryFilePath = JarDirectory.getCurrentJARFilePath();
            Files.delete(directoryFilePath.toPath());
        }
        mc.shutdown();
    }
}
