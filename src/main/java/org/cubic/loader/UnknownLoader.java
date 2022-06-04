package org.cubic.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class UnknownLoader<E> extends AbstractLoader<E> {

    UnknownLoader(ClassLoader classLoader) {
        super(classLoader, new HashSet<>());
    }

    @Override
    List<E> loadFromPackage(String pkg) {
        return new ArrayList<>();
    }

    @Override
    List<E> loadAllFromPackage(String pkg) {
        return new ArrayList<>();
    }

    @Override
    URLPath getURLPathOfClass(Class<?> cls) {
        return null;
    }

    @Override
    URLPath getURLPathOfClass(String cls) {
        return null;
    }

    @Override
    URLPath getURLPathOfPackage(Package pkg) {
        return null;
    }

    @Override
    URLPath getURLPathOfPackage(String pkg) {
        return null;
    }

    @Override
    URLPath getURLPath(String resource) {
        return null;
    }
}
