package net.haltcondition.anode;

import android.os.Handler;
import java.util.concurrent.Callable;

public abstract class ThreadWorker<T>
    implements Callable<T>
{
    public static final int ENDMSG = 0;
    public static final int UPDATEMSG = 1;

    private final Handler hdl;

    public ThreadWorker(Handler h)
    {
        hdl = h;
    }

    protected void sendUpdate(String msg)
    {
        if (hdl != null) {
            hdl.obtainMessage(UPDATEMSG, msg).sendToTarget();
        }
    }

    
    protected void sendEndMsg()
    {
        if (hdl != null) {
            hdl.sendEmptyMessage(ENDMSG);
        }
    }

}