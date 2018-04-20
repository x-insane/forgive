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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.xinsane.util.LogUtil;

import cn.gotohope.forgive.data.Game;
import cn.gotohope.forgive.user.UserManager;

@SuppressLint("DefaultLocale")
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable, GameViewData.GameListener {

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
    private int max_score; // 从max_score文件中读出的最高分，默认0
    private float max_velocity; // 从max_velocity文件中读出的最快速度，默认0

    private float a, v;

    private double fps = 0.0;
    private double p_x = 0.0, p_y = 0.0;

    private int score = 0;
    private int forgive = 0;
    private int game_time = 0;

    private GameForgive last_forgive = null;
    private int forgive_time = 0;
    private int last_x = 0, last_y = 0;

    private void init() {
        parent = (GameActivity) getContext();
        game = parent.getGame();
        data = new GameViewData(game, this);
        if (game.auto_scroll) {
            a = game.a;
            v = game.v;
        } else {
            a = 0.0f;
            v = 0.0f;
        }

        max_score = getContext().getSharedPreferences("max_score", Context.MODE_PRIVATE).getInt(game.id, 0);
        max_velocity = getContext().getSharedPreferences("max_velocity", Context.MODE_PRIVATE).getFloat(game.id, 0);

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

                if (Math.abs(data.get(y)) == x) {
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
                        if (o < 0)
                            continue;
                        if (Math.abs(data.get(o)) == x) {
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
                        if (Math.abs(data.get(o)) == x) {
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
        long last = System.currentTimeMillis() - 20;
        while (isRunning) {
            long now = System.currentTimeMillis();
            long period = now - last;
            fps = 1000.0 / (now - last);
            if (!isOver && !isPause) {
                if (fps < 45)
                    LogUtil.d("fps: " + (int) fps, "LOW_FPS_WARNING");
                isStart = true;
                if (game.auto_scroll) {
                    offset += v;
                    v += a;
                    if (v >= 0.095 && v < 0.095 + a) {
                        v = 0.095f + a;
                        a /= 2;
                    }
                    if (v >= 0.11 && v < 0.11 + a) {
                        v = 0.11f + a;
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
                        v = 0.0f;
                        scroll = 0.0;
                    }
                }
                int offsetY = (int) offset;
                if (offsetY > 0 && data.get(offsetY-1) > 0) {
                    data.wrong(offsetY-1);
                    isOver = true;
                    offset = offsetY - 1;
                }
                if (forgive_time > 0) {
                    if (forgive_time > period)
                        forgive_time -= period;
                    else {
                        forgive_time = 0;
                        last_forgive = null;
                    }
                }
                game_time += period;
                if (game.time_limit > 0) {
                    if (game_time >= game.time_limit * 1000)
                        isOver = true;
                }
            }
            draw();
            if (isOver && !isSave) {
                isSave = true;
                save();
            }
            last = now;
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
        for (int j = offsetY;j < offsetY + cy + 1; ++j) {
            if (j < 0)
                continue;
            int index = data.get(j);
            boolean is_forgive = false;
            if (index < 0) {
                index = -index;
                is_forgive = true;
            }
            if (is_forgive)
                paint.setColor(Color.GREEN);
            else
                paint.setColor(Color.BLACK);
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
        int wrong_step = data.get_missing_till();
        if (wrong_step > 0) {
            paint.setColor(0xffffa500);
            drawTile(canvas, data.get(wrong_step)-1, wrong_step, cx, cy, paint);
        }

        // Wrong Tile
        int wrong_x = data.get_wrong_x();
        int wrong_y = data.get_wrong_y();
        if (last_forgive == GameForgive.GAME_OVER) {
            wrong_x = last_x;
            wrong_y = last_y;
        }
        if (wrong_y >= 0) {
            paint.setColor(Color.RED);
            drawTile(canvas, wrong_x - 1, wrong_y, cx, cy, paint);
        }

        if (game.type.equals("challenge")) {
            // Velocity
            paint.setColor(Color.RED);
            paint.setTextSize(92);
            canvas.drawText(String.format("%.3f tills/s", v), 80, 150, paint);
        } else {
            // Score
            paint.setColor(Color.RED);
            paint.setTextSize(108);
            canvas.drawText(String.valueOf(score), 80, 150, paint);
        }


        // Forgive
        if (forgive > 0) {
            paint.setColor(0xff11aa11);
            paint.setTextSize(108);
            float w = width - 130;
            if (forgive > 9)
                w -= 60;
            if (forgive > 99)
                w -= 60;
            canvas.drawText(String.valueOf(forgive), w, 150, paint);
        }

        // Forgive Text
        if (forgive_time > 0) {
            paint.setColor(Color.RED);
            paint.setTextSize(60 + forgive_time / 10);
            String forgive_text = last_forgive.toString();
            float w = paint.measureText(forgive_text);
            canvas.drawText(forgive_text, (width - w) / 2, 250 + forgive_time / 10, paint);
        }

        // Time Limit Text
        if (game.time_limit > 0) {
            double time_limit = game.time_limit - game_time / 1000.0;
            if (time_limit < 0)
                time_limit = 0;
            if (time_limit < 5)
                paint.setColor(Color.RED);
            else
                paint.setColor(0xff11aa11);
            paint.setTextSize(95);
            canvas.drawText(String.format("%.1f", time_limit), width - 250, 150, paint);
        }

        // Debug v/a & fps
//        paint.setColor(Color.RED);
//        paint.setTextSize(36);
//        String text = String.format("v: %.4f     50a: %.6f     fps: %d", v, a*50, (int) fps);
//        canvas.drawText(text, 65, 50, paint);
        fps = 0 + fps;

        // Debug last wrong point
        if (data.get_wrong_y() > 0) {
            paint.setColor(Color.BLUE);
            canvas.drawCircle((float) p_x, (float) p_y, 15, paint);
        }

        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void onScore() {
        score ++;
    }

    @Override
    public void onForgive() {
        forgive ++;
        last_forgive = GameForgive.choose();
        forgive_time = 500;
        switch (last_forgive) {
            case NOTHING:
                break;
            case SPEED_DOWN:
                v *= 0.9;
                break;
            case SCORE_ADD_5:
                score += 5;
                break;
            case SCORE_ADD_10:
                score += 10;
                break;
            case SCORE_ADD_20:
                score += 20;
                break;
            case SCORE_ADD_100:
                score += 100;
                break;
            case SPEED_UP:
                v *= 1.1;
                break;
            case GAME_OVER:
                isOver = true;
                break;
        }
    }

    @Override
    public void onStep(int x, int y) {
        last_x = x;
        last_y = y;
    }

    private void save() {
        if (game.type.equals("challenge")) {
            Message message = new Message();
            message.what = MessageHandler.EVENT_GAME_OVER_FROM_CHALLENGE;
            message.arg1 = (int) (v * 10000);
            message.arg2 = (int) (max_velocity * 10000);
            handler.sendMessage(message);
            if (v > max_velocity) {
                max_velocity = v;
                SharedPreferences.Editor editor = getContext().getSharedPreferences("max_velocity", Context.MODE_PRIVATE).edit();
                editor.putFloat(game.id, max_velocity);
                editor.apply();
                Intent intent = new Intent();
                intent.putExtra("best", max_velocity);
                parent.setResult(Activity.RESULT_OK, intent);
                UserManager.uploadChallengeScore(game, max_velocity, null);
            }
        } else {
            Message message = new Message();
            message.what = MessageHandler.EVENT_GAME_OVER_FROM_GAME_LIST;
            message.arg1 = score;
            message.arg2 = max_score;
            handler.sendMessage(message);
            if (score > max_score) {
                max_score = score;
                SharedPreferences.Editor editor = getContext().getSharedPreferences("max_score", Context.MODE_PRIVATE).edit();
                editor.putInt(game.id, max_score);
                editor.apply();
                Intent intent = new Intent();
                intent.putExtra("best", max_score);
                parent.setResult(Activity.RESULT_OK, intent);
                UserManager.uploadScore(game, max_score, null);
            }
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
        static final int EVENT_GAME_OVER_FROM_GAME_LIST = 0x01;
        static final int EVENT_GAME_OVER_FROM_CHALLENGE = 0x02;

        private GameView gameView;
        MessageHandler(GameView gameView) {
            this.gameView = gameView;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_GAME_OVER_FROM_GAME_LIST: {
                    int my_score = msg.arg1;
                    int old_max = msg.arg2;
                    int time_limit = gameView.game.time_limit * 1000;
                    new AlertDialog.Builder(gameView.parent)
                        .setTitle(time_limit > 0 && gameView.game_time >= time_limit ? "时间到" : "游戏已结束")
                        .setMessage("你的分数为：" + my_score + " !\n" +
                                (my_score > old_max ? "恭喜你创造了新的记录！" : "最高分为：" + old_max + "，继续加油吧") +
                                (gameView.forgive > 0 ? "\n你一共原谅了" + gameView.forgive + "次 ^_^" : "")
                        )
                        .setCancelable(false)
                        .setPositiveButton("返回主菜单", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gameView.parent.finish();
                            }
                        })
                        .setNegativeButton("死亡回放", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    break;
                }
                case EVENT_GAME_OVER_FROM_CHALLENGE: {
                    float my_velocity = msg.arg1 / 10000.0f;
                    float old_max = msg.arg2 / 10000.0f;
                    String my_velocity_str = String.format("%.3f tills/s", my_velocity);
                    String old_max_str = String.format("%.3f tills/s", old_max);
                    int time_limit = gameView.game.time_limit * 1000;
                    new AlertDialog.Builder(gameView.parent)
                        .setTitle(time_limit > 0 && gameView.game_time >= time_limit ? "时间到" : "游戏已结束")
                        .setMessage("当前速度为：" + my_velocity_str + " !\n" +
                                (my_velocity > old_max ? "恭喜你创造了新的记录！" : "你的历史最快纪录为：" + old_max_str + "，继续加油吧") +
                                (gameView.forgive > 0 ? "\n你一共原谅了" + gameView.forgive + "次 ^_^" : "")
                        )
                        .setCancelable(false)
                        .setPositiveButton("返回主菜单", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gameView.parent.finish();
                            }
                        })
                        .setNegativeButton("死亡回放", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    break;
                }
            }
        }
    }

    private MessageHandler handler = new MessageHandler(this);

}
