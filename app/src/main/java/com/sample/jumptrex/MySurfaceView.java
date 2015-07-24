package com.sample.jumptrex;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ASUS on 7/14/2015.
 */
public class MySurfaceView extends SurfaceView {

    private boolean _debug = true;
    private boolean collision = false;
    private double distanceRan;
    public int Width;
    private boolean started = false;
    private boolean playingIntro = false;
    private double highestScore;
    private long runningTime;
    private boolean paused = false;
    public int Height;
    private int _frameCounter;
    private int _updateCounter;
    private int _drawCounter;
    private boolean crashed = false;
    private boolean activated = false;
    private boolean reqId = false;
    public static int FPS = 60;
    private Point dimensions = new Point(600, 150);
    public static String _tag = "JumpTRex";
    private int playCount;
    private static List<BaseItem> itemList = new ArrayList<BaseItem>();
    private static Timer _timer;
    private ItemTRex tRex;
    private double currentSpeed;
    private ItemHorizon horizon;
    private long avgDeltaTime;
    private int cc = 0;
    private int ccc2 = 0;
    private int[] ccc = new int[]{2, 4, 8, 16};
    private ItemDistanceMeter distanceMeter;
    private Date time;
    private static int newFPS = 60;
    private static volatile boolean running = false;

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public MySurfaceView(Context context) {
        super(context);
        Init(context);
    }

    private void Init(Context context) {
        Log("init");
        currentSpeed = Runner.config.SPEED;
        ItemHorizon horizon = new ItemHorizon(new Point(0, 0), dimensions, Runner.config.GAP_COEFFICIENT);
        this.horizon = horizon;
        ItemDistanceMeter d = new ItemDistanceMeter(Runner.spritePos.TEXT_SPRITE, dimensions.x);
        this.distanceMeter = d;
        ItemTRex tRex = new ItemTRex(Runner.spritePos.TREX);
        this.tRex = tRex;
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onKeyDown();
                        break;
                    case MotionEvent.ACTION_UP:
                        onKeyUp();
                        break;
                }
                return true;
            }
        });
        update();
    }

    private void onKeyDown() {
        if (!this.crashed) {
            if (!this.activated) {
                this.activated = true;
            }
            if (!tRex.jumping) {
                tRex.startJump((int) currentSpeed);
            }
        }
        if (this.crashed) {
            this.restart();
        }
    }

    private void onKeyUp() {
        if (this.isRunning()) {
            tRex.endJump();
        } else if (this.crashed) {
            // Check that enough time has elapsed before allowing jump key to restart.
            long deltaTime = new Date().getTime() - this.time.getTime();
            if (deltaTime >= Runner.config.GAMEOVER_CLEAR_TIME) {
                this.restart();
            }
        } else if (this.paused) {
            // Reset the jump state
            tRex.reset();
            this.play();
        }
    }

    private void play() {
        Log("play");
        if (!this.crashed) {
            this.activated = true;
            this.paused = false;
            tRex.update(0, ItemTRex.Status.RUNNING);
            this.time = new Date();
            this.update();
        }
    }

    private void restart() {
        Log("restart");
        if (!isRunning()) {
            this.playCount++;
            this.runningTime = 0;
            this.activated = true;
            this.crashed = false;
            this.distanceRan = 0;
            this.setSpeed(Runner.config.SPEED);
            this.time = new Date();
            this.distanceMeter.reset(this.highestScore);
            this.horizon.reset();
            this.tRex.reset();
            this.update();
        }
    }

    public void Start() {
        Log("SSStart");
    }

    public void Stop() {
        Log("SSStop");
    }

    private void Draw() {
        SurfaceHolder sfh = null;
        Canvas canvas = null;
        try {
            sfh = this.getHolder();
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                int w = canvas.getWidth();
                int h = canvas.getHeight();
                if (w != Width || h != Height) {
                    Width = w;
                    Height = h;
                    Log.i("dimensions", " sssssssssssssssssssssssssssssssssssssssss " + Width + ", " + Height);
                    if (Width / Height >= 4) {
                        BaseItem.ScaleTarget = (double) Height / 150;
                        BaseItem.startY = 0;
                        BaseItem.startX = (Width - 4 * Height) / 2;
                    } else {
                        BaseItem.ScaleTarget = (double) Width / 600;
                        BaseItem.startY = (Height - Width / 4) / 2;
                        BaseItem.startX = 0;
                    }
                    Log.i("start", BaseItem.startX + ", " + BaseItem.startY);
                }
                canvas.drawColor(Color.WHITE);
                horizon.draw(canvas);
                distanceMeter.draw(canvas);
                tRex.draw(canvas);
            } else {
                Log.i("canvas", "canvas null");
            }
        } catch (Exception e) {
            Log.e(_tag, e.toString());
        } finally {
            if (sfh != null && canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    /*
    private synchronized boolean UpdateItems() {
        boolean needDraw = false;
        long deltaTime = 0;
        if (time == null) {
            time = new Date();
            return needDraw;
        } else {
            Date now = new Date();
            deltaTime = now.getTime() - time.getTime();
            time = now;
            runningTime += deltaTime;
        }
        boolean hasObstacles = this.runningTime > Runner.config.CLEAR_TIME;
        avgDeltaTime += deltaTime;
        for (BaseItem item : itemList) {
            boolean ret;
            if (item instanceof ItemHorizon) {
                ret = item.Update(deltaTime, currentSpeed);
            } else if (item instanceof ItemDistanceMeter)
                ret = distanceMeter.Update(deltaTime,
                        Math.ceil(this.distanceRan));
            else
                ret = item.Update(deltaTime);
            if (ret)
                needDraw = true;
        }
        _updateCounter++;
        if (!collision) {
            this.distanceRan += this.currentSpeed * deltaTime * BaseItem.FPS / 1000;
            if (this.currentSpeed < Runner.config.MAX_SPEED) {
                this.currentSpeed += Runner.config.ACCELERATION;
            }
        } else {
            this.gameOver();
        }
        return needDraw;
    }
    */

    private void gameOver() {
        //vibrate(200);
        Log("game over");
        this.stop();
        this.crashed = true;
        this.distanceMeter.acheivement = false;
        this.tRex.update(100, ItemTRex.Status.CRASHED);
        // Game over panel.
        /*
        if (!this.gameOverPanel) {
            this.gameOverPanel = new GameOverPanel(this.canvas,
                    this.spriteDef.TEXT_SPRITE, this.spriteDef.RESTART,
                    this.dimensions);
        } else {
            this.gameOverPanel.draw();
        }
        */
        // Update the high score.
        if (this.distanceRan > this.highestScore) {
            this.highestScore = Math.ceil(this.distanceRan);
            this.distanceMeter.setHighScore(this.highestScore);
        }
        // Reset the time clock.
        this.time = new Date();
    }

    private void stop() {
        Log("stop");
        this.activated = false;
        this.paused = true;
        stopTimer();
    }

    private int pCount = 0;
    private void update() {
        Date now = new Date();
        long deltaTime = 0;
        if (this.time != null)
            deltaTime = now.getTime() - this.time.getTime();
        this.time = now;
        if (this.activated) {
            this.runningTime += deltaTime;
            boolean hasObstacles = this.runningTime > Runner.config.CLEAR_TIME;
            // First jump triggers the intro.
            if (this.tRex.jumpCount == 1 && !this.playingIntro) {
                this.playIntro();
            }
            // The horizon doesn't move until the intro is over.
            if (this.playingIntro) {
                pCount++;
                if (pCount > 10) {
                    startGame();
                    pCount = 0;
                }
                this.horizon.update((long)0, this.currentSpeed, hasObstacles);
            } else {
                deltaTime = !this.started ? 0 : deltaTime;
                this.horizon.update(deltaTime, this.currentSpeed, hasObstacles);
            }
            // Check for collisions.
            //boolean collision = hasObstacles && checkForCollision(this.horizon.obstacles[0], this.tRex);
            boolean collision = false;
            if (!collision) {
                this.distanceRan += this.currentSpeed * deltaTime * FPS / 1000;
                if (this.currentSpeed < Runner.config.MAX_SPEED) {
                    this.currentSpeed += Runner.config.ACCELERATION;
                }
            } else {
                this.gameOver();
            }
            this.distanceMeter.update(deltaTime, Math.ceil(this.distanceRan));
        }
        if (!this.crashed) {
            this.tRex.update(deltaTime);
            startTimer();
        } else {
            stopTimer();
        }
        Draw();
    }

    private synchronized void startTimer() {
        if (newFPS != FPS) {
            if (running) {
                _timer.cancel();
                _timer = null;
                running = false;
            }
            FPS = newFPS;
            Log("fps: " + FPS);
        }
        if (!running) {
            Log("timer on");
            int spf = (int) ((double) 1000 / FPS);
            _timer = new Timer();
            _timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            }, spf, spf);
            running = true;
        }
    }

    private synchronized void stopTimer() {
        if (running) {
            Log("timer off");
            _timer.cancel();
            _timer = null;
            running = false;
        }
    }

    private synchronized boolean isRunning() {
        return running;
    }

    private void startGame() {
        this.runningTime = 0;
        this.playingIntro = false;
        this.tRex.playingIntro = false;
        this.playCount++;
    }

    private void playIntro() {
        if (!this.started && !this.crashed) {
            Log("playIntro");
            this.playingIntro = true;
            this.tRex.playingIntro = true;
            this.activated = true;
            this.started = true;
        } else if (this.crashed) {
            this.restart();
        }
    }

    private void setSpeed(Object... args) {
        if (args.length > 0) {
            this.currentSpeed = (double) args[0];
        }
    }

    public static synchronized void SetFPS(int fps) {
        newFPS = fps;
    }

    private void Log(String str) {
        if (_debug)
            Log.i(_tag, str);
    }
}
