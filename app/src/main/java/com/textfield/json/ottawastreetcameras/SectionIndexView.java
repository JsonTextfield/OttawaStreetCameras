package com.textfield.json.ottawastreetcameras;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by Jason on 28/10/2017.
 */

public class SectionIndexView extends android.support.v7.widget.AppCompatSeekBar {
    public SectionIndexView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int i = 0;
                i = getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(i);
                Log.i("Progress", getProgress() + "");
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
