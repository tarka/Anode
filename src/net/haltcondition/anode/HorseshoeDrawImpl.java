package net.haltcondition.anode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 12/06/2010
 */
public class HorseshoeDrawImpl 
{
    private final static String TAG = "Horseshoe";

    public final int INODE_ORANGE = 0xFFF47836;
    public final int INODE_DARKORANGE = 0xffc65908;


    protected void drawProgressArc(Canvas canvas, float size, float offset, float strokew, float percent, int colour)
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

    protected void drawHorsehoe(Canvas canvas, float dataUsedPC, float intoPeriodPC)
    {
        Log.d(TAG, "Doing draw command");

        final float size = Math.min(canvas.getWidth(), canvas.getHeight());
        final float strokeOuter = size / 10f;
        final float strokeInner = size / 10f;
        final float strokeGap = strokeOuter * 0.25f;

        drawProgressArc(canvas, size, 0, strokeOuter,
                        dataUsedPC, INODE_DARKORANGE);
        drawProgressArc(canvas, size-((strokeInner+strokeGap)*2), strokeOuter+strokeGap, strokeInner,
                        intoPeriodPC, INODE_ORANGE);

    }

}
