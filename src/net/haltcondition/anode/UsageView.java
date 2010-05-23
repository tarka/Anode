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
 */
public class UsageView
    extends View
{
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

    @Override
    protected void onDraw(Canvas canvas)
    {
        final float size = Math.min(canvas.getWidth(), canvas.getHeight());
        final float strokeOuter = size / 10f;
        final float strokeInner = size / 10f;
        final float strokeGap = strokeOuter * 0.25f;

        drawProgressArc(canvas, size,             0,       strokeOuter, 0.75f, 0xffc65908);
        drawProgressArc(canvas, size-((strokeInner+strokeGap)*2), strokeOuter+strokeGap, strokeInner, 0.75f, 0xffc65908);

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
