package com.kisman.cc.api.util;

import com.kisman.cc.Kisman;
import com.kisman.cc.api.util.exception.PasteBinBufferedReaderException;

import java.io.*;
import java.net.URL;
import java.util.*;

public class PasteBinAPI {
    public String url;

    public PasteBinAPI(String url) {
        this.url = url;
    }

    public List<String> get() {
        List<String> list = new ArrayList<>();
        try {
            final URL url = new URL(Kisman.HWIDS_LIST);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                list.add(inputLine);
            }
        } catch(Exception e) {
            throw new PasteBinBufferedReaderException("Reading URL(" + url + ") failed!");
        }
        return list;
    }
}
