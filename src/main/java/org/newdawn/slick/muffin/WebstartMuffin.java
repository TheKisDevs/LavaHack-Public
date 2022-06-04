package org.newdawn.slick.muffin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.newdawn.slick.util.Log;

/**
 * A muffin load/save implementation based on using Webstart Muffins (a bit like cookies only 
 * for webstart)
 * 
 * @author kappaOne
 */
public class WebstartMuffin implements Muffin {

	/**
	 * @see Muffin#saveFile(HashMap, String)
	 */
	public void saveFile(HashMap scoreMap, String fileName) throws IOException {

	}

	/**
	 * @see Muffin#loadFile(String)
	 */
	public HashMap loadFile(String fileName) throws IOException {
		HashMap hashMap = new HashMap();

		return hashMap;
	}
}
