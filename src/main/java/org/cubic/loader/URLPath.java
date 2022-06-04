package org.cubic.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * URLPath is an abstraction for a path to a resource location.
 * It may represent different formats, like a path to a package,
 * a file or a class.
 *
 * @author Cubic
 */
public abstract class URLPath {

    // The original resource the url was retrieved from
    protected final String resource;

    // The URL
    protected final URL url;

    // Equivalent to url.getProtocol()
    protected final String protocol;

    // Equivalent to url.getPath()
    protected final String path;

    // Equivalent to url.openConnection()
    protected final URLConnection connection;

    // The class loader the URL was retrieved from
    protected final ClassLoader classLoader;

    public URLPath(String resource, ClassLoader cl) throws URLPathNotFoundException {
        this.classLoader = cl != null ? cl : Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(resource);
        if(url == null)
            throw new URLPathNotFoundException();
        this.resource = resource;
        this.url = url;
        this.protocol = url.getProtocol();
        this.path = url.getPath();
        URLConnection connect;
        try {
            connect = url.openConnection();
        } catch (IOException e){
            connect = null;
        }
        this.connection = connect;
    }

    public URLPath(String resource) throws URLPathNotFoundException {
        this(resource, Thread.currentThread().getContextClassLoader());
    }

    protected URLPath(String resource, URL url, ClassLoader cl) throws URLPathNotFoundException {
        this.classLoader = cl != null ? cl : Thread.currentThread().getContextClassLoader();;
        if(url == null)
            throw new URLPathNotFoundException();
        this.resource = resource;
        this.url = url;
        this.protocol = url.getProtocol();
        this.path = url.getPath();
        URLConnection connect;
        try {
            connect = url.openConnection();
        } catch (IOException e){
            connect = null;
        }
        this.connection = connect;
    }

    protected URLPath(String resource, URL url) throws URLPathNotFoundException {
        this(resource, url, null);
    }

    protected URLPath(String resource, URL url, URLConnection connection, ClassLoader classLoader){
        this.resource = resource;
        this.url = url;
        this.protocol = url.getProtocol();
        this.path = url.getProtocol();
        this.connection = connection;
        this.classLoader = classLoader;
    }

    protected URLPath(String resource, URL url, String protocol, String path, URLConnection connection, ClassLoader classLoader){
        this.resource = resource;
        this.url = url;
        this.protocol = protocol;
        this.path = path;
        this.connection = connection;
        this.classLoader = classLoader;
    }

    public String getResource(){
        return resource;
    }

    public URL getURL(){
        return url;
    }

    public String getProtocol(){
        return protocol;
    }

    public String getPath(){
        return path;
    }

    public URLConnection getConnection(){
        return connection;
    }

    public ClassLoader getClassLoader(){
        return classLoader;
    }

    @Override
    public String toString(){
        return this.getClass().getName() + ":" + url.toString() + ":" + classLoader.getClass().getName();
        //return url.toString();
    }
}
