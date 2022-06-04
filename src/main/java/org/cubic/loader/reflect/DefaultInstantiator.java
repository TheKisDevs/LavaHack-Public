package org.cubic.loader.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultInstantiator implements Instantiator {

    @Override
    public Object instantiate(Class<?> cls) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> c = cls.getDeclaredConstructor();
        if(!c.isAccessible())
            c.setAccessible(true);
        return c.newInstance();
    }
}
