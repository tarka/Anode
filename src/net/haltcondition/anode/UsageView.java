package net.haltcondition.anode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
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
        final float woff = (canvas.getWidth() - size) / 2f;
        final float hoff = ((canvas.getHeight() - size) / 2f);
        final float wcenter = size / 2;
        final float hcenter = size / 2;
        final float strokew = size / 10f;
        final float hstrokew = strokew / 2;

        float usedpc = 0.75f;
        float gapa = 65f;


        RectF rect = new RectF(hstrokew, hstrokew, size-hstrokew, size-hstrokew);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokew);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        canvas.rotate(90f+(gapa/2), wcenter, hcenter);

        paint.setColor(0xffcccccc);
        canvas.drawArc(rect, 0f, 360f-gapa, false, paint);

        paint.setColor(0xffc65908);
        canvas.drawArc(rect, 0f, usedpc*(360f-gapa), false, paint);
    }
}
