package net.haltcondition.anode;

import java.io.InputStream;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 09/05/2010
 */
public interface HttpResultHandler<T>
{

    T parse(InputStream in);
    
}