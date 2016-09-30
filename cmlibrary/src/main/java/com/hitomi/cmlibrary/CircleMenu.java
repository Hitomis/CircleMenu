package com.hitomi.cmlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by hitomi on 2016/9/28.
 */
public class CircleMenu extends View {

    private static final int STATUS_MENU_OPEN = 1;

    private static final int STATUS_MENU_OPENED = 1 << 1;

    private static final int STATUS_MENU_CLOSE = 1 << 2;

    private static final int STATUS_MENU_CLOSE_CLEAR = 1 << 3;

    private static final int STATUS_MENU_CLOSED = 1 << 4;

    private static final int STATUS_MENU_CANCEL = 1 << 5;

    private static final int ITEM_NUM = 5;

    private final int PART_SIZE = dip2Px(20);

    private final int[] menuColors = new int[] {
            Color.parseColor("#C0C0C0"),
            Color.parseColor("#258CFF"),
            Color.parseColor("#30A400"),
            Color.parseColor("#FF4B32"),
            Color.parseColor("#8A39FF"),
            Color.parseColor("#FF6A00")
    };

    private final float circleMenuRadius = PART_SIZE * 3;

    private float itemMenuRadius;

    private float centerX, centerY;

    private float fraction;

    private float pathLength;

    private int clickIndex;

    private int rotateAngle;

    private int pressedColor;

    private int status;

    private boolean pressed;

    private RectF[] menuRectFs = new RectF[ITEM_NUM + 1];

    private Paint oPaint, cPaint;

    private PathMeasure pathMeasure;

    private Path path, dstPath;

    public CircleMenu(Context context) {
        this(context, null);
    }

    public CircleMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        status = STATUS_MENU_CLOSED;
        init();
    }

    private void init() {
        oPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setStrokeCap(Paint.Cap.ROUND);

        path = new Path();
        dstPath = new Path();
        pathMeasure = new PathMeasure();

        for (int i = 0; i < menuRectFs.length; i++) {
            menuRectFs[i] = new RectF();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureSize = PART_SIZE * 5 * 2;
        setMeasuredDimension(measureSize, measureSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        centerX = getMeasuredWidth() / 2;
        centerY = centerX;

        path.addCircle(centerX, centerY, circleMenuRadius, Path.Direction.CW);
        pathMeasure.setPath(path, true);
        pathLength = pathMeasure.getLength();

        menuRectFs[0].set(centerX - PART_SIZE, centerY - PART_SIZE, centerX + PART_SIZE, centerY + PART_SIZE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (status) {
            case STATUS_MENU_CLOSED:
                drawCenterMenu(canvas);
                break;
            case STATUS_MENU_OPEN:
                drawCenterMenu(canvas);
                drawAroundMenu(canvas);
                break;
            case STATUS_MENU_OPENED:
                drawCenterMenu(canvas);
                drawAroundMenu(canvas);
                break;
            case STATUS_MENU_CLOSE:
                drawCenterMenu(canvas);
                drawAroundMenu(canvas);
                drawCircleMenu(canvas);
                break;
            case STATUS_MENU_CLOSE_CLEAR:
                drawCenterMenu(canvas);
                drawCircleMenu(canvas);
                break;
            case STATUS_MENU_CANCEL:
                drawCenterMenu(canvas);
                drawAroundMenu(canvas);
                break;
        }

    }

    /**
     * 绘制周围子菜单环绕的圆环路径
     * @param canvas
     */
    private void drawCircleMenu(Canvas canvas) {
        canvas.save();
        if (status == STATUS_MENU_CLOSE) {
            canvas.rotate(rotateAngle, centerX, centerY);
            dstPath.reset();
            dstPath.lineTo(0, 0);
            pathMeasure.getSegment(0, pathLength * fraction, dstPath, true);
            cPaint.setStrokeWidth(PART_SIZE * 2);
            cPaint.setColor(menuColors[clickIndex]);
            canvas.drawPath(dstPath, cPaint);
         } else {
            cPaint.setStrokeWidth(PART_SIZE * 2 + PART_SIZE * .5f * fraction);
            cPaint.setColor(calcAlphaColor(menuColors[clickIndex], true));
            canvas.drawCircle(centerX, centerY, circleMenuRadius + PART_SIZE * .5f * fraction, cPaint);
        }

        canvas.restore();
    }

    /**
     * 绘制周围子菜单项按钮
     * @param canvas
     */
    private void drawAroundMenu(Canvas canvas) {
        int itemX, itemY, angle;
        final float offsetRadius = 1.5f;
        for (int i = 0; i < ITEM_NUM; i++) {
            angle = i * (360 / ITEM_NUM);
            if (status == STATUS_MENU_OPEN) {
                itemX = (int) (centerX + Math.sin(Math.toRadians(angle)) * (circleMenuRadius - (1 - fraction) * PART_SIZE * offsetRadius));
                itemY = (int) (centerY - Math.cos(Math.toRadians(angle)) * (circleMenuRadius - (1 - fraction) * PART_SIZE * offsetRadius));
                oPaint.setColor(calcAlphaColor(menuColors[i + 1], false));
            } else if (status == STATUS_MENU_CANCEL) {
                itemX = (int) (centerX + Math.sin(Math.toRadians(angle)) * (circleMenuRadius -  fraction * PART_SIZE * offsetRadius));
                itemY = (int) (centerY - Math.cos(Math.toRadians(angle)) * (circleMenuRadius -  fraction * PART_SIZE * offsetRadius));
                oPaint.setColor(calcAlphaColor(menuColors[i + 1], true));
            } else {
                itemX = (int) (centerX + Math.sin(Math.toRadians(angle)) * circleMenuRadius);
                itemY = (int) (centerY - Math.cos(Math.toRadians(angle)) * circleMenuRadius);
                oPaint.setColor(menuColors[i + 1]);
            }
            if (pressed && clickIndex - 1 == i) {
                oPaint.setColor(pressedColor);
            }
            canvas.drawCircle(itemX, itemY, itemMenuRadius, oPaint);
            menuRectFs[i + 1].set(itemX - PART_SIZE, itemY - PART_SIZE, itemX + PART_SIZE, itemY + PART_SIZE);
        }
    }

    /**
     * 绘制中间的菜单开关按钮
     * @param canvas
     */
    private void drawCenterMenu(Canvas canvas) {
        float centerMenuRadius;
        if (status == STATUS_MENU_CLOSE) {
            centerMenuRadius = PART_SIZE * (1 - fraction);
        } else if (status == STATUS_MENU_CLOSE_CLEAR) {
            centerMenuRadius = PART_SIZE * fraction;
        } else if (status == STATUS_MENU_CLOSED || status == STATUS_MENU_CANCEL) {
            centerMenuRadius = PART_SIZE;
        } else {
            centerMenuRadius = PART_SIZE;
        }
        if (pressed && clickIndex == 0) {
            oPaint.setColor(pressedColor);
        } else {
            oPaint.setColor(menuColors[0]);
        }
        canvas.drawCircle(centerX, centerY, centerMenuRadius, oPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (status == STATUS_MENU_CLOSE || status == STATUS_MENU_CLOSE_CLEAR) return true;
        int index = clickWhichRectF(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                if (index != -1) {
                    clickIndex = index;
                    updatePressEffect(index, pressed);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (index == -1) {
                    pressed = false;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                pressed = false;
                if (index != -1) {
                    clickIndex = index;
                    updatePressEffect(index, pressed);
                }
                if (index == 0) { // 点击的是中间的按钮
                    if (status == STATUS_MENU_CLOSED) {
                        status = STATUS_MENU_OPEN;
                        startOpenMenuAnima();
                    } else if (status == STATUS_MENU_OPENED) {
                        status = STATUS_MENU_CANCEL;
                        startCancelMenuAnima();
                    }
                } else { // 点击的是周围子菜单项按钮
                    if (status == STATUS_MENU_OPENED && index != -1) {
                        status = STATUS_MENU_CLOSE;
                        rotateAngle = clickIndex * (360 / ITEM_NUM) -  (360 / ITEM_NUM) - 90 ;
                        startCloseMeunAnima();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 更新按钮的状态
     * @param menuIndex
     * @param press
     */
    private void updatePressEffect(int menuIndex, boolean press) {
        if (press) {
            pressedColor = getPressedEffectColor(menuIndex);
        }
        invalidate();
    }

    /**
     * 获取按钮被按下的颜色
     * @param menuIndex
     * @return
     */
    private int getPressedEffectColor(int menuIndex) {
        int color;
        color = menuColors[menuIndex];
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f;
        return Color.HSVToColor(hsv);
    }

    /**
     * 用于完成在 View 中的圆环逐渐扩散消失的动画效果 <br/>
     *
     * 根据 fraction 调整 color 的 Alpha 值
     * @param color 被调整 Alpha 值的颜色
     * @param reverse true : 由不透明到透明的顺序调整，否则就逆序
     * @return
     */
    private int calcAlphaColor(int color, boolean reverse) {
        int alpha;
        if (reverse) { // 由不透明到透明
            alpha = (int) (255 * (1.f - fraction));
        } else { // 由透明到不透明
            alpha = (int) (255 * fraction);
        }
        if (alpha >= 255) alpha = 255;
        if (alpha <= 0) alpha = 0;
        return ColorUtils.setAlphaComponent(color, alpha);
    }

    /**
     * 启动打开菜单动画
     */
    private void startOpenMenuAnima() {
        ValueAnimator openAnima = ValueAnimator.ofFloat(1.f, 100.f);
        openAnima.setDuration(500);
        openAnima.setInterpolator(new OvershootInterpolator());
        openAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fraction = valueAnimator.getAnimatedFraction();
                itemMenuRadius = fraction * PART_SIZE;
                invalidate();
            }
        });
        openAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_OPENED;
            }
        });
        openAnima.start();
    }

    /**
     * 启动取消动画
     */
    private void startCancelMenuAnima() {
        ValueAnimator cancelAnima = ValueAnimator.ofFloat(1.f, 100.f);
        cancelAnima.setDuration(500);
        cancelAnima.setInterpolator(new OvershootInterpolator());
        cancelAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fraction = valueAnimator.getAnimatedFraction();
                itemMenuRadius = (1 - fraction) * PART_SIZE;
                invalidate();
            }
        });
        cancelAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_CLOSED;
            }
        });
        cancelAnima.start();
    }

    /**
     * 开启关闭菜单动画
     */
    private void startCloseMeunAnima() {
        ValueAnimator closeAnima = ValueAnimator.ofFloat(1.f, 100.f);
        closeAnima.setDuration(500);
        closeAnima.setInterpolator(new AccelerateDecelerateInterpolator());
        closeAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fraction = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });
        closeAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_CLOSE_CLEAR;
            }
        });

        ValueAnimator clearAnima = ValueAnimator.ofFloat(1.f, 100.f);
        clearAnima.setDuration(600);
        clearAnima.setInterpolator(new DecelerateInterpolator());
        clearAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fraction = valueAnimator.getAnimatedFraction();
                invalidate();
            }
        });
        clearAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_CLOSED;
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(closeAnima).before(clearAnima);
        animatorSet.start();
    }

    private int clickWhichRectF(float x, float y) {
        int which = -1;
        for (int i = 0; i < menuRectFs.length; i++) {
            if (menuRectFs[i].contains(x, y)) {
                which = i;
                break;
            }
        }
        return which;
    }

    public int dip2Px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
