package com.kisman.cc.util;

import org.cubic.loader.Loader;
import org.cubic.loader.reflect.Instantiator;

import java.lang.reflect.Field;
import java.util.List;

public final class ReflectUtil extends SecurityManager {

    private ReflectUtil(){
    }

    private final static ReflectUtil instance;

    private static Class<?>[] stack;

    static {
        instance = new ReflectUtil();
        stack = instance.getClassContext();
    }

    public static Class<?> getCallerClass(){
        stack = instance.getClassContext();
        if(stack.length < 3)
            return null;
        return stack[2];
    }

    public static Class<?> getCallerClass(int depth){
        stack = instance.getClassContext();
        if(stack.length < depth + 1)
            return null;
        return stack[depth];
    }

    public static <T> T createInstance(Class<T> cls){
        return new Loader<T>(cls).loadFromClass(cls);
    }

    public static <T> T createInstance(Class<T> cls, Instantiator instantiator){
        Loader<T> loader = new Loader<>(cls);
        loader.instantiator(instantiator);
        return loader.loadFromClass(cls);
    }

    public static <T> List<T> load(Class<T> cls, String pkg){
        return new Loader<>(cls).loadFromPackage(pkg);
    }

    public static <T> List<T> load(Class<T> cls, String pkg, Instantiator instantiator){
        Loader<T> loader = new Loader<>(cls);
        loader.instantiator(instantiator);
        return loader.loadFromPackage(pkg);
    }

    public static <T> List<T> loadAll(Class<T> cls, String pkg){
        return new Loader<>(cls).loadAllFromPackage(pkg);
    }

    public static <T> List<T> loadAll(Class<T> cls, String pkg, Instantiator instantiator){
        Loader<T> loader = new Loader<>(cls);
        loader.instantiator(instantiator);
        return loader.loadAllFromPackage(pkg);
    }

    public static Object getObjectFieldVal(Class<?> cls, String name, Object instance){
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(instance);
        } catch (Exception e){
            return null;
        }
    }

    public static Object getStaticFieldVal(Class<?> cls, String name){
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(null);
        } catch (Exception e){
            return null;
        }
    }
}
