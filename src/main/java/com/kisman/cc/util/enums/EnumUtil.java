package com.kisman.cc.util.enums;

public class EnumUtil<T extends Enum<T>> {
    private T t;

    public Enum<T> getEnumByName(String name) {
        for(Enum<T> e : t.getClass().getEnumConstants()) if(e.name().equals(name)) return e;
        return null;
    }
}
