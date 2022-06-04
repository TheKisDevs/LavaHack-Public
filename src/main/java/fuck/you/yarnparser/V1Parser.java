package fuck.you.yarnparser;

import fuck.you.yarnparser.entry.ClassEntry;
import fuck.you.yarnparser.entry.FieldEntry;
import fuck.you.yarnparser.entry.MethodEntry;
import fuck.you.yarnparser.exceptions.MappingParseException;
import fuck.you.yarnparser.exceptions.UnknownMappingTypeException;
import fuck.you.yarnparser.exceptions.YarnVersionException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.objectweb.asm.Type;

import java.io.*;
import java.util.List;

public class V1Parser
{
    private ObjectList< ClassEntry > classes;
    private ObjectList< MethodEntry > methods;
    private ObjectList< FieldEntry > fields;

    public boolean parsed;

    public V1Parser( File file ) throws IOException
    {
        reset( );

        if( !file.exists( ) )
            throw new FileNotFoundException( file.getAbsolutePath( ) );

        parse( new FileInputStream( file ) );
    }

    public V1Parser( InputStream is ) throws IOException
    {
        reset( );

        parse( is );
    }

    public void reset( )
    {
        this.classes = new ObjectArrayList<>( );
        this.methods = new ObjectArrayList< >( );
        this.fields = new ObjectArrayList< >( );
        this.parsed = false;
    }

    public void parse( InputStream is ) throws IOException
    {
        // dont parse twice
        if( parsed ) return;


        BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );


        String line = reader.readLine( );

        // check header
        if( !line.startsWith( "v1" ) )
            throw new YarnVersionException( "Only v1 mappings are supported [" + line + "]" );

        while( ( line = reader.readLine( ) ) != null )
        {
            String[ ] split = line.split( "\t" );
            if( split.length >= 2 )
            {
                switch (split[ 0 ]) {
                    case "CLASS": {
                        if (split.length < 4)
                            throw new MappingParseException("[CLASS] whitespace count mismatch, got " + split.length + " while expected at least 4 [" + line + "]");
                        String cofficial = split[ 1 ];
                        String cintermediary = split[ 2 ];
                        String cnamed = split[ 3 ];
                        ClassEntry centry = new ClassEntry();
                        centry.official = cofficial;
                        centry.intermediary = cintermediary;
                        centry.named = cnamed;
                        classes.add(centry);
                    }
                    case "FIELD": {
                        if (split.length < 6)
                            throw new MappingParseException("[FIELD] whitespace count mismatch, got " + split.length + " while expected at least 6 [" + line + "]");
                        String fofficial = split[ 1 ];
                        String ftype = split[ 2 ];
                        String fintermediary = split[ 4 ];
                        String fnamed = split[ 5 ];
                        ClassEntry fcentry = findClass(fofficial, ClassFindType.OFFICIAL);
                        if (fcentry != null)
                            fofficial = fcentry.intermediary;
                        FieldEntry fentry = new FieldEntry();
                        fentry.official = fofficial;
                        fentry.type = ftype;
                        fentry.intermediary = fintermediary;
                        fentry.named = fnamed;
                        fields.add(fentry);
                    }
                    case "METHOD": {
                        if (split.length < 6)
                            throw new MappingParseException("[METHOD] whitespace count mismatch, got " + split.length + " while expected at least 6 [" + line + "]");
                        String mofficial = split[ 1 ];
                        String mtype = split[ 2 ];
                        String mintermediary = split[ 4 ];
                        String mnamed = split[ 5 ];
                        ClassEntry mcentry = findClass(mofficial, ClassFindType.OFFICIAL);
                        if (mcentry != null)
                            mofficial = mcentry.intermediary;
                        MethodEntry mentry = new MethodEntry();
                        mentry.official = mofficial;
                        mentry.type = mtype;
                        mentry.intermediary = mintermediary;
                        mentry.named = mnamed;
                        methods.add(mentry);
                    }
                    default: throw new UnknownMappingTypeException("Unknown mapping type [" + line + "]");
                }
            }
        }

        reader.close( );
        parsed = true;
    }

    public ClassEntry findClass( String name, ClassFindType findtype )
    {
        for( ClassEntry entry : classes )
        {
            switch( findtype )
            {
                case OFFICIAL:
                    if( entry.official.equals( name ) )
                        return entry;
                    break;
                case INTERMEDIARY:
                    if( entry.intermediary.equals( name ) )
                        return entry;
                    break;
                case NAMED:
                    if( entry.named.equals( name ) )
                        return entry;
                    break;
            }
        }

        return null;
    }

    public FieldEntry findField( String classname, String fieldname, NormalFindType findtype )
    {
        for( FieldEntry entry : fields )
        {
            if( classname != null && !entry.official.equals( classname ) )
                continue;

            switch( findtype )
            {
                case INTERMEDIARY:
                    if( entry.intermediary.equals( fieldname ) )
                        return entry;
                    break;
                case NAMED:
                    if( entry.named.equals( fieldname ) )
                        return entry;
                    break;
            }
        }

        return null;
    }

    public MethodEntry findMethod( String classname, String methodname, NormalFindType findtype, int args, String desc )
    {
        for( MethodEntry entry : methods )
        {
            if( classname != null && !entry.official.equals( classname ) )
                continue;

            if( args != -1 && Type.getArgumentTypes( entry.type ).length != args ) continue;

            if( desc != null )
            {
                if( !entry.type.contains( desc ) )
                    continue;
            }

            switch( findtype )
            {
                case INTERMEDIARY:
                    if( entry.intermediary.equals( methodname ) )
                        return entry;
                    break;
                case NAMED:
                    if( entry.named.equals( methodname ) )
                        return entry;
                    break;
            }
        }

        return null;
    }

    public List< ClassEntry > getClasses( )
    {
        return classes;
    }

    public List< FieldEntry > getFields( )
    {
        return fields;
    }

    public List< MethodEntry > getMethods( )
    {
        return methods;
    }

    public enum ClassFindType
    {
        OFFICIAL,
        INTERMEDIARY,
        NAMED
    }

    public enum NormalFindType
    {
        INTERMEDIARY,
        NAMED
    }
}
