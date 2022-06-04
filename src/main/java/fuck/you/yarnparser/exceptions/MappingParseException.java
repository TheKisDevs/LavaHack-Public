package fuck.you.yarnparser.exceptions;

import java.io.IOException;

public class MappingParseException extends IOException
{
    public MappingParseException( String text )
    {
        super( text );
    }
}
