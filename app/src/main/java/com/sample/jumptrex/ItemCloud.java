package com.sample.jumptrex;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by ASUS on 7/22/2015.
 */
public class ItemCloud extends BaseItem {

    private static class config {
        public static int HEIGHT = 14;
        public static int MAX_CLOUD_GAP = 400;
        public static int MAX_SKY_LEVEL = 30;
        public static int MIN_CLOUD_GAP = 100;
        public static int MIN_SKY_LEVEL = 71;
        public static int WIDTH = 46;
    }

    private Point spritePos;
    private int containerWidth;
    public double xPos;
    public int yPos;
    public boolean remove;
    public double cloudGap;

    public ItemCloud(Point sprite, int w) {
        this.spritePos = sprite;
        this.containerWidth = w;
        this.xPos = containerWidth;
        this.yPos = 0;
        this.remove = false;
        this.cloudGap = getRandomNum(config.MIN_CLOUD_GAP,
                config.MAX_CLOUD_GAP);
        this.init();
    }

    private void init() {
        this.yPos = (int) getRandomNum(config.MAX_SKY_LEVEL,
                config.MIN_SKY_LEVEL);
    }

    @Override
    public void update(Object... args) {
        super.update(args);
        double speed = 0;
        if (args.length > 1) {
            speed = (double) args[1];
        } else {
        }
        if (!this.remove) {
            this.xPos -= Math.ceil(speed);
            // Mark as removeable if no longer in the canvas.
            if (!this.isVisible()) {
                this.remove = true;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int sourceWidth = config.WIDTH;
        int sourceHeight = config.HEIGHT;
        double scale = BaseItem.Scale;
        double scale2 = BaseItem.ScaleTarget;
        int sl = (int) (spritePos.x * scale);
        int st = (int) (spritePos.y * scale);
        int sr = (int) (sl + sourceWidth * scale);
        int sb = (int) (st + sourceHeight * scale);
        int tl = (int) (xPos * scale2);
        int tt = (int) (yPos * scale2);
        int tr = (int) (tl + config.WIDTH * scale2);
        int tb = (int) (tt + config.HEIGHT * scale2);
        Rect sRect = getScaledSource(spritePos.x, spritePos.y, sourceWidth, sourceHeight);
        Rect tRect = getScaledTarget((int) xPos, yPos, config.WIDTH, config.HEIGHT);
        canvas.drawBitmap(BaseItem.BaseBitmap, sRect, tRect, null);
    }

    private boolean isVisible() {
        return this.xPos + config.WIDTH > 0;
    }
}
