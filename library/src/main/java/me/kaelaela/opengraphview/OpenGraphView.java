package me.kaelaela.opengraphview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.kaelaela.opengraphview.network.model.OGData;
import me.kaelaela.opengraphview.network.tasks.LoadFaviconTask;
import me.kaelaela.opengraphview.network.tasks.LoadImageTask;
import me.kaelaela.opengraphview.network.tasks.LoadOGDataTask;

public class OpenGraphView extends RelativeLayout {

    public static final int IMAGE_POS_LEFT = 0;
    public static final int IMAGE_POS_RIGHT = 1;

    @ColorInt
    private int mStrokeColor = -1;
    @ColorInt
    private int mBgColor = -1;
    private int mViewWidth;
    private int mViewHeight;
    private float mStrokeWidth = 0;
    private boolean mSeparate = false;
    private View mSeparator;
    private ImageView mImageView;
    private String mUrl;
    private OnLoadListener mOnLoadListener;
    private Paint mFill = new Paint();
    private Paint mStroke = new Paint();

    public OpenGraphView(Context context) {
        this(context, null);
    }

    public OpenGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OpenGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.view_open_graph, this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.OpenGraphView, defStyleAttr, 0);
        if (attrs == null) {
            return;
        }
        mSeparator = findViewById(R.id.separator);
        setWillNotDraw(false);
        mFill.setStyle(Paint.Style.FILL);
        mFill.setAntiAlias(true);
        mFill.setColor(ContextCompat.getColor(context, android.R.color.white));

        mStroke.setStyle(Paint.Style.STROKE);
        mStroke.setAntiAlias(true);
        mStroke.setColor(ContextCompat.getColor(context, R.color.light_gray));

        mImageView = (ImageView) findViewById(R.id.og_image);
        setBgColor(array.getColor(R.styleable.OpenGraphView_bgColor,
                ContextCompat.getColor(context, android.R.color.white)));
        ((TextView) findViewById(R.id.og_title)).setTextColor(
                array.getColor(R.styleable.OpenGraphView_titleColor,
                        ContextCompat.getColor(context, R.color.text_black)));
        ((TextView) findViewById(R.id.og_description)).setTextColor(
                array.getColor(R.styleable.OpenGraphView_descTextColor,
                        ContextCompat.getColor(context, R.color.text_black)));
        ((TextView) findViewById(R.id.og_url)).setTextColor(
                array.getColor(R.styleable.OpenGraphView_urlTextColor,
                        ContextCompat.getColor(context, R.color.base_gray)));
        setStrokeColor(array.getColor(R.styleable.OpenGraphView_strokeColor,
                ContextCompat.getColor(context, R.color.light_gray)));
        setStrokeWidth(array.getDimension(R.styleable.OpenGraphView_strokeWidth, 2f));
        setImagePosition(array.getInteger(R.styleable.OpenGraphView_imagePosition, IMAGE_POS_LEFT));
        mSeparate = array.getBoolean(R.styleable.OpenGraphView_separateImage, true);
        mSeparator.setVisibility(mSeparate ? VISIBLE : GONE);
        mImageView.setImageDrawable(array.getDrawable(R.styleable.OpenGraphView_imagePlaceHolder));
        ((ImageView) findViewById(R.id.favicon))
                .setImageDrawable(array.getDrawable(R.styleable.OpenGraphView_faviconPlaceHolder));
        array.recycle();
    }

    public void setBgColor(@ColorInt int bgColor) {
        mBgColor = bgColor;
        mFill.setColor(mBgColor);
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        mStrokeColor = strokeColor;
        mStroke.setColor(strokeColor);
    }

    public void setStrokeWidth(float strokeWidth) {
        if (strokeWidth < 0) {
            mStrokeWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.default_stroke_width);
            return;
        }
        RelativeLayout.LayoutParams sepaParam = (RelativeLayout.LayoutParams) mSeparator.getLayoutParams();
        sepaParam.width = (int) strokeWidth;
        mSeparator.setLayoutParams(sepaParam);
        mSeparator.setBackgroundColor(mStrokeColor);

        mStrokeWidth = strokeWidth;
        mStroke.setStrokeWidth(strokeWidth);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
        int margin = (int) (mStrokeWidth);
        params.setMargins(margin, margin, margin, margin);
        params.width = params.width - margin * 2;
        params.height = params.height - margin * 2;
        mImageView.setLayoutParams(params);
        mImageView.setTranslationX(margin / 2);
        invalidate();
    }

    /**
     * set image position.
     *
     * @param position IMAGE_POS_LEFT(=0), IMAGE_POS_RIGHT(=1)
     */
    public void setImagePosition(int position) {
        int parentPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.default_stroke_width);
        View parent = findViewById(R.id.parent);
        if (position == IMAGE_POS_LEFT) {
            setImageParam(ALIGN_PARENT_LEFT);
            setContentParam(RIGHT_OF);
            setSeparatorParam(RIGHT_OF);
            parent.setPadding(parentPadding, 0, 0, 0);
        } else if (position == IMAGE_POS_RIGHT) {
            setImageParam(ALIGN_PARENT_RIGHT);
            setContentParam(LEFT_OF);
            setSeparatorParam(LEFT_OF);
            parent.setPadding(0, 0, parentPadding, 0);
        }
    }

    private void setImageParam(int rule) {
        Resources resources = getContext().getResources();
        int imageSize = resources.getDimensionPixelSize(R.dimen.default_image_size);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(imageSize, imageSize);
        imageParams.addRule(rule);
        int margin = resources.getDimensionPixelOffset(R.dimen.default_stroke_width);
        imageParams.topMargin = margin;
        imageParams.bottomMargin = margin;
        mImageView.setLayoutParams(imageParams);
    }

    private void setContentParam(int rule) {
        RelativeLayout.LayoutParams contentsParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentsParams.addRule(rule, mImageView.getId());
        View contents = findViewById(R.id.og_contents);
        contents.setLayoutParams(contentsParams);
        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.default_content_padding);
        contents.setPadding(padding, padding, padding, padding);
    }

    private void setSeparatorParam(int rule) {
        RelativeLayout.LayoutParams separatorParams = new RelativeLayout.LayoutParams(
                (int) mStrokeWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        separatorParams.addRule(rule, mImageView.getId());
        mSeparator.setLayoutParams(separatorParams);
    }

    public void separateImage(boolean separate) {
        mSeparate = separate;
        if (mSeparator == null) {
            return;
        }
        mSeparator.setVisibility(separate ? VISIBLE : GONE);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mOnLoadListener = listener;
    }

    public void loadFrom(@Nullable String url) {
        setVisibility(TextUtils.isEmpty(url) ? GONE : VISIBLE);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mUrl = url;
        if (mSeparator == null) {
            return;
        }
        mSeparator.setVisibility(GONE);
        LoadOGDataTask task = new LoadOGDataTask(new LoadOGDataTask.OnLoadListener() {
            @Override
            public void onLoadStart() {
                super.onLoadStart();
                if (mOnLoadListener != null) {
                    mOnLoadListener.onLoadStart();
                }
                loadFavicon(mUrl);
            }

            @Override
            public void onLoadSuccess(OGData ogData) {
                super.onLoadSuccess(ogData);
                loadImage(ogData.getImage());
                setOpenGraphData(ogData);
                if (mOnLoadListener != null) {
                    mOnLoadListener.onLoadFinish();
                }
            }

            @Override
            public void onLoadError() {
                super.onLoadError();
                if (mOnLoadListener != null) {
                    mOnLoadListener.onLoadError();
                }
            }
        });
        task.execute(url);
    }

    public void loadFrom(Uri uri) {
        if (uri == null) {
            return;
        }
        mUrl = uri.getPath();
        loadFrom(mUrl);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float padding = mStrokeWidth / 2;
        canvas.drawRect(padding, padding, mViewWidth - padding, mViewHeight - padding, mStroke);
        canvas.drawRect(mStrokeWidth, mStrokeWidth, mViewWidth - mStrokeWidth, mViewHeight - mStrokeWidth, mFill);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    private void loadImage(String url) {
        LoadImageTask task = new LoadImageTask(new LoadImageTask.OnLoadListener() {
            @Override
            public void onLoadStart() {
                super.onLoadStart();
            }

            @Override
            public void onLoadSuccess(Bitmap bitmap) {
                super.onLoadSuccess(bitmap);
                mImageView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                mImageView.setImageBitmap(bitmap);
                ImageAnimator.alphaAnimation(mImageView);
                if (mSeparate) {
                    mSeparator.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onLoadError() {
                super.onLoadError();
            }
        });
        task.execute(url);
        if (mImageView != null) {
            mImageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_gray));
        }
    }

    private void loadFavicon(String url) {
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (host != null) {
            host = host.startsWith("www.") ? host.substring(4) : host;
        }
        final ImageView favicon = (ImageView) findViewById(R.id.favicon);
        LoadFaviconTask task = new LoadFaviconTask(new LoadFaviconTask.OnLoadListener() {
            @Override
            public void onLoadStart() {
                super.onLoadStart();
            }

            @Override
            public void onLoadSuccess(Bitmap bitmap) {
                super.onLoadSuccess(bitmap);
                favicon.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                favicon.setImageBitmap(bitmap);
                ImageAnimator.alphaAnimation(favicon);
            }

            @Override
            public void onLoadError() {
                super.onLoadError();
                favicon.setVisibility(GONE);
            }
        });
        task.execute(host);
    }

    private void setOpenGraphData(OGData data) {
        TextView url = (TextView) findViewById(R.id.og_url);
        TextView title = (TextView) findViewById(R.id.og_title);
        TextView description = (TextView) findViewById(R.id.og_description);
        if (data == null) {
            title.setText(mUrl);
            url.setText("");
            description.setText("");
            mImageView.setVisibility(VISIBLE);
            return;
        }

        boolean isImageEmpty = TextUtils.isEmpty(data.getImage());
        if (!isImageEmpty) {
            mImageView.setImageURI(Uri.parse(data.getImage()));
        }
        mImageView.setVisibility(isImageEmpty ? GONE : VISIBLE);
        if (TextUtils.isEmpty(data.getTitle()) && TextUtils.isEmpty(data.getDescription())) {
            title.setText(mUrl);
            description.setText("");
        } else {
            title.setText(data.getTitle());
            description.setText(data.getDescription());
        }

        Uri uri = Uri.parse(mUrl);
        String host = uri.getHost();
        url.setText(host == null ? mUrl : host);
    }

    public String getUrl() {
        return mUrl;
    }

    public void clear() {
        mImageView.setImageDrawable(null);
        ((TextView) findViewById(R.id.og_url)).setText("");
        ((TextView) findViewById(R.id.og_title)).setText("");
        ((TextView) findViewById(R.id.og_description)).setText("");
        ((ImageView) findViewById(R.id.favicon)).setImageURI(Uri.parse(""));
    }
}
