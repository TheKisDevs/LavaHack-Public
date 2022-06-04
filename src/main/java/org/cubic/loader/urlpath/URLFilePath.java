package org.cubic.loader.urlpath;

import org.cubic.loader.URLPath;
import org.cubic.loader.URLPathNotFoundException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Cubic
 */
public class URLFilePath extends URLPath {

    private final File file;

    private final String filePath;

    public URLFilePath(File file, ClassLoader cl) throws URLPathNotFoundException {
        super(file.getPath(), getURL(file), cl);
        this.file = file;
        this.filePath = file.getAbsolutePath();
    }

    public URLFilePath(String file, ClassLoader cl) throws URLPathNotFoundException {
        this(new File(file), cl);
    }

    public URLFilePath(URL url, ClassLoader cl) throws URLPathNotFoundException {
        super(url.toString(), url, cl);
        this.file = new File(url.getPath());
        this.filePath = file.getAbsolutePath();
    }

    private static URL getURL(File file) throws URLPathNotFoundException {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e){
            throw new URLPathNotFoundException(e);
        }
    }

    public File getFile(){
        return file;
    }

    public String getFilePath(){
        return filePath;
    }
}
