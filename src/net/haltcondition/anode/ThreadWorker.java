package net.haltcondition.anode;

import android.os.Handler;

public abstract class ThreadWorker 
    implements Runnable
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
        hdl.obtainMessage(UPDATEMSG, msg).sendToTarget();
    }

    
    protected void sendEndMsg()
    {
        hdl.sendEmptyMessage(ENDMSG);
    }


}
