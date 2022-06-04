package org.cubic.loader.urlpath;

import org.cubic.loader.URLPath;
import org.cubic.loader.URLPathNotFoundException;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Cubic
 */
public class URLJarPath extends URLPath {

    private final JarURLConnection jarURLConnection;

    private final JarFile jarFile;

    public URLJarPath(JarURLConnection connection, ClassLoader cl) throws URLPathNotFoundException {
        super(connection.toString(), connection.getURL(), cl);
        this.jarURLConnection = connection;
        JarFile jar;
        try {
            jar = connection.getJarFile();
        } catch (IOException e){
            throw new URLPathNotFoundException(e);
        }
        this.jarFile = jar;
    }

    public URLJarPath(JarURLConnection connection) throws URLPathNotFoundException {
        this(connection, null);
    }

    public URLJarPath(URL url, ClassLoader cl) throws URLPathNotFoundException {
        super(url.toString(), url, cl);
        JarURLConnection connection;
        try {
            connection = (JarURLConnection) url.openConnection();
        } catch (IOException | ClassCastException | NullPointerException e) {
            connection = null;
        }
        this.jarURLConnection = connection;
        if(connection == null){
            jarFile = null;
            return;
        }
        JarFile file;
        try {
            file = connection.getJarFile();
        } catch (IOException e){
            file = null;
        }
        this.jarFile = file;
    }

    public JarURLConnection getJarURLConnection(){
        return jarURLConnection;
    }

    public JarFile getJarFile(){
        return jarFile;
    }

    public JarEntry getJarEntry(String name){
        return jarFile.getJarEntry(name);
    }
}
