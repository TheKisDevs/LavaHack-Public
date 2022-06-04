package org.cubic.loader.reflect;

public interface Instantiator {

    Object instantiate(Class<?> cls) throws Exception;


    static Instantiator getDefault(){
        return new DefaultInstantiator();
    }

}
