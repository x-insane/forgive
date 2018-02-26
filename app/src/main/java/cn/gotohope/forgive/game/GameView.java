package cn.gotohope.forgive.game;

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

import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cn.gotohope.forgive.data.Game;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private boolean isRunning;
    private boolean isPause;
    private boolean isOver;
    private SurfaceHolder holder;
    private Paint paint = new Paint();
    private int height = 0, width;
    private double offset = 0;

    private Game game;
    private Data data;

    class Data {
        private final int loop;
        private final int[] data;
        private int step = 0;
        private Random rand = new Random(System.currentTimeMillis());

        public Data(Game game) {
            loop = game.row_number() * 2 + 1;
            data = new int[loop];
            data[0] = game.column_number();
        }

        public int get(int i) {
            i %= loop;
            if (i == (step + 1) % loop) {
                data[i] = rand.nextInt(game.column_number());
                int last = i > 0 ? i - 1 : loop - 1;
                while (data[i] == data[last])
                    data[i] = rand.nextInt(game.column_number());
                step ++;
            }
            return data[i];
        }

        public void set(int i) {
            i %= loop;
            int last = i > 0 ? i - 1 : loop - 1;
            if (data[last] == game.column_number())
                data[i] = game.column_number();
        }
    }

    private void init() {
        game = ((GameActivity) getContext()).game;
        data = new Data(game);

        holder = getHolder();
        holder.addCallback(this);
        paint.setColor(Color.BLACK);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isOver)
                    return false;
                if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
                    return false;
                int x = (int) (motionEvent.getX() / width * game.column_number());
                int y = (int) ((height - motionEvent.getY()) / height * game.row_number() + offset);
                if (data.get(y) == x) {
                    data.set(y);
                    if (isPause)
                        isPause = false;
                }
                return true;
            }
        });
        isPause = true;
        isOver = false;
//        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
//        pool.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (isPause == false) {
//                    offset += 0.05;
//                    int offsetY = (int) offset;
//                    if (offsetY > 0 && data.get(offsetY-1) != game.column_number()) {
//                        isOver = true;
//                        offset -= 1;
//                    }
//                }
//                if (isOver)
//                    this.cancel();
//            }
//        }, 0, 50, TimeUnit.MILLISECONDS);
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
        double a = 0.00002;
        double v = 0.04;
        while (isRunning) {
            if (isOver == false && isPause == false) {
                offset += v;
                v += a;
                int offsetY = (int) offset;
                if (offsetY > 0 && data.get(offsetY-1) != game.column_number()) {
                    isOver = true;
                    offset -= 1;
                }
            }
            draw();
        }
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
        int cx = game.column_number();
        int cy = game.row_number();
        for (int j = offsetY;j < offsetY + cy + 1; ++j) {
            for (int i = 0;i < cx; ++i) {
                if (i == data.get(j)) {
                    RectF rect = new RectF(i*width/cx, (cy-j)*height/cy, (i+1)*width/cx, (cy-1-j)*height/cy);
                    rect.offset(0, (float) offset * height / cy);
                    canvas.drawRect(rect, paint);
                }
            }
        }
        holder.unlockCanvasAndPost(canvas);
    }

}
