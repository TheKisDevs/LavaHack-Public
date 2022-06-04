package org.cubic.loader;

/**
 * @author Cubic
 */
public class URLPathNotFoundException extends Exception {

    public URLPathNotFoundException(){
        super();
    }

    public URLPathNotFoundException(String message){
        super(message);
    }

    public URLPathNotFoundException(Throwable cause){
        super(cause);
    }

    public URLPathNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
