package com.kisman.cc.util.process.web.microsoft;

import java.nio.charset.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class PostRequest {
    public HttpURLConnection conn;
    
    public PostRequest(final String uri) throws Exception {
        (this.conn = (HttpURLConnection)new URL(uri).openConnection()).setRequestMethod("POST");
        this.conn.setDoOutput(true);
        this.conn.setDoInput(true);
    }
    
    public PostRequest header(final String key, final String value) {
        this.conn.setRequestProperty(key, value);
        return this;
    }
    
    public void post(final String s) throws IOException {
        final byte[] out = s.getBytes(StandardCharsets.UTF_8);
        this.conn.connect();
        final OutputStream os = this.conn.getOutputStream();
        os.write(out);
        os.flush();
        os.close();
    }
    
    public void post(final Map<Object, Object> map) throws Exception {
        final StringJoiner sj = new StringJoiner("&");
        for (final Map.Entry<Object, Object> entry : map.entrySet()) sj.add(URLEncoder.encode(entry.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        this.post(sj.toString());
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
