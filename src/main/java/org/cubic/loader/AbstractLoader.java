package org.cubic.loader;

import org.cubic.loader.reflect.Instantiator;
import org.cubic.loader.urlpath.DefaultURLPath;
import org.cubic.loader.urlpath.URLFilePath;
import org.cubic.loader.urlpath.URLJarPath;

import java.io.File;
import java.net.JarURLConnection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

abstract class AbstractLoader<E> {

    final Set<String> excluded;

    ClassLoader classLoader;

    Predicate<E>[] filter;

    Instantiator instantiator;

    AbstractLoader(ClassLoader classLoader, Set<String> excluded){
        this.classLoader = classLoader != null ? classLoader : contextClassLoader();
        this.excluded = excluded;
    }

    final ClassLoader getClassLoader(){
        return classLoader;
    }

    final void setClassLoader(ClassLoader cl){
        classLoader = cl != null ? cl : contextClassLoader();
    }

    ClassLoader contextClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    String formatClass(String cls){
        return cls.replace('.', '/') + ".class";
    }

    String formatPackage(String pkg){
        return pkg.replace('.', '/');
    }

    URLFilePath urlFilePath(File file){
        try {
            return new URLFilePath(file, classLoader);
        } catch (URLPathNotFoundException ignored){
            return null;
        }
    }

    URLJarPath urlJarPath(JarURLConnection connection){
        try {
            return new URLJarPath(connection, classLoader);
        } catch (URLPathNotFoundException ignored){
            return null;
        }
    }

    DefaultURLPath defaultURLPath(String resource){
        try {
            return new DefaultURLPath(resource, classLoader);
        } catch (URLPathNotFoundException ignored){
            return null;
        }
    }

    AbstractLoader<E> filter(Predicate<E>[] filter){
        this.filter = filter;
        return this;
    }

    AbstractLoader<E> instantiator(Instantiator inst){
        this.instantiator = inst;
        return this;
    }

    boolean applyFilter(E e){
        if(filter.length < 1)
            return true;
        for(Predicate<E> predicate : filter)
            if(!predicate.test(e))
                return false;
        return true;
    }


    boolean instanceOf(Class<?> x, Class<?> y){
        return y.isAssignableFrom(x);
    }

    abstract List<E> loadFromPackage(String pkg);

    abstract List<E> loadAllFromPackage(String pkg);


    abstract URLPath getURLPathOfClass(Class<?> cls);

    abstract URLPath getURLPathOfClass(String cls);

    abstract URLPath getURLPathOfPackage(Package pkg);

    abstract URLPath getURLPathOfPackage(String pkg);

    abstract URLPath getURLPath(String resource);
}
