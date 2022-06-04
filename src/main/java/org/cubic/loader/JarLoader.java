package org.cubic.loader;

import org.cubic.loader.urlpath.URLJarPath;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class JarLoader<E> extends AbstractLoader<E> {

    private final Class<?> typeClass;

    JarLoader(ClassLoader classLoader, Class<?> cls, Set<String> excluded) {
        super(classLoader, excluded);
        this.typeClass = cls;
    }

    @Override
    List<E> loadFromPackage(String pkg) {
        return resolve(pkg, false);
    }

    @Override
    List<E> loadAllFromPackage(String pkg) {
        return resolve(pkg, true);
    }

    private List<E> resolve(String pkg, boolean search){
        List<E> list = new ArrayList<>();
        URLJarPath urlJarPath = (URLJarPath) getURLPathOfPackage(pkg);
        if(urlJarPath == null)
            return list;
        JarFile file = urlJarPath.getJarFile();
        if(file == null)
            return list;
        searchClasses(pkg, file, list, search);
        return list;
    }

    private void searchClasses(String pkg, JarFile jarFile, List<E> list, boolean search){
        Enumeration<JarEntry> entries = jarFile.entries();
        for(JarEntry jarEntry; entries.hasMoreElements() && (jarEntry = entries.nextElement()) != null;){
            String name = jarEntry.getName();
            if(!name.endsWith(".class"))
                continue;
            name = name.replace('/', '.').substring(0, name.length() - 6);
            if(search){
                if(!name.startsWith(pkg))
                    continue;
                try {
                    Class<?> cls = Class.forName(name);
                    if(excluded.contains(name))
                        continue;
                    /*
                    Constructor<?> c = cls.getDeclaredConstructor();
                    if(!c.isAccessible())
                        c.setAccessible(true);
                    Object o = c.newInstance();
                     */
                    Object o = instantiator.instantiate(cls);
                    if(instanceOf(o.getClass(), typeClass)){
                        E e = (E) o;
                        if(applyFilter(e))
                            list.add(e);
                    }
                } catch (Exception ignored){
                }
            } else {
                if(!name.startsWith(pkg))
                    continue;
                if(name.lastIndexOf('.') > pkg.length())
                    continue;
                try {
                    Class<?> cls = Class.forName(name);
                    if(excluded.contains(name))
                        continue;
                    /*
                    Constructor<?> c = cls.getDeclaredConstructor();
                    if(!c.isAccessible())
                        c.setAccessible(true);
                    Object o = c.newInstance();
                     */
                    Object o = instantiator.instantiate(cls);
                    if(instanceOf(o.getClass(), typeClass)){
                        E e = (E) o;
                        if(applyFilter(e))
                            list.add(e);
                    }
                } catch (Exception ignored){
                }
            }
        }
    }

    @Override
    URLPath getURLPathOfClass(Class<?> cls) {
        return getURLPathOfClass(cls.getName());
    }

    @Override
    URLPath getURLPathOfClass(String cls) {
        try {
            Class.forName(cls, false, classLoader);
        } catch (ClassNotFoundException ignored){
            return null;
        }
        String name = formatClass(cls);
        URL url = classLoader.getResource(name);
        if(url == null)
            return null;
        JarURLConnection connection;
        try {
            connection = (JarURLConnection) url.openConnection();
        } catch (IOException | NullPointerException | ClassCastException e){
            return null;
        }
        return urlJarPath(connection);
    }

    @Override
    URLPath getURLPathOfPackage(Package pkg) {
        return getURLPathOfPackage(pkg.getName());
    }

    @Override
    URLPath getURLPathOfPackage(String pkg) {
        if(Package.getPackage(pkg) == null)
            return null;
        String name = formatPackage(pkg);
        URL url = classLoader.getResource(name);
        if(url == null)
            return null;
        JarURLConnection connection;
        try {
            connection = (JarURLConnection) url.openConnection();
        } catch (IOException | NullPointerException | ClassCastException e){
            return null;
        }
        return urlJarPath(connection);
    }

    @Override
    URLPath getURLPath(String resource) {
        URL url = classLoader.getResource(resource);
        if(url == null)
            return null;
        return defaultURLPath(resource);
    }
}
