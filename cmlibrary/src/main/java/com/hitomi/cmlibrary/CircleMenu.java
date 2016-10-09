package com.hitomi.cmlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by hitomi on 2016/9/28.
 *
 * github : https://github.com/Hitomis <br/>
 *
 * email : 196425254@qq.com
 */
public class CircleMenu extends View {

    private static final int STATUS_MENU_OPEN = 1;

    private static final int STATUS_MENU_OPENED = 1 << 1;

    private static final int STATUS_MENU_CLOSE = 1 << 2;

    private static final int STATUS_MENU_CLOSE_CLEAR = 1 << 3;

    private static final int STATUS_MENU_CLOSED = 1 << 4;

    private static final int STATUS_MENU_CANCEL = 1 << 5;

    private static final int ITEM_NUM = 5;

    private final int partSize = dip2px(20);

    private final int iconSize = partSize * 4 / 5;

    private final float circleMenuRadius = partSize * 3;

    private final int shadowRadius = 5;

    private float itemMenuRadius;

    private float fraction, rFraction;

    private float pathLength;

    private int[] menuColors = new int[] {
            Color.parseColor("#CDCDCD"),
            Color.parseColor("#258CFF"),
            Color.parseColor("#30A400"),
            Color.parseColor("#FF4B32"),
            Color.parseColor("#8A39FF"),
            Color.parseColor("#FF6A00")
    };

    private int centerX, centerY;

    private int clickIndex;

    private int rotateAngle;

    private int itemIconSize;

    private int pressedColor;

    private int status;

    private boolean pressed;

    private RectF[] menuRectFs = new RectF[ITEM_NUM + 1];

    private Drawable[] iconDrawables = new Drawable[ITEM_NUM];

    private Drawable openMenuIcon, closeMenuIcon;

    private Paint oPaint, cPaint, sPaint;

    private PathMeasure pathMeasure;

    private Path path, dstPath;

    private OnMenuSelectedListener onMenuSelectedListener;

    private OnMenuStatusChangeListener onMenuStatusChangeListener;

    public CircleMenu(Context context) {
        this(context, null);
    }

    public CircleMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        status = STATUS_MENU_CLOSED;
        init();
    }

    private void init() {
        initTool();

        centerX = partSize * 5  ;
        centerY = centerX;

        path.addCircle(centerX, centerY, circleMenuRadius, Path.Direction.CW);
        pathMeasure.setPath(path, true);
        pathLength = pathMeasure.getLength();

        menuRectFs[0].set(centerX - partSize, centerY - partSize, centerX + partSize, centerY + partSize);
    }

    private void initTool() {
        oPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setStrokeCap(Paint.Cap.ROUND);

        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sPaint.setStyle(Paint.Style.FILL);

        path = new Path();
        dstPath = new Path();
        pathMeasure = new PathMeasure();

        for (int i = 0; i < menuRectFs.length; i++) {
            menuRectFs[i] = new RectF();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureSize = partSize * 5 * 2;
        setMeasuredDimension(measureSize, measureSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (status) {
            case STATUS_MENU_CLOSED:
                drawMainMenu(canvas);
                break;
            case STATUS_MENU_OPEN:
                drawMainMenu(canvas);
                drawSubMenu(canvas);
                break;
            case STATUS_MENU_OPENED:
                drawMainMenu(canvas);
                drawSubMenu(canvas);
                break;
            case STATUS_MENU_CLOSE:
                drawMainMenu(canvas);
                drawSubMenu(canvas);
                drawCircleMenu(canvas);
                break;
            case STATUS_MENU_CLOSE_CLEAR:
                drawMainMenu(canvas);
                drawCircleMenu(canvas);
                break;
            case STATUS_MENU_CANCEL:
                drawMainMenu(canvas);
                drawSubMenu(canvas);
                break;
        }
    }

    /**
     * 绘制周围子菜单环绕的圆环路径
     * @param canvas
     */
    private void drawCircleMenu(Canvas canvas) {
        if (status == STATUS_MENU_CLOSE) {
            drawCirclePath(canvas);
            drawCircleIcon(canvas);
         } else {
            cPaint.setStrokeWidth(partSize * 2 + partSize * .5f * fraction);
            cPaint.setColor(calcAlphaColor(menuColors[clickIndex], true));
            canvas.drawCircle(centerX, centerY, circleMenuRadius + partSize * .5f * fraction, cPaint);
        }
    }

    /**
     * 绘制子菜单转动时的图标
     * @param canvas
     */
    private void drawCircleIcon(Canvas canvas) {
        canvas.save();
        Drawable selDrawable = iconDrawables[clickIndex - 1];
        int startAngle = (clickIndex - 1) * (360 / ITEM_NUM);
        int endAngle = 360 + startAngle;
        int itemX = (int) (centerX + Math.sin(Math.toRadians((endAngle - startAngle) * fraction + startAngle)) * circleMenuRadius);
        int itemY = (int) (centerY - Math.cos(Math.toRadians((endAngle - startAngle) * fraction + startAngle)) * circleMenuRadius);
        canvas.rotate(360 * fraction, itemX, itemY);
        selDrawable.setBounds(itemX - iconSize / 2, itemY - iconSize / 2, itemX + iconSize / 2, itemY + iconSize / 2);
        selDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制子菜单项转动时的轨迹路径
     * @param canvas
     */
    private void drawCirclePath(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotateAngle, centerX, centerY);
        dstPath.reset();
        dstPath.lineTo(0, 0);
        pathMeasure.getSegment(0, pathLength * fraction, dstPath, true);
        cPaint.setStrokeWidth(partSize * 2);
        cPaint.setColor(menuColors[clickIndex]);
        canvas.drawPath(dstPath, cPaint);
        canvas.restore();
    }

    /**
     * 绘制周围子菜单项按钮
     * @param canvas
     */
    private void drawSubMenu(Canvas canvas) {
        int itemX, itemY, angle;
        final float offsetRadius = 1.5f;
        for (int i = 0; i < ITEM_NUM; i++) {
            angle = i * (360 / ITEM_NUM);
            if (status == STATUS_MENU_OPEN) {
                itemX = (int) (centerX + Math.sin(Math.toRadians(angle)) * (circleMenuRadius - (1 - fraction) * partSize * offsetRadius));
                itemY = (int) (centerY - Math.cos(Math.toRadians(angle)) * (circleMenuRadius - (1 - fraction) * partSize * offsetRadius));
                oPaint.setColor(calcAlphaColor(menuColors[i + 1], false));
                sPaint.setColor(calcAlphaColor(menuColors[i + 1], false));
            } else if (status == STATUS_MENU_CANCEL) {
                itemX = (int) (centerX + Math.sin(Math.toRadians(angle)) * (circleMenuRadius -  fraction * partSize * offsetRadius));
                itemY = (int) (centerY - Math.cos(Math.toRadians(angle)) * (circleMenuRadius -  fraction * partSize * offsetRadius));
                oPaint.setColor(calcAlphaColor(menuColors[i + 1], true));
                sPaint.setColor(calcAlphaColor(menuColors[i + 1], true));
            } else {
                itemX = (int) (centerX + Math.sin(Math.toRadians(angle)) * circleMenuRadius);
                itemY = (int) (centerY - Math.cos(Math.toRadians(angle)) * circleMenuRadius);
                oPaint.setColor(menuColors[i + 1]);
                sPaint.setColor(menuColors[i + 1]);
            }
            if (pressed && clickIndex - 1 == i) {
                oPaint.setColor(pressedColor);
            }
            drawMenuShadow(canvas, itemX, itemY, itemMenuRadius);
            canvas.drawCircle(itemX, itemY, itemMenuRadius, oPaint);
            drawSubMenuIcon(canvas, itemX, itemY, i);
            menuRectFs[i + 1].set(itemX - partSize, itemY - partSize, itemX + partSize, itemY + partSize);
        }
    }

    /**
     * 绘制子菜单项图标
     * @param canvas
     * @param centerX
     * @param centerY
     * @param index
     */
    private void drawSubMenuIcon(Canvas canvas, int centerX, int centerY, int index) {
        int diff;
        if (status == STATUS_MENU_OPEN || status == STATUS_MENU_CANCEL) {
            diff = itemIconSize / 2;
        } else {
            diff = iconSize / 2;
        }
        resetBoundsAndDrawIcon(canvas, iconDrawables[index], centerX, centerY, diff);
    }

    private void resetBoundsAndDrawIcon(Canvas canvas, Drawable drawable, int centerX, int centerY, int diff) {
        drawable.setBounds(centerX - diff, centerY - diff, centerX + diff, centerY + diff);
        drawable.draw(canvas);
    }

    /**
     * 绘制中间的菜单开关按钮
     * @param canvas
     */
    private void drawMainMenu(Canvas canvas) {
        float centerMenuRadius, realFraction;
        if (status == STATUS_MENU_CLOSE) {
            // 中心主菜单按钮以两倍速度缩小
            realFraction = (1 - fraction * 2) == 0 ? 0 : (1 - fraction * 2);
            centerMenuRadius = partSize * realFraction;
        } else if (status == STATUS_MENU_CLOSE_CLEAR) {
            // 中心主菜单按钮以四倍速度扩大
            realFraction = fraction * 4 >= 1 ? 1 : fraction * 4;
            centerMenuRadius = partSize * realFraction;
        } else if (status == STATUS_MENU_CLOSED || status == STATUS_MENU_CANCEL) {
            centerMenuRadius = partSize;
        } else {
            centerMenuRadius = partSize;
        }
        if (status == STATUS_MENU_OPEN || status == STATUS_MENU_OPENED || status == STATUS_MENU_CLOSE) {
            oPaint.setColor(calcPressedEffectColor(0, .5f));
        } else if (pressed && clickIndex == 0) {
            oPaint.setColor(pressedColor);
        } else {
            oPaint.setColor(menuColors[0]);
            sPaint.setColor(menuColors[0]);
        }
        drawMenuShadow(canvas, centerX, centerY, centerMenuRadius);
        canvas.drawCircle(centerX, centerY, centerMenuRadius, oPaint);
        drawMainMenuIcon(canvas);
    }

    private void drawMainMenuIcon(Canvas canvas) {
        canvas.save();
        switch (status) {
            case STATUS_MENU_CLOSED:
                openMenuIcon.draw(canvas);
                break;
            case STATUS_MENU_OPEN:
                canvas.rotate(45 * (fraction - 1), centerX, centerY);
                resetBoundsAndDrawIcon(canvas, closeMenuIcon, centerX, centerY, iconSize / 2);
                break;
            case STATUS_MENU_OPENED:
                resetBoundsAndDrawIcon(canvas, closeMenuIcon, centerX, centerY, iconSize / 2);
                break;
            case STATUS_MENU_CLOSE:
                resetBoundsAndDrawIcon(canvas, closeMenuIcon, centerX, centerY, itemIconSize / 2);
                break;
            case STATUS_MENU_CLOSE_CLEAR:
                canvas.rotate(90 * (rFraction - 1), centerX, centerY);
                resetBoundsAndDrawIcon(canvas, openMenuIcon, centerX, centerY, itemIconSize / 2);
                break;
            case STATUS_MENU_CANCEL:
                canvas.rotate(-45 * fraction, centerX, centerY);
                closeMenuIcon.draw(canvas);
                break;
        }
        canvas.restore();
    }

    /**
     * 绘制菜单按钮阴影
     * @param canvas
     * @param centerX
     * @param centerY
     */
    private void drawMenuShadow(Canvas canvas, int centerX, int centerY, float radius) {
        if (radius + shadowRadius > 0) {
            sPaint.setShader(new RadialGradient(centerX, centerY, radius + shadowRadius,
                    Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP));
            canvas.drawCircle(centerX, centerY, radius + shadowRadius, sPaint);
        }
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
                        if (onMenuSelectedListener != null)
                            onMenuSelectedListener.onMenuSelected(index - 1);
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
            pressedColor = calcPressedEffectColor(menuIndex, .15f);
        }
        invalidate();
    }

    /**
     * 获取按钮被按下的颜色
     * @param menuIndex
     * @param depth 取值范围为[0, 1].值越大，颜色越深
     * @return
     */
    private int calcPressedEffectColor(int menuIndex, float depth) {
        int color;
        color = menuColors[menuIndex];
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (1.f - depth);
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
                itemMenuRadius = fraction * partSize;
                itemIconSize = (int) (fraction * iconSize);
                invalidate();
            }
        });
        openAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_OPENED;
                if (onMenuStatusChangeListener != null)
                    onMenuStatusChangeListener.onMenuOpened();
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
        cancelAnima.setInterpolator(new AnticipateInterpolator());
        cancelAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fraction = valueAnimator.getAnimatedFraction();
                itemMenuRadius = (1 - fraction) * partSize;
                itemIconSize = (int) ((1 - fraction) * iconSize);
                invalidate();
            }
        });
        cancelAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_CLOSED;
                if (onMenuStatusChangeListener != null)
                    onMenuStatusChangeListener.onMenuClosed();
            }
        });
        cancelAnima.start();
    }

    /**
     * 开启关闭菜单动画 </br>
     * <p>关闭菜单动画分为三部分</p>
     * <ur>
     *     <li>选中菜单项转动一周</li>
     *     <li>环状轨迹扩散消失</li>
     *     <li>主菜单按钮旋转</li>
     * </ur>
     *
     */
    private void startCloseMeunAnima() {
        // 选中菜单项转动一周动画驱动
        ValueAnimator aroundAnima = ValueAnimator.ofFloat(1.f, 100.f);
        aroundAnima.setDuration(600);
        aroundAnima.setInterpolator(new AccelerateDecelerateInterpolator());
        aroundAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fraction = valueAnimator.getAnimatedFraction();
                // 中心主菜单图标以两倍速度缩小
                float animaFraction = fraction * 2 >= 1 ? 1 : fraction * 2;
                itemIconSize = (int) ((1 - animaFraction) * iconSize);
                invalidate();
            }
        });
        aroundAnima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_CLOSE_CLEAR;
            }
        });

        // 环状轨迹扩散消失动画驱动
        ValueAnimator spreadAnima = ValueAnimator.ofFloat(1.f, 100.f);
        spreadAnima.setInterpolator(new LinearInterpolator());
        spreadAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                fraction = valueAnimator.getAnimatedFraction();
            }
        });

        // 主菜单转动动画驱动
        ValueAnimator rotateAnima = ValueAnimator.ofFloat(1.f, 100.f);
        rotateAnima.setInterpolator(new OvershootInterpolator());
        rotateAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                rFraction = valueAnimator.getAnimatedFraction();
                itemIconSize = (int) (rFraction * iconSize);
                invalidate();
            }
        });

        AnimatorSet closeAnimaSet = new AnimatorSet();
        closeAnimaSet.setDuration(500);
        closeAnimaSet.play(spreadAnima).with(rotateAnima);
        closeAnimaSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                status = STATUS_MENU_CLOSED;
                if (onMenuStatusChangeListener != null)
                    onMenuStatusChangeListener.onMenuClosed();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(aroundAnima).before(closeAnimaSet);
        animatorSet.start();
    }

    /**
     * 获取当前点击的是哪一个菜单按钮 <br/>
     * 中心菜单下标为0，周围菜单从正上方顺时针计数1~5
     * @param x
     * @param y
     * @return
     */
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

    private Drawable convertDrawable(int iconRes) {
        return getResources().getDrawable(iconRes);
    }

    private void resetMainDrawableBounds() {
        openMenuIcon.setBounds(centerX - iconSize / 2, centerY - iconSize / 2,
                centerX + iconSize / 2, centerY + iconSize / 2);
        closeMenuIcon.setBounds(centerX - iconSize / 2, centerY - iconSize / 2,
                centerX + iconSize / 2, centerY + iconSize / 2);
    }

    /**
     * 以 Resource 格式，设置打开/关闭菜单图标
     * @param openRes
     * @param closeRes
     */
    public void setMainIconResource(int openRes, int closeRes) {
        openMenuIcon = convertDrawable(openRes);
        closeMenuIcon = convertDrawable(closeRes);
        resetMainDrawableBounds();
    }

    /**
     * 以 Bitmap 格式，设置打开/关闭菜单图标
     * @param openBitmap
     * @param closeBitmap
     */
    public void setMainIconBitmap(Bitmap openBitmap, Bitmap closeBitmap) {
        openMenuIcon = new BitmapDrawable(getResources(), openBitmap);
        closeMenuIcon = new BitmapDrawable(getResources(), closeBitmap);
        resetMainDrawableBounds();
    }

    /**
     * 以 Drawable 格式，设置打开/关闭菜单图标
     * @param openDrawable
     * @param closeDrawable
     */
    public void setMainIconDrawable(Drawable openDrawable, Drawable closeDrawable) {
        openMenuIcon = openDrawable;
        closeMenuIcon = closeDrawable;
        resetMainDrawableBounds();
    }

    /**
     * 设置一组 Resource 格式的子菜单项图标
     * @param iconResources
     */
    public void setSubIconResources(int[] iconResources) {
        for (int i = 0; i < iconResources.length; i++) {
            iconDrawables[i] = convertDrawable(iconResources[i]);
        }
    }

    /**
     * 设置一组 Bitmap 格式的子菜单项图标
     * @param bitmaps
     */
    public void setSubIconBitmaps(Bitmap[] bitmaps) {
        for (int i = 0; i < bitmaps.length; i++) {
            iconDrawables[i] = new BitmapDrawable(getResources(), bitmaps[i]);
        }
    }

    /**
     * 设置一组 Drawable 格式的子菜单项图标
     * @param drawables
     */
    public void setSubIconDrawables(Drawable[] drawables) {
        iconDrawables = drawables;
    }

    /**
     * 设置一组菜单的颜色，中心主菜单按钮颜色下标为0, 正上方子菜单按钮颜色下标为1，顺时针依次下标累加1
     * @param menuColors
     */
    public void setMenuColors(int[] menuColors) {
        this.menuColors = menuColors;
    }

    public void setOnMenuSelectedListener(OnMenuSelectedListener listener) {
        this.onMenuSelectedListener = listener;
    }

    public void setOnMenuStatusChangeListener(OnMenuStatusChangeListener listener) {
        this.onMenuStatusChangeListener = listener;
    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
