package net.haltcondition.anode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

    private final int INODE_ORANGE = 0xFFF47836;
    private final int INODE_DARKORANGE = 0xffc65908;

    private float dataUsedPC = 0.0f;
    private float intoPeriodPC = 0.0f;

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
        Log.d(TAG, "Got Usage: "+dataUsedPC+", "+intoPeriodPC);
        this.dataUsedPC = dataUsedPC;
        this.intoPeriodPC = intoPeriodPC;
        invalidate();  // Force redraw
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Log.d(TAG, "Doing draw command");

        final float size = Math.min(getWidth(), getHeight());
        final float strokeOuter = size / 10f;
        final float strokeInner = size / 10f;
        final float strokeGap = strokeOuter * 0.25f;

        drawProgressArc(canvas, size, 0, strokeOuter,
                        dataUsedPC, INODE_DARKORANGE);
        drawProgressArc(canvas, size-((strokeInner+strokeGap)*2), strokeOuter+strokeGap, strokeInner,
                        intoPeriodPC, INODE_ORANGE);

    }

    private void drawProgressArc(Canvas canvas, float size, float offset, float strokew, float percent, int colour)
    {
        canvas.save();

        final float GAP = 65f;

        final float wcenter = size / 2;
        final float hcenter = size / 2;
        final float hstrokew = strokew / 2;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokew);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);


        RectF rect = new RectF(hstrokew, hstrokew, size-hstrokew, size-hstrokew);

        canvas.translate(offset, offset);
        canvas.rotate(90f+(GAP/2), wcenter, hcenter);

        paint.setColor(0xffcccccc);
        canvas.drawArc(rect, 0f, 360f-GAP, false, paint);

        paint.setColor(colour);
        canvas.drawArc(rect, 0f, percent*(360f-GAP), false, paint);

        canvas.restore();
    }
}
