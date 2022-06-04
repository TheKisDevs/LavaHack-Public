package com.kisman.cc.util.process.web.microsoft;

import java.net.*;
import java.nio.charset.*;
import java.io.*;

public class GetRequest {
    public HttpURLConnection conn;
    
    public GetRequest(final String uri) throws Exception {
        (this.conn = (HttpURLConnection)new URL(uri).openConnection()).setRequestMethod("GET");
    }
    
    public GetRequest header(final String key, final String value) {
        this.conn.addRequestProperty(key, value);
        return this;
    }
    
    public void get() throws IOException {
        this.conn.connect();
    }
    
    public int response() throws IOException {
        return this.conn.getResponseCode();
    }
    
    public String body() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final Reader r = new InputStreamReader(this.conn.getInputStream(), StandardCharsets.UTF_8);
        int i;
        while ((i = r.read()) >= 0) sb.append((char)i);
        r.close();
        return sb.toString();
    }
}
