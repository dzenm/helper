package com.dzenm.helper.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.R;
import com.dzenm.helper.dialog.PreviewDialog;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;

import java.util.List;

/**
 * module使用library中的自定义view没提示的问题并不能用的问题
 * 编译版本的问题，module的编译版本（即你的主app）必须和library的一致才行
 * 修改module的gradle文件的compileSdkVersion和targetSdkVersion
 * <pre>
 *  预览图片的使用
 *  <com.dzenm.helper.view.PhotoLayout
 *       android:id="@+id/pl_preview"
 *       android:layout_width="match_parent"
 *       android:layout_height="wrap_content"
 *       android:layout_margin="@dimen/margin" />
 *
 * plPreview = findViewById(R.id.pl_preview);
 * plPreview.setOnItemClickListener(new PhotoLayout.OnItemClickListener() {
 *             @Override
 *             public void onItemClick(View view, int position) {
 *                 ToastHelper.show("点击的是第: " + position);
 *             }
 *         });
 * plPreview.setPreview(true);
 * plPreview.loader(new MyImageLoader());
 * plPreview.load(Arrays.asList(url));
 *
 *  添加图片的使用
 *  plAdd = findViewById(R.id.pl_add);
 *  plAdd.setOnPhotoListener(this);
 *  添加图片的回调
 *  PhotoSelector.getInstance()
 *       .with(DrawableActivity.this)
 *       .setOnSelectPhotoListener(new PhotoSelector.OnSelectPhotoListener() {
 *           @Override
 *           public boolean onGallery(PhotoSelector helper, String filePath) {
 *               Bitmap bitmap = FileHelper.getInstance().getPhoto(filePath);
 *               layout.load(bitmap);
 *               layout.loader(new MyImageLoader());
 *               return false;
 *           }
 *
 *           @Override
 *           public void onError(String msg) {
 *               super.onError(msg);
 *           }
 *       }).selectGallery();
 *
 * public class MyImageLoader implements ImageLoader {
 *     @Override
 *     public void onLoader(RatioImageView imageView, Object object) {
 *         Glide.with(imageView.getContext()).load(object).into(imageView);
 *     }
 * }
 * </pre>
 *
 * @author dzenm
 * @date 2019-09-04 08:27
 */
public class PhotoLayout extends GridLayout {

    private static final String TAG = PhotoLayout.class.getSimpleName() + "|";

    private static final int DEFAULT_TOTAL_NUMBER = 9, DEFAULT_COLUMN_NUMBER = 3;

    /**
     * 容纳ImageView的总数, 默认为 {@link #DEFAULT_TOTAL_NUMBER}, {@link #setTotalNumber(int)}
     * Grid的列数, 默认为 {@link #DEFAULT_COLUMN_NUMBER}, {@link #setColumnNumber(int)}
     */
    private int mTotalNumber, mColumnNumber;

    /**
     * 提示添加的View
     */
    private RatioImageView mEmptyRatioImageView;

    /**
     * 图片的宽高, 以正方形显示
     */
    private int mImageWidth, mImageHeight;

    /**
     * 当前应该显示的ImageView的位置, 随着ImageView的增加而增加, 随着ImageView的移除而减少
     */
    private int mCurrentPosition = 0;

    /**
     * ImageView的边距, 默认为2dp, {@link #setMargin(int)}
     * 删除按钮的大小
     */
    private int mMargin, mDeleteSize;

    /**
     * 是否是预览图片的形式
     */
    private boolean isPreview;

    /**
     * 默认添加图标, 删除图标
     */
    private int mDefaultIcon, mDeleteIcon;

    private OnLoadPhotoListener mOnLoadPhotoListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private ImageLoader mImageLoader;

    public void setTotalNumber(int totalNumber) {
        mTotalNumber = totalNumber;
    }

    public void setColumnNumber(int columnNumber) {
        mColumnNumber = columnNumber;
        invalidateGridView();
    }

    public void setMargin(int margin) {
        mMargin = OsHelper.dp2px(margin);
        invalidateGridView();
    }

    public void setOnPhotoListener(OnLoadPhotoListener listener) {
        mOnLoadPhotoListener = listener;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
        invalidateGridView();
    }

    /**
     * @param onItemClickListener Item点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 图片加载的方式 {@link ImageLoader}, 使用第三方图片加载框架, 如Glide
     *
     * @param imageLoader 图片加载的接口
     */
    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public void load(List images) {
        setTotalNumber(images.size());
        for (Object image : images) load(image);
    }

    public void load(final Object image) {
        post(new Runnable() {
            @Override
            public void run() {
                newRatioImageView(image);
            }
        });
    }

    public void remove(View view) {
        removeRatioImageView(view);
    }

    /**
     * 创建RatioImageView
     *
     * @param image 图片
     */
    private void newRatioImageView(Object image) {
        if (mCurrentPosition < mTotalNumber) {
            RatioImageView imageView = addRatioImageView(image, mCurrentPosition);
            mImageLoader.onLoader(imageView, image);
        }
    }

    public PhotoLayout(Context context) {
        this(context, null);
    }

    public PhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PhotoLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray t = context
                .obtainStyledAttributes(attrs, R.styleable.PhotoLayout, defStyleAttr, defStyleRes);

        mTotalNumber = t.getInteger(R.styleable.PhotoLayout_totalNumber, DEFAULT_TOTAL_NUMBER);
        mColumnNumber = t.getInteger(R.styleable.PhotoLayout_columnNumber, DEFAULT_COLUMN_NUMBER);
        mMargin = (int) t.getDimension(R.styleable.PhotoLayout_margin, OsHelper.dp2px(2));
        isPreview = t.getBoolean(R.styleable.PhotoLayout_isPreview, false);
        mDeleteSize = (int) t.getDimension(R.styleable.PhotoLayout_deleteSize, OsHelper.dp2px(16));
        mDeleteIcon = t.getResourceId(R.styleable.PhotoLayout_deleteIcon, R.drawable.ic_delete_picture);
        mDefaultIcon = t.getResourceId(R.styleable.PhotoLayout_defaultIcon, R.drawable.ic_add);

        t.recycle();

        // 设置子View的动画
        setLayoutTransition(LayoutTransitionHelper.scaleViewAnimator(this));
        invalidateGridView();
    }

    /**
     * 初始化View
     */
    private void initializeView() {
        if (!isPreview && mEmptyRatioImageView != null) {
            removeView(mEmptyRatioImageView);
            mEmptyRatioImageView = null;
        }
        setColumnCount(mColumnNumber);
        int rowCount = mCurrentPosition + 1 / mColumnNumber;
        setRowCount(mCurrentPosition + 1 == mTotalNumber ? rowCount : rowCount + 1);
        mImageWidth = mImageHeight = (getWidth() - 6 * mMargin) / getColumnCount();
        if (!isPreview && mEmptyRatioImageView == null && getChildCount() == 0) {
            mEmptyRatioImageView = createEmptyRatioImageView(mImageWidth, mImageHeight, mMargin);
            addView(mEmptyRatioImageView);
        }
    }

    /**
     * 重新绘制
     */
    private void invalidateGridView() {
        post(new Runnable() {
            @Override
            public void run() {
                initializeView();
            }
        });
    }

    /**
     * 创建一个空的ImageView
     *
     * @return ImageView
     */
    private RatioImageView createEmptyRatioImageView(int width, int height, int margin) {
        RatioImageView imageView = new RatioImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(layoutParams);
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        imageView.setImageResource(mDefaultIcon);
        imageView.setScaleType(RatioImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnLoadPhotoListener != null) mOnLoadPhotoListener.onLoad(PhotoLayout.this);
            }
        });
        DrawableHelper.ripple(R.color.colorLightGray, R.color.colorHint).into(imageView);
        return imageView;
    }

    /**
     * 为ImageView设置图片
     *
     * @return ImageView
     */
    private RatioImageView addRatioImageView(Object object, int position) {
        // 预览图片时, 隐藏提示View
        if (isPreview && mEmptyRatioImageView != null && getChildCount() >= 1) {
            mEmptyRatioImageView.setVisibility(GONE);
        }
        RatioImageView imageView = createRatioImageView(object, position);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mImageWidth, mImageHeight);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
        relativeLayout.setLayoutParams(layoutParams);
        // 设置动画起始点
        relativeLayout.setPivotX(0);
        relativeLayout.setPivotY(0);
        relativeLayout.addView(imageView);
        if (!isPreview) relativeLayout.addView(createDeleteView(relativeLayout, position));

        // 设置View为实际mCurrentCount所在的位置, 防止图片混乱
        Logger.d(TAG + "当前ImageView所在的位置: " + position);
        addView(relativeLayout, position);
        mCurrentPosition++;

        setEmptyRatioImageViewVisible(false);
        return imageView;
    }

    /**
     * 创建显示图片的ImageView
     * * @return ImageView
     */
    private RatioImageView createRatioImageView(final Object object, final int currentPosition) {
        final RatioImageView imageView = new RatioImageView(getContext());
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(mImageWidth, mImageHeight));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setTag(R.id.photo_layout_image_id, currentPosition);
        // 设置图片点击事件, 单击预览图片
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d(TAG + "click position is " + currentPosition);
                if (mOnItemClickListener == null) {
                    previewImage(object);
                } else {
                    if (!mOnItemClickListener.onItemClick(imageView, currentPosition)) {
                        previewImage(object);
                    }
                }
            }
        });
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Logger.d(TAG + "long click position is " + currentPosition);
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(imageView, currentPosition);
                }
                return false;
            }
        });
        return imageView;
    }

    /**
     * 预览图片
     *
     * @param object 预览的图片
     */
    private void previewImage(Object object) {
        PreviewDialog.newInstance((AppCompatActivity) getContext())
                .setImageLoader(mImageLoader)
                .load(object)
                .show();
    }

    /**
     * 创建删除按钮
     * * @return ImageView
     */
    private RatioImageView createDeleteView(final RelativeLayout relativeLayout, final int currentPosition) {
        RatioImageView imageView = new RatioImageView(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mDeleteSize, mDeleteSize);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(mDeleteIcon);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // 设置点击事件, 单击删除
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d(TAG + "click position is " + currentPosition);
                remove(relativeLayout);
            }
        });
        return imageView;
    }

    /**
     * @param view 移除的View
     */
    private void removeRatioImageView(View view) {
        removeView(view);
        mCurrentPosition--;
        setEmptyRatioImageViewVisible(true);
    }

    /**
     * 设置提示View是否可见, 添加的图片数量等于 {@link #mTotalNumber} 时隐藏
     * 添加完图片时如果删除一张图片时应显示
     *
     * @param visible 是否可见
     */
    private void setEmptyRatioImageViewVisible(boolean visible) {
        int count = visible ? getChildCount() : mCurrentPosition;
        if (count == mTotalNumber && !isPreview) {
            mEmptyRatioImageView.setVisibility(visible ? VISIBLE : GONE);
        }
    }

    public interface OnLoadPhotoListener {
        void onLoad(PhotoLayout layout);
    }

    public interface OnItemClickListener {

        boolean onItemClick(RatioImageView imageView, int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(RatioImageView imageView, int position);
    }
}
