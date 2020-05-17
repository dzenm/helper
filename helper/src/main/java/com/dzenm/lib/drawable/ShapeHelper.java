package com.dzenm.lib.drawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntDef;

import com.dzenm.lib.R;
import com.dzenm.lib.os.OsHelper;
import com.dzenm.lib.os.ThemeHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-07-19 16:08
 */
public class ShapeHelper {

    @SuppressLint("StaticFieldLeak")
    private static volatile ShapeHelper sShapeHelper;

    private Context mContext;

    @IntDef({DrawableStyle.GRADIENT, DrawableStyle.STATE_LIST, DrawableStyle.RIPPLE,})
    @Retention(RetentionPolicy.SOURCE)
    @interface DrawableStyle {

        /**
         * null
         */
        int NONE = 0;

        /**
         * {@link android.graphics.drawable.GradientDrawable}
         */
        int GRADIENT = 1;

        /**
         * {@link android.graphics.drawable.StateListDrawable}
         */
        int STATE_LIST = 2;

        /**
         * {@link android.graphics.drawable.RippleDrawable}
         */
        int RIPPLE = 3;
    }

    private @DrawableStyle
    int mDrawableStyle = DrawableStyle.NONE;

    private int mShape;                         // Drawable形状, 默认矩形
    private int mWidth = -1, mHeight = -1;      // Shape的宽度和高度, 不设置为默认宽高
    private int mAlpha = 0xFF;                  // 设置颜色的透明度
    private int mColor;                         // 背景颜色

    // 圆角大小(上左, 上右, 下右, 下右, 下左)
    private float mTopLeftRadius = 0f, mTopRightRadius = 0f, mBottomRightRadius = 0f, mBottomLeftRadius = 0f;
    private float mStroke;                      // 边框
    private int mStrokeColor;                   // 边框颜色
    private int[] mBounds;                      // 内边距（依次为左，上，右，下）
    private int[] mGradientColors;              // 渐变颜色

    // 渐变类型(默认为扫描渐变)
    private int mGradientType = GradientDrawable.SWEEP_GRADIENT;
    private float mGradientRadius;              // 辐射渐变范围半径

    // 渐变方向
    private GradientDrawable.Orientation mGradientOrientation = GradientDrawable.Orientation.TOP_BOTTOM;

    @IntDef({SelectorState.STATE_PRESSED, SelectorState.STATE_ENABLED, SelectorState.STATE_SELECTED,
            SelectorState.STATE_CHECKED, SelectorState.STATE_CHECKABLE, SelectorState.STATE_FOCUSED,
            SelectorState.STATE_WINDOW_FOCUSED, SelectorState.STATE_ACTIVATED, SelectorState.STATE_HOVERED})
    @Retention(RetentionPolicy.SOURCE)
    @interface SelectorState {

        // 按压状态
        int STATE_PRESSED = android.R.attr.state_pressed;

        // 可用状态(触摸或点击事件)
        int STATE_ENABLED = android.R.attr.state_enabled;

        // 选中状态
        int STATE_SELECTED = android.R.attr.state_selected;

        // 勾选状态(用于CheckBox和RadioButton)
        int STATE_CHECKED = android.R.attr.state_checked;

        // 勾选可用状态
        int STATE_CHECKABLE = android.R.attr.state_checkable;

        // 焦点获得状态
        int STATE_FOCUSED = android.R.attr.state_focused;

        // 当前窗口焦点获得状态(下拉通知栏或弹出对话框)
        int STATE_WINDOW_FOCUSED = android.R.attr.state_window_focused;

        // 是否激活状态(API 11以上支持, 可用setActivated()设置)
        int STATE_ACTIVATED = android.R.attr.state_activated;

        // 鼠标滑动状态(API 14以上才支持)
        int STATE_HOVERED = android.R.attr.state_hovered;
    }

    private @SelectorState
    int mState;                                 // Selector状态
    private Drawable mSelectorDrawable;         // 选中后的背景
    private Drawable mNormalDrawable;           // 正常的背景
    private int mSelectorTextColor;             // 点击后的字体颜色
    private int mNormalTextColor;               // 正常的字体颜色
    private int mRippleColor;                   // Ripple 点击时波纹的颜色
    private Drawable mRippleDrawable;           // Ripple 点击时背景的颜色

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

    public ShapeHelper init(Context context) {
        mContext = context;
        return this;
    }

    /**
     * @param shape 形状 {@link GradientDrawable Shape}
     * @return ShapeHelper
     */
    public ShapeHelper shape(int shape) {
        mShape = shape;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param width  ShapeDrawable 宽度
     * @param height ShapeDrawable 高度
     * @return ShapeHelper
     */
    public ShapeHelper size(int width, int height) {
        mWidth = dp2px(width);
        mHeight = dp2px(height);
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param width ShapeDrawable 宽度
     * @return ShapeHelper
     */
    public ShapeHelper width(int width) {
        size(width, -1);
        return this;
    }

    /**
     * @param height ShapeDrawable 高度
     * @return ShapeHelper
     */
    public ShapeHelper height(int height) {
        size(-1, height);
        return this;
    }

    /**
     * @param colorResId 背景颜色, 使用 res/color 文件下的资源id
     * @return ShapeHelper
     */
    public ShapeHelper solid(int colorResId) {
        mColor = getColor(colorResId);
        isCustomizeColor = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param alpha 背景颜色, 使用 res/color 文件下的资源id
     * @return ShapeHelper
     */
    public ShapeHelper alpha(int alpha) {
        mAlpha = alpha;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param color 背景颜色, 字符串的16进制颜色值
     * @return ShapeHelper
     */
    public ShapeHelper solid(String color) {
        mColor = Color.parseColor(color);
        isCustomizeColor = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param radius 圆角弧度(包含上左, 上右, 下右, 下左)
     * @return ShapeHelper
     */
    public ShapeHelper radius(float radius) {
        radius(new float[]{radius, radius, radius, radius});
        return this;
    }

    /**
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
     * @param radii 圆角弧度(float数组分别为上左，上右，下右，下左)
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
     * @param stroke 边框宽度
     * @param color  边框颜色
     * @return ShapeHelper
     */
    public ShapeHelper stroke(float stroke, int color) {
        mStroke = stroke;
        mStrokeColor = getColor(color);
        isCustomizeStroke = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param padding 内边距大小(包含四边)
     * @return ShapeHelper
     */
    public ShapeHelper bound(int padding) {
        bound(new int[]{padding, padding, padding, padding});
        return this;
    }

    /**
     * @param padding 内边距大小(int数组分别为，左，上，右，下)
     * @return ShapeHelper
     */
    public ShapeHelper bound(int[] padding) {
        mBounds = padding;
        isCustomizeBounds = true;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param orientation 渐变方向
     * @return ShapeHelper
     * @see GradientDrawable.Orientation
     */
    public ShapeHelper orientation(GradientDrawable.Orientation orientation) {
        mGradientOrientation = orientation;
        mGradientType = GradientDrawable.LINEAR_GRADIENT;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param gradientRadius 辐射渐变半径
     * @return ShapeHelper
     */
    public ShapeHelper radialRadius(int gradientRadius) {
        mGradientRadius = gradientRadius;
        mGradientType = GradientDrawable.RADIAL_GRADIENT;
        mDrawableStyle = DrawableStyle.GRADIENT;
        return this;
    }

    /**
     * @param colorsResId 渐变颜色组, 必须大于2个颜色, 线性类型: 线性渐变 {@link GradientDrawable#LINEAR_GRADIENT}
     * @return ShapeHelper
     */
    public ShapeHelper gradient(int... colorsResId) {
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
     * @param colors 渐变颜色组, 必须大于2个颜色, 线性类型: 线性渐变 {@link GradientDrawable#LINEAR_GRADIENT}
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
     * @param pressedColor 触摸屏幕时的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(String pressedColor) {
        pressed("#FFFFFFFF", pressedColor);
        return this;
    }

    /**
     * @param pressedColor 触摸屏幕时的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(int pressedColor) {
        pressed(android.R.color.transparent, pressedColor);
        return this;
    }

    /**
     * @param normalColor  正常背景颜色
     * @param pressedColor 触摸屏幕时的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(String normalColor, String pressedColor) {
        float topLeftRadius = mTopLeftRadius;
        float topRightRadius = mTopRightRadius;
        float bottomRightRadius = mBottomRightRadius;
        float bottomLeftRadius = mBottomLeftRadius;
        pressed(solid(normalColor)
                        .radius(new float[]{topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius})
                        .build(),
                solid(pressedColor)
                        .radius(new float[]{topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius})
                        .build());
        return this;
    }

    /**
     * @param normalColor  正常背景颜色
     * @param pressedColor 触摸屏幕时的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(int normalColor, int pressedColor) {
        float topLeftRadius = mTopLeftRadius;
        float topRightRadius = mTopRightRadius;
        float bottomRightRadius = mBottomRightRadius;
        float bottomLeftRadius = mBottomLeftRadius;
        pressed(ShapeHelper.getInstance()
                        .solid(normalColor)
                        .radius(new float[]{topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius})
                        .build(),
                ShapeHelper.getInstance()
                        .solid(pressedColor)
                        .radius(new float[]{topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius})
                        .build());
        return this;
    }

    /**
     * @param normalDrawable  正常背景颜色
     * @param pressedDrawable 触摸屏幕时的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper pressed(Drawable normalDrawable, Drawable pressedDrawable) {
        state(SelectorState.STATE_PRESSED, normalDrawable, pressedDrawable);
        return this;
    }

    /**
     * @param state           Selector状态
     * @param normalDrawable  正常的背景
     * @param pressedDrawable 触摸屏幕时的背景颜色
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
     * @param normalColorResId   正常的文本颜色
     * @param selectorColorResId 触摸屏幕时的文本颜色
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
     * @param normalColor   正常的文本颜色
     * @param selectorColor 触摸屏幕时的文本颜色
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
    public ShapeHelper ripple(int normalColor) {
        ripple(normalColor, R.color.hintColor);
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
     * @param pressedColor 触摸屏幕时的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper ripple(String normalColor, String pressedColor) {
        mRippleColor = Color.parseColor(pressedColor);
        mRippleDrawable = ShapeHelper.getInstance()
                .solid(normalColor)
                .radius(new float[]{mTopLeftRadius, mTopRightRadius, mBottomRightRadius, mBottomLeftRadius})
                .build();
        mDrawableStyle = DrawableStyle.RIPPLE;
        return this;
    }

    /**
     * 波纹点击效果
     *
     * @param normalColor  正常显示View的颜色
     * @param pressedColor 触摸屏幕时的背景颜色
     * @return ShapeHelper
     */
    public ShapeHelper ripple(int normalColor, int pressedColor) {
        mRippleColor = pressedColor;
        mRippleDrawable = ShapeHelper.getInstance()
                .solid(normalColor)
                .radius(new float[]{mTopLeftRadius, mTopRightRadius, mBottomRightRadius, mBottomLeftRadius})
                .build();
        mDrawableStyle = DrawableStyle.RIPPLE;
        return this;
    }

    /**
     * @param view 需要设置背景的View
     */
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

    /**
     * @return 创建一个新的Drawable
     */
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
        mGradientOrientation = GradientDrawable.Orientation.TOP_BOTTOM;
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
        drawable.setShape(mShape);

        // 设置背景颜色
        if (isCustomizeColor) drawable.setColor(mColor);

        drawable.setAlpha(mAlpha);

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
            drawable.setBounds(dp2px(mBounds[0]), dp2px(mBounds[1]), dp2px(mBounds[2]), dp2px(mBounds[3]));

        if (isCustomizeGradient) {
            if (mGradientType == GradientDrawable.LINEAR_GRADIENT) {
                drawable.setOrientation(mGradientOrientation);   // 设置线性渐变方向
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
        RippleDrawable rd = new RippleDrawable(ThemeHelper.getColorStateList(mContext, mRippleColor),
                mRippleDrawable, null);
        reset();
        return rd;
    }

    private int dp2px(float value) {
        return OsHelper.dp2px(value);
    }

    private int getColor(int id) {
        return ThemeHelper.getColor(mContext, id);
    }

}
