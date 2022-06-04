package com.kisman.cc.catlua.mapping;

import com.kisman.cc.Kisman;
import fuck.you.yarnparser.entry.*;
import it.unimi.dsi.fastutil.objects.*;
import org.objectweb.asm.Type;

import java.io.BufferedReader;
import java.io.*;
import java.util.List;

public class ForgeMappings {
    public ObjectList<ClassEntry> classes;
    public ObjectList<MethodEntry> methods;
    public ObjectList<FieldEntry> fields;

    public void init(InputStream is) throws IOException {
        this.classes = new ObjectArrayList<>();
        this.methods = new ObjectArrayList<>();
        this.fields = new ObjectArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;

        while((line = reader.readLine()) != null) {
            if(line.startsWith("CL")) {
                String[] split = line.replace("CL: ", "").split(" ");
                ClassEntry classEntry = new ClassEntry();
                classEntry.official = split[0];
                classEntry.named = split[1];
                classEntry.intermediary = classEntry.named;
                classes.add(classEntry);
                System.out.println("Class: official " + classEntry.official + ", named/intermediary " + classEntry.named);
            }
            if(line.startsWith("FD")) {
                if(!line.contains("#C")) {
                    String[] split = line.replaceAll("FD: ", "").split(" ");
                    FieldEntry fieldEntry = new FieldEntry();
                    fieldEntry.official = split[0].split("/")[1];
                    fieldEntry.type = "";
                    fieldEntry.intermediary = split[1].split("/")[split[1].split("/").length - 1];
                    fieldEntry.named = Kisman.instance.remapper3000.remappingField(fieldEntry.intermediary);
                    fieldEntry.classEntry = findClass(split[0].split("/")[0], ClassFindType.OFFICIAL);
                    fields.add(fieldEntry);
                    System.out.println("Field: official " + fieldEntry.official + ", named " + fieldEntry.named + ", intermediary " + fieldEntry.intermediary);
                }
            }
            if(line.startsWith("MD")) {
                String[] split = line.replace("MD: ", "").split(" ");
                MethodEntry methodEntry = new MethodEntry();
                methodEntry.official = split[0].split("/")[1];
                methodEntry.type = split[1];
                methodEntry.intermediary = split[2].split("/")[split[2].split("/").length - 1];
                methodEntry.named = Kisman.instance.remapper3000.remappingMethod(methodEntry.intermediary);
                methodEntry.classEntry = findClass(split[0].split("/")[0], ClassFindType.OFFICIAL);
                methods.add(methodEntry);
                System.out.println("Method: official " + methodEntry.official + ", named " + methodEntry.named + ", intermediary " + methodEntry.intermediary);
            }
        }
    }

    public ClassEntry findClass( String name, ClassFindType findtype ) {
        for( ClassEntry entry : classes ) {
            switch( findtype ) {
                case OFFICIAL:
                    if( entry.official.equals( name ) ) return entry;
                    break;
                case INTERMEDIARY:
                    if( entry.intermediary.equals( name ) ) return entry;
                    break;
                case NAMED:
                    if( entry.named.equals( name ) ) return entry;
                    break;
            }
        }
        return null;
    }

    public FieldEntry findField( String classname, String fieldname, NormalFindType findtype ) {
        for( FieldEntry entry : fields ) {
            if(entry.named == null) continue;
            if( classname != null && !entry.classEntry.official.equals( classname ) ) continue;
            switch( findtype ) {
                case INTERMEDIARY:
                    if( entry.intermediary.equals( fieldname ) ) return entry;
                    break;
                case NAMED:
                    if( entry.named.equals( fieldname ) ) return entry;
                    break;
                case OFFICIAL:
                    if(entry.official.equals(fieldname)) return entry;
                    break;
            }
        }
        return null;
    }

    public MethodEntry findMethod(String classname, String methodname, NormalFindType findtype, int args, String desc ) {
        for( MethodEntry entry : methods ) {
            if(entry.named == null) continue;
            if( classname != null && !entry.official.equals( classname ) ) continue;
            if( args != -1 && Type.getArgumentTypes( entry.type ).length != args ) continue;
            if( desc != null ) if( !entry.type.contains( desc ) ) continue;
            switch( findtype ) {
                case INTERMEDIARY:
                    if( entry.intermediary.equals( methodname ) ) return entry;
                    break;
                case NAMED:
                    if( entry.named.equals( methodname ) ) return entry;
                    break;
            }
        }
        return null;
    }

    public List< ClassEntry > getClasses( ) {return classes;}
    public List< FieldEntry > getFields( ) {return fields;}
    public List< MethodEntry > getMethods( ) {return methods;}
    public enum ClassFindType {OFFICIAL, INTERMEDIARY, NAMED}
    public enum NormalFindType {INTERMEDIARY, NAMED, OFFICIAL}
}
