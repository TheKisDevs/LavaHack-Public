package org.cubic.loader.urlpath;

import org.cubic.loader.URLPath;
import org.cubic.loader.URLPathNotFoundException;

/**
 * @author Cubic
 */
public class DefaultURLPath extends URLPath {

    public DefaultURLPath(String resource, ClassLoader cl) throws URLPathNotFoundException {
        super(resource, cl);
    }

    public DefaultURLPath(String resource) throws URLPathNotFoundException {
        super(resource);
    }
}
