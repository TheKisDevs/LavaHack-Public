package fuck.you.yarnparser.exceptions;

import java.io.IOException;

public class UnknownMappingTypeException extends IOException
{
    public UnknownMappingTypeException( String text )
    {
        super( text );
    }
}
