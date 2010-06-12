package net.haltcondition.anode;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 23/05/2010
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
public class UsageView
    extends View
{
    private final static String TAG = "UsageView";

    private float dataUsedPC = 0.0f;
    private float intoPeriodPC = 0.0f;

    private final HorseshoeDrawImpl horseshoe = new HorseshoeDrawImpl();

    public UsageView(Context context)
    {
        super(context);
    }

    public UsageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UsageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setUsage(float dataUsedPC, float intoPeriodPC)
    {
        this.dataUsedPC = dataUsedPC;
        this.intoPeriodPC = intoPeriodPC;
        invalidate();  // Force redraw
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Log.d(TAG, "Doing draw command");
        horseshoe.drawHorsehoe(canvas, dataUsedPC, intoPeriodPC);
    }

}
