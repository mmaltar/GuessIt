package com.example.x.aplikacija;

/**
 * Created by x on 9.1.2018..
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class Square extends View { //sredit još klasu pošteno

    int height, width;
    char letter = ' ';
    Paint p = new Paint();
    Bitmap bomb = BitmapFactory.decodeResource(this.getResources(),
            R.drawable.bomb24);
    Bitmap revert = BitmapFactory.decodeResource(this.getResources(),
            R.drawable.revert22);
    Bitmap hint = BitmapFactory.decodeResource(this.getResources(),
            R.drawable.hint32);
    Bitmap negClock = BitmapFactory.decodeResource(this.getResources(),
            R.drawable.negclock32);


    boolean touchOn;
    boolean mDownTouch = false;
    boolean revealed = false;
    boolean first_time = true;
    private VariableChangeListener variableChangeListener;
    private OnToggledListener toggledListener;
    int idX = 0; //default
    int idY = 0; //default


    public interface OnToggledListener {
        void OnToggled(Square v, boolean touchOn);
    }

    public Square(Context context, int x, int y) {
        super(context);
        idX = x;
        idY = y;
        init();
    }

    public Square(Context context) {
        super(context);
        init();
    }

    public Square(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Square(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        touchOn = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("onDraw: ", "poziv onDraw, x: " + Integer.toString(idX) + " y: " + Integer.toString(idY) );

        if (touchOn || (revealed  && first_time)) {
            height = canvas.getHeight();
            width = canvas.getWidth();
            // Log.d("height: ", Integer.toString(height));
            //Log.d("width: ", Integer.toString(width));

            p.setColor(Color.RED);
            p.setTextSize((float) 0.8 * height);

            if (letter >= 'A' && letter <= 'Z')
                canvas.drawText(Character.toString(letter), (float) 0.2 * width, (float) 0.8 * height, p);
            else if (letter == 'b')
            {
                canvas.drawBitmap(bomb,(float) 0.15 * width, (float) 0.15 * height, null);
            }
            else if (letter == 'r')
            {
                canvas.drawBitmap(revert,(float) 0.15 * width, (float) 0.15 * height, null);
            }
            else if (letter == 'h')
            {
                canvas.drawBitmap(hint,(float) 0.05 * width, 0, null);
            }
            else if (letter == 't')
            {
                canvas.drawBitmap(negClock,(float) 0.05 * width, 0, null);
            }

            /*revealed = true;
            this.variableChangeListener.onVariableChanged(revealed, idX, idY);
            Log.d("onDraw: ", "revealed = true");
            first_time = false;*/
        } else {
            canvas.drawColor(Color.GRAY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (revealed ) return false;

        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                touchOn = !touchOn;
                invalidate();

                if(toggledListener != null){
                    toggledListener.OnToggled(this, touchOn);
                }

                mDownTouch = true;
                return true;

            case MotionEvent.ACTION_UP:
                if (mDownTouch) {
                    mDownTouch = false;
                    performClick();
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean performClick() {
        super.performClick();

        revealed = true;
        this.variableChangeListener.onVariableChanged(revealed, idX, idY);
        Log.d("onDraw: ", "revealed = true");
        first_time = false;
        return true;
    }

    public void setOnToggledListener(OnToggledListener listener){
        toggledListener = listener;
    }

    public int getIdX(){
        return idX;
    }

    public int getIdY(){
        return idY;
    }

    public void setVariableChangeListener(VariableChangeListener variableChangeListener) {
        this.variableChangeListener = variableChangeListener;
    }

}

/*
package com.example.x.aplikacija;


 * Created by x on 9.1.2018..


import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.view.View;


public class Square extends View { //sredit još klasu pošteno

    public interface OnToggledListener {
        void OnToggled(Square v, boolean touchOn);
    }

    boolean touchOn;
    boolean mDownTouch = false;
    private OnToggledListener toggledListener;
    int idX = 0; //default
    int idY = 0; //default

    public Square(Context context, int x, int y) {
        super(context);
        idX = x;
        idY = y;
        init();
    }

    public Square(Context context) {
        super(context);
        init();
    }

    public Square(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Square(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        touchOn = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (touchOn) {
            canvas.drawColor(Color.RED);
        } else {
            canvas.drawColor(Color.GRAY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                touchOn = !touchOn;
                invalidate();

                if(toggledListener != null){
                    toggledListener.OnToggled(this, touchOn);
                }

                mDownTouch = true;
                return true;

            case MotionEvent.ACTION_UP:
                if (mDownTouch) {
                    mDownTouch = false;
                    performClick();
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void setOnToggledListener(OnToggledListener listener){
        toggledListener = listener;
    }

    public int getIdX(){
        return idX;
    }

    public int getIdY(){
        return idY;
    }

}
 */