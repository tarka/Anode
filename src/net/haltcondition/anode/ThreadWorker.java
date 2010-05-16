package net.haltcondition.anode;

import android.os.Handler;
import android.os.Message;

public abstract class ThreadWorker<RType>
    implements Runnable
{

    public enum MsgCode {
        ENDMSG,
        UPDATEMSG,
        RESULT,
        ERRORMSG
    }

    private final Handler hdl;

    public ThreadWorker(Handler h)
    {
        hdl = h;
    }

    protected void sendUpdate(String msg)
    {
        if (hdl != null) {
            hdl.obtainMessage(MsgCode.UPDATEMSG.ordinal(), msg).sendToTarget();
        }
    }

    protected void sendResult(RType result)
    {
        if (hdl != null) {
            hdl.obtainMessage(MsgCode.RESULT.ordinal(), result).sendToTarget();
        }
    }

    protected void sendError(String msg)
    {
        if (hdl != null) {
            hdl.obtainMessage(MsgCode.ERRORMSG.ordinal(), msg).sendToTarget();
        }
    }
    
    protected void sendEndMsg()
    {
        if (hdl != null) {
            hdl.sendEmptyMessage(MsgCode.ENDMSG.ordinal());
        }
    }

}
