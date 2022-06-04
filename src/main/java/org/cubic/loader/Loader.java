package org.cubic.loader;

import org.cubic.loader.reflect.Instantiator;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The Loader can load class files as instances of them.
 * It can load whole packages or just single classes.
 *
 * You may customize it with:
 *
 * 1. Setting your instantiator:
 * @see #instantiator(Instantiator)
 *
 * 2. Setting the class loader
 * @see #setClassLoader(ClassLoader)
 *
 * 3. Setting filters:
 * @see #filter(Predicate[])
 *
 * 4. Including or excluding class files
 * @see #include(Class)
 * @see #include(String)
 * @see #exclude(Class)
 * @see #exclude(String)
 *
 * @param <E>
 *
 * @author Cubic
 */
public final class Loader<E> {

    private final Set<String> excluded;

    private ClassLoader classLoader;

    private final Class<?> typeClass;

    private Predicate<E>[] filters;

    private Instantiator instantiator;

    public Loader(Class<E> cls){
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.typeClass = cls;
        this.excluded = new HashSet<>(32);
        this.filters = new Predicate[0];
        this.instantiator = Instantiator.getDefault();
    }

    public Loader(Class<E> cls, ClassLoader classLoader){
        this.classLoader = classLoader;
        this.typeClass = cls;
        this.excluded = new HashSet<>(32);
        this.filters = new Predicate[0];
        this.instantiator = Instantiator.getDefault();
    }

    /**
     * Unsafe constructor
     */
    @UnsafeVarargs
    public Loader(ClassLoader classLoader, E... es){
        this.classLoader = classLoader;
        this.typeClass = es.getClass().getComponentType();
        this.excluded = new HashSet<>(32);
        this.filters = new Predicate[0];
        this.instantiator = Instantiator.getDefault();
    }

    /**
     * Unsafe constructor
     */
    @UnsafeVarargs
    public Loader(E... es){
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.typeClass = es.getClass().getComponentType();
        this.excluded = new HashSet<>(32);
        this.filters = new Predicate[0];
        this.instantiator = Instantiator.getDefault();
    }

    AbstractLoader<E> get(URL url){
        if(url == null)
            return new UnknownLoader<>(classLoader);
        String protocol = url.getProtocol();
        if(protocol.equals("file"))
            return new FileLoader<>(classLoader, typeClass, excluded);
        if(protocol.equals("jar"))
            return new JarLoader<>(classLoader, typeClass, excluded);
        // This case should NEVER occur
        return new UnknownLoader<>(classLoader);
    }

    AbstractLoader<E> getOfPackage(String pkg){
        String name = pkg.replace('.', '/');
        return get(classLoader.getResource(name));
    }

    AbstractLoader<E> getOfClass(String cls){
        String name = cls.replace('.', '/') + ".class";
        return get(classLoader.getResource(name));
    }

    AbstractLoader<E> getRaw(String resource){
        return get(classLoader.getResource(resource));
    }

    private boolean exists(String cls){
        try {
            Class.forName(cls, false, classLoader);
            return true;
        } catch (ClassNotFoundException e){
            return false;
        }
    }

    // Class Loader

    public ClassLoader getClassLoader(){
        return classLoader;
    }

    public ClassLoader setClassLoader(ClassLoader cl){
        this.classLoader = cl != null ? cl : Thread.currentThread().getContextClassLoader();
        return this.classLoader;
    }


    // Instantiator

    public void instantiator(Instantiator inst){
        this.instantiator = inst;
    }


    // Filtering

    @SafeVarargs
    public final void filter(Predicate<E>... filters){
        this.filters = filters;
    }


    // Loading

    public List<E> loadFromPackage(String pkg){
        return getOfPackage(pkg).filter(filters).instantiator(instantiator).loadFromPackage(pkg);
    }

    public List<E> loadAllFromPackage(String pkg){
        return getOfPackage(pkg).filter(filters).instantiator(instantiator).loadAllFromPackage(pkg);
    }

    public E loadFromClass(Class<?> cls){
        try {
            Object o = instantiator.instantiate(cls);
            if(typeClass.isAssignableFrom(o.getClass()))
                return (E) o;
        } catch (Exception ignored){
        }
        return null;
    }

    public E loadFromClass(String clsName){
        try {
            Class<?> cls = Class.forName(clsName);
            /*
            Constructor<?> c = cls.getDeclaredConstructor();
            if(!c.isAccessible())
                c.setAccessible(true);
            Object o = c.newInstance();
             */
            Object o = instantiator.instantiate(cls);
            if(typeClass.isAssignableFrom(o.getClass()))
                return (E) o;
        } catch (Exception ignored){
        }
        return null;
    }

    public void exclude(Class<?> cls){
        excluded.add(cls.getName());
    }

    public void exclude(String cls){
        if(!exists(cls))
            return;
        excluded.add(cls);
    }

    public void include(Class<?> cls){
        excluded.remove(cls.getName());
    }

    public void include(String cls){
        if(!exists(cls))
            return;
        excluded.remove(cls);
    }


    // URL paths

    public URLPath getURLPathOfClass(Class<?> cls){
        return getOfClass(cls.getName()).getURLPathOfClass(cls);
    }

    public URLPath getURLPathOfClass(String cls){
        return getOfClass(cls).getURLPathOfClass(cls);
    }

    public URLPath getURLPathOfPackage(Package pkg){
        return getOfPackage(pkg.getName()).getURLPathOfPackage(pkg);
    }

    public URLPath getURLPathOfPackage(String pkg){
        return getOfPackage(pkg).getURLPathOfPackage(pkg);
    }

    public URLPath getURLPath(String resource){
        return getRaw(resource).getURLPath(resource);
    }
}
