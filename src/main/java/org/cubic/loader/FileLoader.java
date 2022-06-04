package org.cubic.loader;

import org.cubic.loader.urlpath.URLFilePath;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class FileLoader<E> extends AbstractLoader<E> {

    private final Class<?> typeClass;

    FileLoader(ClassLoader classLoader, Class<?> cls, Set<String> excluded) {
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
        URLFilePath urlFilePath = (URLFilePath) getURLPathOfPackage(pkg);
        searchClasses(urlFilePath.getFile(), pkg, list, search);
        return list;
    }

    private void searchClasses(File directory, String pkg, List<E> list, boolean search){
        File tmp;
        if(!directory.exists() || !directory.isDirectory())
            return;
        String[] files = directory.list();
        if(files == null)
            return;
        for(String file : files){
            tmp = new File(directory, file);
            if(search && tmp.isDirectory())
                searchClasses(tmp, pkg + "." + file, list, true);
            if(!file.endsWith(".class"))
                continue;
            String name = pkg + "." + file.substring(0, file.length() - 6);
            try {
                Class<?> cls = Class.forName(name); // pkg + "." + file.substring(0, file.length() - 6)
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
        File file = new File(url.getPath());
        return urlFilePath(file);
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
        File file = new File(url.getPath());
        return urlFilePath(file);
    }

    @Override
    URLPath getURLPath(String resource) {
        URL url = classLoader.getResource(resource);
        if(url == null)
            return null;
        return defaultURLPath(resource);
    }
}
