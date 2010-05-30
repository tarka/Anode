package net.haltcondition.anode;

import android.os.Handler;
import android.os.Message;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 2010
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation..
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
