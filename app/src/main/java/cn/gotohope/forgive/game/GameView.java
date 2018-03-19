package cn.gotohope.forgive.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xinsane.util.LogUtil;

import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.user.UserManager;

@SuppressLint("DefaultLocale")
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private boolean isRunning;
    private boolean isStart;
    private boolean isPause;
    private boolean isOver;
    private boolean isSave;
    private double scroll;
    private SurfaceHolder holder;
    private Paint paint = new Paint();
    private float height = 0, width;
    private double offset = 0;

    private GameActivity parent;
    private Game game;
    private GameViewData data;
    private int max_score;

    private double a, v;

    private double fps = 0.0;
    private double p_x = 0.0, p_y = 0.0;

    private void init() {
        parent = (GameActivity) getContext();
        game = parent.getGame();
        data = new GameViewData(game);
        if (game.auto_scroll) {
            a = game.a;
            v = game.v;
        } else {
            a = 0.0;
            v = 0.0;
        }

        max_score = getContext().getSharedPreferences("max_score", Context.MODE_PRIVATE).getInt(game.id, 0);

        holder = getHolder();
        holder.addCallback(this);
        paint.setColor(Color.BLACK);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS)
                    performClick();
                if (isOver)
                    return true;

                int type = motionEvent.getAction();
                int x = (int) (motionEvent.getX() / width * game.column_number) + 1;
                double Y = (height - motionEvent.getY()) / height * game.row_number + offset;
                int y = (int) Y;

                if (type != MotionEvent.ACTION_DOWN &&
                        type != MotionEvent.ACTION_POINTER_DOWN)
                    return true;

                p_x = motionEvent.getX();
                p_y = motionEvent.getY();

                if (data.get(y) == x) {
                    if (data.set(y)) {
                        if (isPause)
                            isPause = false;
                        onStep();
                        LogUtil.d(String.format("(%d, %d), ok", x, y));
                    } else {
                        LogUtil.d(String.format("(%d, %d), too fast", x, y));
                    }
                } else if (!isPause) {
                    double helper_up = 0.8;
                    double helper_down = 0.3;
                    int y2 = (int) (Y - helper_up);
                    for (int o = y2; o < y; ++ o) {
                        if (data.get(o) == x) {
                            if (data.set(o)) {
                                onStep();
                                LogUtil.d(String.format("(%d, %d), ok fix up", x, o));
                            }
                            else
                                LogUtil.d(String.format("(%d, %d), fail fix up", x, o));
                            return true;
                        }
                    }
                    y2 = (int) (Y + helper_down);
                    for (int o = y2; o < y; ++ o) {
                        if (data.get(o) == x) {
                            if (data.set(o)) {
                                onStep();
                                LogUtil.d(String.format("(%d, %d), ok fix down", x, o));
                            }
                            else
                                LogUtil.d(String.format("(%d, %d), fail fix down", x, o));
                            return true;
                        }
                    }
                    LogUtil.d(String.format("(%d, %d), wrong", x, y));
                    isOver = true;
                    data.wrong(x, y);
                }
                return true;
            }
        });
        isStart = false;
        isPause = true;
        isOver = false;
        isSave = false;
        scroll = 0.0;
    }

    private void onStep() {
        if (!game.auto_scroll)
            scroll += 1.0;
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
        long last = System.currentTimeMillis();
        while (isRunning) {
            if (!isOver && !isPause) {
                isStart = true;
                if (game.auto_scroll) {
                    offset += v;
                    v += a;
                    if (v >= 0.095 && v < 0.095 + a) {
                        v = 0.095 + a;
                        a /= 2;
                    }
                    if (v >= 0.11 && v < 0.11 + a) {
                        v = 0.11 + a;
                        a /= 2;
                    }
                    if (v > 0.115)
                        a /= 1.005;
                } else if (scroll > 0) {
                    v = game.scroll_v;
                    if (scroll > v) {
                        offset += v;
                        scroll -= v;
                    } else {
                        offset += scroll;
                        v = 0.0;
                        scroll = 0.0;
                    }
                }
                int offsetY = (int) offset;
                if (offsetY > 0 && data.get(offsetY-1) != 0) {
                    data.wrong(offsetY-1);
                    isOver = true;
                    offset = offsetY - 1;
                }
            }
            long now = System.currentTimeMillis();
            fps = 1000.0 / (now - last);
            last = now;
            draw();
            if (isOver && !isSave) {
                isSave = true;
                save();
            }
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

    private void drawTile(Canvas canvas, int i, int j, int cx, int cy, Paint paint) {
        RectF rect = new RectF(i*width/cx, (cy-j)*height/cy, (i+1)*width/cx, (cy-1-j)*height/cy);
        rect.offset(0, (float) offset * height / cy);
        canvas.drawRect(rect, paint);
    }

    public void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null)
            return;
        if (height == 0) {
            height = canvas.getHeight();
            width = canvas.getWidth();
            offset = -400.0 / height * game.row_number;
        }
        canvas.drawColor(0xffe4f2e6);

        int offsetY = (int) offset;
        int cx = game.column_number;
        int cy = game.row_number;

        // Tiles
        paint.setColor(Color.BLACK);
        for (int j = offsetY;j < offsetY + cy + 1; ++j) {
            if (j < 0)
                continue;
            int index = data.get(j);
            for (int i = 0;i < cx; ++i) {
                if (i + 1 == index)
                    drawTile(canvas, i, j, cx, cy, paint);
            }
        }

        // Game Info
        if (!isStart) {
            RectF info = new RectF(0, height - 400, width, height);
            paint.setTextSize(65);
            paint.setColor(0xffd5ebdb);
            canvas.drawRect(info, paint);
            paint.setColor(0xff3f48cc);
            paint.setAntiAlias(true);
            canvas.drawText(game.name + "    best: " + max_score, info.left + 100, info.top + 150, paint);
            paint.setColor(0xff1467ff);
            paint.setTextSize(50);
            canvas.drawText(game.description, info.left + 100, info.top + 250, paint);
        }

        // Miss Tile
        int wrong_step = data.get_wrong_step();
        if (wrong_step > 0) {
            paint.setColor(0xffffa500);
            drawTile(canvas, data.get(wrong_step)-1, wrong_step, cx, cy, paint);
        }

        // Wrong Tile
        int wrong_x = data.get_wrong_x();
        int wrong_y = data.get_wrong_y();
        if (wrong_y > 0) {
            paint.setColor(Color.RED);
            drawTile(canvas, wrong_x - 1, wrong_y, cx, cy, paint);
        }

        // Score
        paint.setColor(Color.RED);
        paint.setTextSize(108);
        canvas.drawText(String.valueOf(data.score()), 50, 150, paint);

        // Debug v/a & fps
//        paint.setColor(Color.RED);
//        paint.setTextSize(36);
//        String text = String.format("v: %.4f     50a: %.6f     fps: %d", v, a*50, (int) fps);
//        canvas.drawText(text, 65, 50, paint);

        // Debug last wrong point
        if (wrong_y > 0) {
            paint.setColor(Color.BLUE);
            canvas.drawCircle((float) p_x, (float) p_y, 15, paint);
        }

        holder.unlockCanvasAndPost(canvas);
    }

    private void save() {
        Message message = new Message();
        message.what = MessageHandler.EVENT_GAME_OVER;
        message.arg1 = data.score();
        message.arg2 = max_score;
        handler.sendMessage(message);
        if (data.score() > max_score) {
            max_score = data.score();
            SharedPreferences.Editor editor = getContext().getSharedPreferences("max_score", Context.MODE_PRIVATE).edit();
            editor.putInt(game.id, max_score);
            editor.apply();
            Intent intent = new Intent();
            intent.putExtra("best", max_score);
            parent.setResult(Activity.RESULT_OK, intent);
            UserManager.uploadScore(game, max_score, null);
        }
    }

    public boolean onBackPressed() {
        if (isOver || !isStart)
            return true;
        isPause = true;
        new AlertDialog.Builder(parent).setTitle("确认退出吗").setMessage("现在退出将不会保留游戏数据")
                .setCancelable(false)
                .setPositiveButton("回到游戏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        isPause = false;
                    }
                })
                .setNegativeButton("确认退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        parent.finish();
                    }
                }).show();
        return false;
    }

    private static class MessageHandler extends Handler {
        static final int EVENT_GAME_OVER = 0x01;

        private GameView gameView;
        MessageHandler(GameView gameView) {
            this.gameView = gameView;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_GAME_OVER:
                    int my_score = msg.arg1;
                    int old_max = msg.arg2;
                    new AlertDialog.Builder(gameView.parent).setTitle("游戏已结束")
                        .setMessage("你的分数为：" + my_score + " !\n" +
                                (my_score > old_max ? "恭喜你创造了新的记录！" : "最高分为：" + old_max + "，继续加油吧"))
                        .setCancelable(true)
                        .setPositiveButton("返回主菜单", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gameView.parent.finish();
                            }
                        }).show();
                    break;
            }
        }
    }

    private MessageHandler handler = new MessageHandler(this);

}
