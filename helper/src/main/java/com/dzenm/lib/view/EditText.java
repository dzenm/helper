package com.dzenm.lib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.dzenm.lib.R;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class EditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    private Drawable clearDrawable;

    private boolean isCloseDrawable = false;

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        startAnimation(shakeAnimation(2));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return 平移动画
     */
    public Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    public void setCloseDrawable(boolean closeDrawable) {
        isCloseDrawable = closeDrawable;
        setClearIconVisible(false);
    }

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
     * getCompoundDrawables(left,top,right,bottom)[position]
     *
     * @param context 上下文
     */
    private void init(Context context) {
        clearDrawable = getCompoundDrawables()[2];
        if (clearDrawable == null) {
            clearDrawable = new BitmapDrawable(null, createBitmap(R.drawable.ic_delete, context));
        }
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    /*
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     *
     * 如果DrawableRight有图片显示，并且触摸的位置处于图片所在的范围，清除输入框的内容
     *
     *      getX()                                  触摸点X的位置
     *      getWidth()                              EditText的控件宽度
     *      getPaddingRight()                       EditText右边的内边距
     *      clearDrawable.getIntrinsicWidth()       清除按钮的实际图片宽度
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchAble = event.getX() > (getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchAble) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 判断图标是否可见，true时在EditText右边设置清除图标，false时为空
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = (!isCloseDrawable) ? visible ? clearDrawable : null : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        setClearIconVisible(s.length() > 0);
    }

    public void afterTextChanged(Editable editable) {
    }

    /**
     * 给图标染上当前提示文本的颜色并且转出Bitmap
     *
     * @param resources
     * @param context
     * @return
     */
    private Bitmap createBitmap(int resources, Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, resources);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        return drawableToBitamp(wrappedDrawable);
    }

    /**
     * drawable 转换成 bitmap
     *
     * @param drawable 需要转换的drawable
     * @return bitmap
     */
    private Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
}