package cn.gotohope.forgive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private boolean isRunning;
    private boolean isPause;
    private SurfaceHolder holder;
    private Paint paint = new Paint();
    private int height = 0, width;
    private int data[] = {2,3,0,1,2,3,0,1,0,3,2,3,0,1,2,3,0,1,0,3,2,3,0,1,2,3,0,1,0,3,2,3,0,1,2,3,0,1,0,3,2,3,0,1,2,3,0,1,0,3,2};
    private double offset = 0;

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        paint.setColor(Color.BLACK);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
                    return false;
                int x = (int) (motionEvent.getX() / width * 4);
                int y = (int) ((height - motionEvent.getY()) / height * 4 + offset);
                if (data[y] == x) {
                    data[y] = 4;
                    if (isPause)
                        isPause = false;
                }
                return true;
            }
        });
        isPause = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (isPause == false)
                            offset += 0.01;
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void run() {
        while (isRunning)
            draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    public void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null)
            return;
        if (height == 0) {
            height = canvas.getHeight();
            width = canvas.getWidth();
        }
        canvas.drawColor(Color.WHITE);
        int offsetY = (int) offset;
        for (int j = offsetY;j < offsetY + 5 && j < data.length; ++j) {
            for (int i = 0;i < 4; ++i) {
                if (i == data[j]) {
                    RectF rect = new RectF(i*width/4, (4-j)*height/4, (i+1)*width/4, (3-j)*height/4);
                    rect.offset(0, (float) offset * height / 4);
                    canvas.drawRect(rect, paint);
                }
            }
        }
        holder.unlockCanvasAndPost(canvas);
    }

}
