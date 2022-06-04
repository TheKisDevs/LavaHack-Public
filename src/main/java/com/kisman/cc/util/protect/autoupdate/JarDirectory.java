package com.kisman.cc.util.protect.autoupdate;

import java.io.File;
import java.net.URISyntaxException;

public class JarDirectory {
	public static String getJarName() {
		return new File(JarDirectory.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
	}

	public static boolean isRunningFromJAR() {
		String jarName = getJarName();
		return jarName.contains(".jar");
	}

	public static String getProgramDirectory() {
		if (isRunningFromJAR()) return getCurrentJARDirectory();
		else return getCurrentProjectDirectory();
	}

	private static String getCurrentProjectDirectory() {
		return new File("").getAbsolutePath();
	}

	public static String getCurrentJARDirectory() {
		try {return getCurrentJARFilePath().getParent();} catch (URISyntaxException exception) {exception.printStackTrace();}
		throw new IllegalStateException("Unexpected null JAR path");
	}

	public static File getCurrentJARFilePath() throws URISyntaxException {
		return new File(JarDirectory.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
	}
}