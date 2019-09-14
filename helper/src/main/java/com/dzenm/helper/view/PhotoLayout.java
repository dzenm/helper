package com.dzenm.helper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.BackGHelper;
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
 *                 Toa.show("点击的是第: " + position);
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
 *  PhotoHelper.getInstance()
 *       .with(DrawableActivity.this)
 *       .setOnSelectPhotoListener(new PhotoHelper.OnSelectPhotoListener() {
 *           @Override
 *           public boolean onGallery(PhotoHelper helper, String filePath) {
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
public class PhotoLayout extends GridLayout implements View.OnClickListener {

    private static final String TAG = PhotoLayout.class.getSimpleName() + "|";

    private static final int DEFAULT_TOTAL_NUMBER = 9;
    private static final int DEFAULT_COLUMN_NUMBER = 3;

    /**
     * 容纳ImageView的总数, 默认为 {@link #DEFAULT_TOTAL_NUMBER}, {@link #setTotalNumber(int)}
     */
    private int mTotalNumber;

    /**
     * Grid的列数, 默认为 {@link #DEFAULT_COLUMN_NUMBER}, {@link #setColumnNumber(int)}
     */
    private int mColumnNumber;

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
    private int mCurrentPosition = 1;

    /**
     * ImageView的边距, 默认为2dp, {@link #setMargin(int)}
     */
    private int mMargin;

    /**
     * 是否处于第一个或者最后一个
     */
    private boolean isHeader = true;

    /**
     * 是否是预览图片的形式
     */
    private boolean isPreview;

    private OnAddPhotoListener mOnAddPhotoListener;
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;

    public void setTotalNumber(int totalNumber) {
        mTotalNumber = totalNumber;
    }

    public void setColumnNumber(int columnNumber) {
        mColumnNumber = columnNumber;
    }

    public void setMargin(int margin) {
        mMargin = OsHelper.dp2px(margin);
    }

    public void setOnPhotoListener(OnAddPhotoListener listener) {
        mOnAddPhotoListener = listener;
    }

    /**
     * 如果未设置Item点击事件, 默认点击Item时, 移除view
     *
     * @param onItemClickListener Item点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    /**
     * 图片加载的方式 {@link ImageLoader}, 使用第三方图片加载框架
     *
     * @param imageLoader 图片加载的接口
     */
    public void loader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public void load(List lists) {
        for (Object object : lists) load(object);
    }

    public void load(Object object) {
        loadRatioImageView(object);
    }

    public RatioImageView load() {
        return mCurrentPosition <= mTotalNumber ? addRatioImageView() : null;
    }

    public void remove(View view) {
        removeRatioImageView(view);
    }

    /**
     * 加载图片到RatioImageView
     *
     * @param object 图片
     */
    private void loadRatioImageView(final Object object) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                newRatioImageView(object);
            }
        }, 100);
    }

    /**
     * 创建RatioImageView
     *
     * @param object 图片
     */
    private void newRatioImageView(Object object) {
        RatioImageView imageView = load();
        if (imageView != null) mImageLoader.onLoader(imageView, object);
    }

    public PhotoLayout(Context context) {
        this(context, null);
    }

    public PhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("Recycle") TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.PhotoLayout);

        mTotalNumber = t.getInteger(R.styleable.PhotoLayout_totalNumber, DEFAULT_TOTAL_NUMBER);
        mColumnNumber = t.getInteger(R.styleable.PhotoLayout_columnNumber, DEFAULT_COLUMN_NUMBER);
        mMargin = (int) t.getDimension(R.styleable.PhotoLayout_margin, OsHelper.dp2px(2));
        isPreview = t.getBoolean(R.styleable.PhotoLayout_isPreview, false);
    }

    private void initialize() {
        setColumnCount(mColumnNumber);
        int rowCount = mCurrentPosition / mColumnNumber;
        setRowCount(mCurrentPosition == mTotalNumber ? rowCount : rowCount + 1);
        setOrientation(GridLayout.HORIZONTAL);
    }

    /**
     * 初始化提示View, 初始时 {@link #mCurrentPosition} == 0, 即没有ImageView, 当添加
     * 新的ImageView作为提示用户点击进行添加的ImageView后, 该值变为1, {@link #isHeader}
     * 用于判断是否处于需要移除提示View
     */
    private void initializeEmptyRatioImageView() {
        if (mCurrentPosition == 1) {
            if (isHeader) {
                if (getChildCount() < mTotalNumber) {
                    addView(createEmptyRatioImageView());
                    isHeader = false;
                }
            }
        }
    }

    /**
     * 创建一个空的ImageView
     *
     * @return ImageView
     */
    private RatioImageView createEmptyRatioImageView() {
        mEmptyRatioImageView = new RatioImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mImageWidth, mImageHeight);
        layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
        mEmptyRatioImageView.setLayoutParams(layoutParams);
        BackGHelper.pressed(R.color.colorLightGray, R.color.colorHint).into(mEmptyRatioImageView);
        mEmptyRatioImageView.setImageResource(R.drawable.ic_add);
        mEmptyRatioImageView.setScaleType(RatioImageView.ScaleType.CENTER_CROP);
        mEmptyRatioImageView.setOnClickListener(this);
        return mEmptyRatioImageView;
    }

    /**
     * 为ImageView设置图片, {@link #removeEmptyRatioImageView()} 用于判断添加的ImageView是否
     * 超出 {@link #mTotalNumber}
     *
     * @return ImageView
     */
    private RatioImageView addRatioImageView() {
        RatioImageView imageView = createRatioImageView();
        // 设置View为实际(mCurrentCount - 1)所在的位置, 防止图片混乱
        addView(imageView, mCurrentPosition - 1);
        Logger.d(TAG + "当前ImageView所在的位置: " + mCurrentPosition);
        mCurrentPosition++;
        if (!isPreview) removeEmptyRatioImageView();
        return imageView;
    }

    /**
     * 当添加的图片数达到 {@link #mTotalNumber}, 并且处于最后一个时, 移除提示View
     */
    private void removeEmptyRatioImageView() {
        if (mCurrentPosition - 1 == mTotalNumber) {
            if (!isHeader) {
                removeView(mEmptyRatioImageView);
                mCurrentPosition--;
                isHeader = true;
            }
        }
    }

    /**
     * 创建显示图片的ImageView
     * * @return ImageView
     */
    private RatioImageView createRatioImageView() {
        RatioImageView imageView = new RatioImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mImageWidth, mImageHeight);
        layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // 设置当前View所在的实际位置, 否则图片加载时会产生混乱
        setClickToDelete(imageView, mCurrentPosition - 1);
        return imageView;
    }

    /**
     * 设置图片点击事件, 单击删除图片
     *
     * @param view     点击的view
     * @param position 当前view所在的位置
     */
    private void setClickToDelete(View view, final int position) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d(TAG + "click position is " + position);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, position);
                } else {
                    removeRatioImageView(view);
                }
            }
        });
    }

    /**
     * @param view 移除的View
     */
    private void removeRatioImageView(View view) {
        removeView(view);
        mCurrentPosition--;
        removeDeleteEmptyRatioImageView();
    }

    /**
     * 当图片全部添加完, 移除提示View
     */
    private void removeDeleteEmptyRatioImageView() {
        if (getChildCount() + 1 == mTotalNumber) {
            if (isHeader) {
                addView(createEmptyRatioImageView());
                mCurrentPosition++;
                isHeader = false;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initialize();
        mImageWidth = mImageHeight = (w - 6 * mMargin) / getColumnCount();
        if (!isPreview) initializeEmptyRatioImageView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mEmptyRatioImageView.getId()) {
            if (mOnAddPhotoListener != null) mOnAddPhotoListener.onAdd(this);
        }
    }

    public interface OnAddPhotoListener {
        void onAdd(PhotoLayout layout);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
