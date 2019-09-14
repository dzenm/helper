package com.dzenm.helper.draw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.ColorRes;
import android.view.View;
import android.widget.TextView;

import com.dzenm.helper.R;
import com.dzenm.helper.os.ScreenHelper;

/**
 * @author dzenm
 * @date 2019-07-19 16:08
 */
public class ShapeHelper implements IBackG<Drawable, View> {

    @SuppressLint("StaticFieldLeak")
    private static volatile ShapeHelper sShapeHelper;

    private Context mContext;

    private @DrawableStyle
    int mDrawableStyle = DrawableStyle.NONE;

    // 样式形状, 默认矩形
    private @Shape
    int mShape;

    // Shape的宽度, 不设置为默认宽度
    private int mWidth = -1;

    // Shape的高度, 不设置为默认高度
    private int mHeight = -1;

    // 背景颜色
    private int mColor;

    // 圆角大小(上左)
    private float mTopLeftRadius = 0f;

    // 圆角大小(上右)
    private float mTopRightRadius = 0f;

    // 圆角大小(下右)
    private float mBottomRightRadius = 0f;

    // 圆角大小(下左)
    private float mBottomLeftRadius = 0f;

    // 边框
    private float mStroke;

    // 边框颜色
    private int mStrokeColor;

    // 内边距（依次为左，上，右，下）
    private int[] mBounds;

    // 渐变颜色
    private int[] mGradientColors;

    // 渐变类型(默认为扫描渐变)
    private int mGradientType = GradientDrawable.SWEEP_GRADIENT;

    // 辐射渐变范围半径
    private float mGradientRadius;

    // 渐变方向
    private @Orientation
    String mGradientOrientation = Orientation.TOP_BOTTOM;

    // Selector状态
    private @SelectorState
    int mState;

    // 选中后的背景
    private Drawable mSelectorDrawable;

    // 正常的背景
    private Drawable mNormalDrawable;

    // 点击后的字体颜色
    private int mSelectorTextColor;

    // 正常的字体颜色
    private int mNormalTextColor;

    // Ripple 点击时波纹的颜色
    private int mRippleColor;

    // Ripple 点击时背景的颜色
    private Drawable mRippleDrawable;

    private boolean isCustomizeColor;
    private boolean isCustomizeRadius;
    private boolean isCustomizeStroke;
    private boolean isCustomizeBounds;
    private boolean isCustomizeSelectorTextColor;
    private boolean isCustomizeGradient;

    private ShapeHelper() {
    }

    static ShapeHelper getInstance() {
        if (sShapeHelper == null) synchronized (ShapeHelper.class) {
            if (sShapeHelper == null) sShapeHelper = new ShapeHelper();
        }
        return sShapeHelper;
    }

    ShapeHelper init(Context context) {
        mContext = context;
        return this;
    }

    /**
     * 设置形状
     *
     * @param shape 可选项为圆形和矩形
     * @return ShapeHelper
     */
    public ShapeHelper shape(@Shape int shape) {
        mShape = shape;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 设置宽度和高度
     *
     * @param width  宽度
     * @param height 高度
     * @return ShapeHelper
     */
    public ShapeHelper size(int width, int height) {
        mWidth = dp2px(width);
        mHeight = dp2px(height);
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 设置宽度
     *
     * @param width 宽度
     * @return ShapeHelper
     */
    public ShapeHelper width(int width) {
        size(width, -1);
        return this;
    }

    /**
     * 设置高度
     *
     * @param height 高度
     * @return ShapeHelper
     */
    public ShapeHelper height(int height) {
        size(-1, height);
        return this;
    }

    /**
     * 背景颜色
     *
     * @param colorResId 颜色值, res/color 文件下的资源id
     * @return ShapeHelper
     */
    public ShapeHelper solid(@ColorRes int colorResId) {
        mColor = getColor(colorResId);
        isCustomizeColor = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 背景颜色
     *
     * @param color 字符串的16进制颜色值
     * @return ShapeHelper
     */
    public ShapeHelper solid(String color) {
        mColor = Color.parseColor(color);
        isCustomizeColor = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        ;
        return this;
    }

    /**
     * 圆角弧度
     *
     * @param radius 圆角弧度值(包含四个角)
     * @return ShapeHelper
     */
    public ShapeHelper radius(float radius) {
        radius(radius, radius, radius, radius);
        return this;
    }

    /**
     * 圆角弧度
     *
     * @param tl 上左圆角弧度
     * @return ShapeHelper
     */
    public ShapeHelper radiusTL(float tl) {
        mTopLeftRadius = tl;
        isCustomizeRadius = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 圆角弧度
     *
     * @param tr 上右圆角弧度
     * @return ShapeHelper
     */
    public ShapeHelper radiusTR(float tr) {
        mTopRightRadius = tr;
        isCustomizeRadius = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 圆角弧度
     *
     * @param br 下右圆角弧度
     * @return ShapeHelper
     */
    public ShapeHelper radiusBR(float br) {
        mBottomRightRadius = br;
        isCustomizeRadius = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 圆角弧度
     *
     * @param bl 下左圆角弧度
     * @return ShapeHelper
     */
    public ShapeHelper radiusBL(float bl) {
        mBottomLeftRadius = bl;
        isCustomizeRadius = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 圆角弧度
     *
     * @param tl 上左圆角弧度
     * @param tr 右上圆角弧度
     * @param br 下右圆角弧度
     * @param bl 下左圆角弧度
     * @return ShapeHelper
     */
    public ShapeHelper radius(float tl, float tr, float br, float bl) {
        radius(new float[]{tl, tr, br, bl});
        return this;
    }

    /**
     * 圆角弧度
     *
     * @param radii 圆角弧度值(float数组大小为四个值，上左，上右，下右，下左)
     * @return ShapeHelper
     */
    public ShapeHelper radius(float[] radii) {
        mTopLeftRadius = radii[0];
        mTopRightRadius = radii[1];
        mBottomRightRadius = radii[2];
        mBottomLeftRadius = radii[3];
        isCustomizeRadius = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 边框大小
     *
     * @param stroke 边框值
     * @param color  边框颜色
     * @return ShapeHelper
     */
    public ShapeHelper stroke(float stroke, @ColorRes int color) {
        mStroke = stroke;
        mStrokeColor = getColor(color);
        isCustomizeStroke = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 内边距大小
     *
     * @param padding 内边距值(包含四边)
     * @return ShapeHelper
     */
    public ShapeHelper bound(int padding) {
        bound(new int[]{padding, padding, padding, padding});
        return this;
    }

    /**
     * 内边距大小
     *
     * @param l 左边
     * @param t 上边
     * @param r 右边
     * @param b 下边
     * @return ShapeHelper
     */
    public ShapeHelper bound(int l, int t, int r, int b) {
        bound(new int[]{l, t, r, b});
        return this;
    }

    /**
     * 内边距大小
     *
     * @param padding 内边距值(int数组为四个值，左，上，右，下)
     * @return ShapeHelper
     */
    public ShapeHelper bound(int[] padding) {
        mBounds = padding;
        isCustomizeBounds = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 线性渐变方向
     *
     * @param orientation 线性渐变方向
     * @return ShapeHelper
     */
    public ShapeHelper orientation(@Orientation String orientation) {
        mGradientOrientation = orientation;
        mGradientType = GradientDrawable.LINEAR_GRADIENT;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 辐射渐变半径
     *
     * @param gradientRadius 渐变半径
     * @return ShapeHelper
     */
    public ShapeHelper radialRadius(int gradientRadius) {
        mGradientRadius = gradientRadius;
        mGradientType = GradientDrawable.RADIAL_GRADIENT;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 渐变颜色
     * 线性类型: 线性渐变(GradientDrawable.LINEAR_GRADIENT)
     *
     * @param colorsResId 渐变颜色组, 必须大于2个颜色
     * @return ShapeHelper
     */
    public ShapeHelper gradient(@ColorRes int... colorsResId) {
        if (colorsResId.length > 1) {
            mGradientColors = new int[colorsResId.length];
            for (int i = 0; i < colorsResId.length; i++) {
                mGradientColors[i] = getColor(colorsResId[i]);
            }
        }
        isCustomizeGradient = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * 渐变颜色
     * 线性类型: 线性渐变(GradientDrawable.LINEAR_GRADIENT)
     *
     * @param colors 渐变颜色组, 必须大于2个颜色
     * @return ShapeHelper
     */
    public ShapeHelper gradient(String... colors) {
        if (colors.length > 1) {
            mGradientColors = new int[colors.length];
            for (int i = 0; i < colors.length; i++) {
                mGradientColors[i] = Color.parseColor(colors[i]);
            }
        }
        isCustomizeGradient = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * Selector按压设置
     *
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(String pressedColor) {
        pressed("#FFFFFFFF", pressedColor);
        return this;
    }

    /**
     * Selector按压设置
     *
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(@ColorRes int pressedColor) {
        pressed(android.R.color.transparent, pressedColor);
        return this;
    }

    /**
     * Selector按压设置
     *
     * @param normalColor  正常背景颜色
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(String normalColor, String pressedColor) {
        float topLeftRadius = mTopLeftRadius;
        float topRightRadius = mTopRightRadius;
        float bottomRightRadius = mBottomRightRadius;
        float bottomLeftRadius = mBottomLeftRadius;
        pressed(ShapeHelper.getInstance()
                        .solid(normalColor)
                        .radius(topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius)
                        .build(),
                ShapeHelper.getInstance()
                        .solid(pressedColor)
                        .radius(topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius)
                        .build());
        return this;
    }

    /**
     * Selector按压设置
     *
     * @param normalColor  正常背景颜色
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(@ColorRes int normalColor, @ColorRes int pressedColor) {
        float topLeftRadius = mTopLeftRadius;
        float topRightRadius = mTopRightRadius;
        float bottomRightRadius = mBottomRightRadius;
        float bottomLeftRadius = mBottomLeftRadius;
        pressed(ShapeHelper.getInstance()
                        .solid(normalColor)
                        .radius(topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius)
                        .build(),
                ShapeHelper.getInstance()
                        .solid(pressedColor)
                        .radius(topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius)
                        .build());
        return this;
    }

    /**
     * Selector按压设置
     *
     * @param normalDrawable  正常的背景
     * @param pressedDrawable 按压后的背景
     * @return ShapeHelper
     */
    public ShapeHelper pressed(Drawable normalDrawable, Drawable pressedDrawable) {
        state(SelectorState.STATE_PRESSED, normalDrawable, pressedDrawable);
        return this;
    }

    /**
     * Selector状态设置
     *
     * @param state           Selector状态
     * @param normalDrawable  正常的背景
     * @param pressedDrawable 按压后的背景
     * @return ShapeHelper
     */
    public ShapeHelper state(@SelectorState int state, Drawable normalDrawable, Drawable pressedDrawable) {
        mState = state;
        mNormalDrawable = normalDrawable;
        mSelectorDrawable = pressedDrawable;
        mDrawableStyle = DrawableStyle.STATE_LIST;
        return this;
    }

    /**
     * 按压后的文本颜色
     *
     * @param normalColorResId   正常的文本颜色
     * @param selectorColorResId 按压后的文本颜色
     * @return ShapeHelper
     */
    public ShapeHelper textColor(int normalColorResId, int selectorColorResId) {
        mNormalTextColor = getColor(normalColorResId);
        mSelectorTextColor = getColor(selectorColorResId);
        mDrawableStyle = DrawableStyle.STATE_LIST;
        isCustomizeSelectorTextColor = true;
        return this;
    }

    /**
     * 按压后的文本颜色
     *
     * @param normalColor   正常的文本颜色
     * @param selectorColor 按压后的文本颜色
     * @return ShapeHelper
     */
    public ShapeHelper textColor(String normalColor, String selectorColor) {
        mNormalTextColor = Color.parseColor(normalColor);
        mSelectorTextColor = Color.parseColor(selectorColor);
        mDrawableStyle = DrawableStyle.STATE_LIST;
        isCustomizeSelectorTextColor = true;
        return this;
    }

    /**
     * 波纹点击效果, 白色背景, 按压波纹效果为淡灰色
     *
     * @return ShapeHelper
     */
    public ShapeHelper ripple() {
        ripple(android.R.color.white, R.color.colorTranslucentLightGray);
        return this;
    }

    /**
     * 波纹点击效果, 按压波纹效果为淡灰色
     *
     * @param normalColor 正常显示View的颜色
     * @return ShapeHelper
     */
    public ShapeHelper ripple(@ColorRes int normalColor) {
        ripple(normalColor, R.color.colorHint);
        return this;
    }

    /**
     * 波纹点击效果, 按压波纹效果为淡灰色
     *
     * @param normalColor 正常显示View的颜色
     * @return ShapeHelper
     */
    public ShapeHelper ripple(String normalColor) {
        ripple(normalColor, "#E0E0E0");
        return this;
    }

    /**
     * 波纹点击效果
     *
     * @param normalColor  正常显示View的颜色
     * @param pressedColor 点击View显示的波纹的颜色
     * @return ShapeHelper
     */
    public ShapeHelper ripple(String normalColor, String pressedColor) {
        mRippleColor = Color.parseColor(pressedColor);
        mRippleDrawable = ShapeHelper.getInstance()
                .solid(normalColor)
                .radius(mTopLeftRadius, mTopRightRadius, mBottomRightRadius, mBottomLeftRadius)
                .build();
        mDrawableStyle = DrawableStyle.RIPPLE;
        return this;
    }

    /**
     * 波纹点击效果
     *
     * @param normalColor  正常显示View的颜色
     * @param pressedColor 点击View显示的波纹的颜色
     * @return ShapeHelper
     */
    public ShapeHelper ripple(@ColorRes int normalColor, @ColorRes int pressedColor) {
        mRippleColor = pressedColor;
        mRippleDrawable = ShapeHelper.getInstance()
                .solid(normalColor)
                .radius(mTopLeftRadius, mTopRightRadius, mBottomRightRadius, mBottomLeftRadius)
                .build();
        mDrawableStyle = DrawableStyle.RIPPLE;
        return this;
    }

    @Override
    public void into(View view) {
        if (isCustomizeSelectorTextColor) {
            // TextView等view默认没有点击事件，所以针对view初始化点击事件
            int[] colors = new int[]{mSelectorTextColor, mNormalTextColor};
            int[][] states = new int[2][];
            states[0] = new int[]{mState};
            states[1] = new int[]{-mState};
            ((TextView) view).setTextColor(new ColorStateList(states, colors));
        }
        view.setBackground(build());
    }

    @Override
    public Drawable build() {
        if (mDrawableStyle == DrawableStyle.GRADIENT) {
            return createShape();
        } else if (mDrawableStyle == DrawableStyle.STATE_LIST) {
            return createSelector();
        } else if (mDrawableStyle == DrawableStyle.RIPPLE) {
            return createRipple();
        }
        return null;
    }

    private void reset() {
        mColor = 0;
        mTopLeftRadius = 0f;
        mTopRightRadius = 0f;
        mBottomRightRadius = 0f;
        mBottomLeftRadius = 0f;
        mStroke = 0;
        mStrokeColor = 0;
        mBounds = new int[]{0, 0, 0, 0};
        mGradientType = GradientDrawable.SWEEP_GRADIENT;
        mGradientRadius = 0f;
        mGradientOrientation = Orientation.TOP_BOTTOM;
        isCustomizeColor = false;
        isCustomizeRadius = false;
        isCustomizeStroke = false;
        isCustomizeBounds = false;
        isCustomizeGradient = false;
        isCustomizeSelectorTextColor = false;
        mDrawableStyle = DrawableStyle.NONE;
    }

    /**
     * 创建Shape Drawable
     *
     * @return Shape Drawable
     */
    private GradientDrawable createShape() {
        GradientDrawable drawable = new GradientDrawable();
        // 设置形状
        drawable.setShape(mShape == Shape.OVAL ? GradientDrawable.OVAL : GradientDrawable.RECTANGLE);

        // 设置背景颜色
        if (isCustomizeColor) drawable.setColor(mColor);

        // 设置圆角
        if (isCustomizeRadius) {
            float[] radII = new float[]{
                    dp2px(mTopLeftRadius), dp2px(mTopLeftRadius),
                    dp2px(mTopRightRadius), dp2px(mTopRightRadius),
                    dp2px(mBottomRightRadius), dp2px(mBottomRightRadius),
                    dp2px(mBottomLeftRadius), dp2px(mBottomLeftRadius)};
            drawable.setCornerRadii(radII);
        }

        // 设置边框
        if (isCustomizeStroke) drawable.setStroke(dp2px(mStroke), mStrokeColor);

        // 设置内边距
        if (isCustomizeBounds)
            drawable.setBounds(
                    dp2px(mBounds[0]), dp2px(mBounds[1]),
                    dp2px(mBounds[2]), dp2px(mBounds[3]));

        if (isCustomizeGradient) {
            if (mGradientType == GradientDrawable.LINEAR_GRADIENT) {
                drawable.setOrientation(createGradientOrientation());   // 设置线性渐变方向
            } else if (mGradientType == GradientDrawable.RADIAL_GRADIENT) {
                drawable.setGradientRadius(dp2px(mGradientRadius));     // 设置辐射渐变的辐射渐变范围半径
            }
            drawable.setGradientType(mGradientType);
            drawable.setColors(mGradientColors);
        } else {
            drawable.setUseLevel(true);                                 // 扫描渐变和辐射渐变添加这个属性会不起作用
        }
        drawable.setSize(mWidth, mHeight);
        reset();
        return drawable;
    }

    /**
     * 创建Selector Drawable
     *
     * @return Selector Drawable
     */
    private StateListDrawable createSelector() {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{mState}, mSelectorDrawable);    // 状态为true的背景
        drawable.addState(new int[]{-mState}, mNormalDrawable);     // 状态为false的背景
        reset();
        return drawable;
    }

    /**
     * 创建Ripple Drawable
     *
     * @return Ripple Drawable
     */
    private RippleDrawable createRipple() {
        RippleDrawable rd = new RippleDrawable(mContext.getResources().getColorStateList(mRippleColor),
                mRippleDrawable, null);
        reset();
        return rd;
    }

    /**
     * 创建线性方向渐变
     *
     * @return 线性方向渐变
     */
    private GradientDrawable.Orientation createGradientOrientation() {
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        switch (mGradientOrientation) {
            case Orientation.TOP_BOTTOM:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case Orientation.TR_BL:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case Orientation.RIGHT_LEFT:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case Orientation.BR_TL:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case Orientation.BOTTOM_TOP:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case Orientation.BL_TR:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case Orientation.LEFT_RIGHT:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case Orientation.TL_BR:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
        }
        return orientation;
    }


    private int dp2px(float value) {
        return ScreenHelper.dp2px(value);
    }

    private int getColor(int id) {
        return mContext.getResources().getColor(id);
    }
}
