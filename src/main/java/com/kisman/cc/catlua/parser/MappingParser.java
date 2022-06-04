package com.kisman.cc.catlua.parser;

import fuck.you.yarnparser.entry.ClassEntry;
import fuck.you.yarnparser.entry.FieldEntry;
import fuck.you.yarnparser.entry.MethodEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class MappingParser {
    private ObjectList<ClassEntry> classes;
    private ObjectList<MethodEntry> methods;
    private ObjectList<FieldEntry> fields;

    public boolean parsed;

    public void reset() {
        this.classes = new ObjectArrayList<>( );
        this.methods = new ObjectArrayList< >( );
        this.fields = new ObjectArrayList< >( );
        this.parsed = false;
    }

    public void parse(String remapped, String mapped, ForParsing state) {
        switch(state) {
            case METHOD: {
                MethodEntry methodEntry = new MethodEntry();
                methodEntry.official = methodEntry.type = "";
                methodEntry.named = remapped;
                methodEntry.intermediary = mapped;
                methods.add(methodEntry);
                break;
            }
            case FIELD: {
                FieldEntry fieldEntry = new FieldEntry();
                fieldEntry.official = fieldEntry.type = "";
                fieldEntry.named = remapped;
                fieldEntry.intermediary = mapped;
                fields.add(fieldEntry);
                break;
            }
        }
    }

    public MethodEntry findMethod(String name, boolean named) {
        for(MethodEntry method : methods) {
            if(method.named.equals(name) && named) return method;
            if(method.intermediary.equals(name) && !named) return method;
        }
        return null;
    }

    public FieldEntry findField(String name, boolean named) {
        for(FieldEntry field : fields) {
            if(field.named.equals(name) && named) return field;
            if(field.intermediary.equals(name) && !named) return field;
        }
        return null;
    }

    public enum ForParsing {
        METHOD, FIELD
    }
}
