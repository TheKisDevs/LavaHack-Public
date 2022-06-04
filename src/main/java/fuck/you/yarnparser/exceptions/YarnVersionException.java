package fuck.you.yarnparser.exceptions;

import java.io.IOException;

public class YarnVersionException extends IOException
{
    public YarnVersionException( String text )
    {
        super( text );
    }
}