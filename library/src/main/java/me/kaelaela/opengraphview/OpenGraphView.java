package me.kaelaela.opengraphview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.kaelaela.opengraphview.network.DefaultTaskManager;
import me.kaelaela.opengraphview.network.model.OGData;
import me.kaelaela.opengraphview.network.tasks.BaseTask;
import me.kaelaela.opengraphview.network.tasks.FaviconCallable;
import me.kaelaela.opengraphview.network.tasks.FaviconTask;
import me.kaelaela.opengraphview.network.tasks.ImageCallable;
import me.kaelaela.opengraphview.network.tasks.ImageTask;
import me.kaelaela.opengraphview.network.tasks.OGDataCallable;
import me.kaelaela.opengraphview.network.tasks.OGDataTask;

public class OpenGraphView extends RelativeLayout {

    public enum IMAGE_POSITION {
        LEFT, RIGHT
    }

    @ColorInt
    private int mStrokeColor = -1;
    @ColorInt
    private int mBgColor = -1;
    private int mViewWidth;
    private int mViewHeight;
    private float mStrokeWidth = 0;
    private float mCornerRadius = 0;
    private boolean mSeparate = false;
    private View mSeparator;
    private RoundableImageView mRoundableImageView;
    private ImageView mFavicon;
    private Uri mUri;
    private String mUrl;
    private OnLoadListener mOnLoadListener;
    private RectF mFillRect = new RectF();
    private RectF mStrokeRect = new RectF();
    private Paint mFill = new Paint();
    private Paint mStroke = new Paint();
    private Parser mParser;
    private OGCache mOGCache = OGCache.getInstance();
    private DefaultTaskManager mTaskManager = DefaultTaskManager.getInstance();

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

        mRoundableImageView = (RoundableImageView) findViewById(R.id.og_image);
        mFavicon = (ImageView) findViewById(R.id.favicon);
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
        setStrokeWidth();
        setCornerRadius(array.getDimension(R.styleable.OpenGraphView_cornerRadius, 0));
        int attrPosition = array.getInteger(R.styleable.OpenGraphView_imagePosition, 0);

        setImagePosition((attrPosition == 0 || attrPosition != 1) ? IMAGE_POSITION.LEFT : IMAGE_POSITION.RIGHT);
        mSeparate = array.getBoolean(R.styleable.OpenGraphView_separateImage, true);
        mSeparator.setVisibility(mSeparate ? VISIBLE : GONE);
        mRoundableImageView.setBackgroundColor(array.getColor(R.styleable.OpenGraphView_imagePlaceHolder,
                ContextCompat.getColor(getContext(), R.color.light_gray)));
        findViewById(R.id.favicon).setBackgroundColor(array.getColor(R.styleable.OpenGraphView_faviconPlaceHolder,
                ContextCompat.getColor(getContext(), R.color.light_gray)));
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

    private void setStrokeWidth() {
        mStrokeWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.default_stroke_width);
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mSeparator.getLayoutParams();
        param.width = (int) mStrokeWidth;
        mSeparator.setLayoutParams(param);
        mSeparator.setBackgroundColor(mStrokeColor);

        mFillRect.set(mStrokeWidth, mStrokeWidth, mViewWidth - mStrokeWidth, mViewHeight - mStrokeWidth);
        mStroke.setStrokeWidth(mStrokeWidth);
        int defaultImageSize = getContext().getResources().getDimensionPixelOffset(R.dimen.default_image_size);
        mRoundableImageView.setMargin(defaultImageSize, (int) mStrokeWidth);
        invalidate();
    }

    public void setCornerRadius(float cornerRadius) {
        if (cornerRadius < 0) {
            cornerRadius = 0;
        }
        mCornerRadius = cornerRadius;
        mRoundableImageView.setRadius(cornerRadius);
        invalidate();
    }

    public void setImagePosition(IMAGE_POSITION position) {
        int parentPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.default_stroke_width);
        View parent = findViewById(R.id.parent);
        if (position == IMAGE_POSITION.LEFT) {
            setContentParam(RIGHT_OF);
            setSeparatorParam(RIGHT_OF);
            parent.setPadding(parentPadding, 0, 0, 0);
        } else if (position == IMAGE_POSITION.RIGHT) {
            setContentParam(LEFT_OF);
            setSeparatorParam(LEFT_OF);
            parent.setPadding(0, 0, parentPadding, 0);
        }
        mRoundableImageView.setPosition(position);
    }

    private void setContentParam(int rule) {
        RelativeLayout.LayoutParams contentsParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentsParams.addRule(rule, mRoundableImageView.getId());
        View contents = findViewById(R.id.og_contents);
        contents.setLayoutParams(contentsParams);
        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.default_content_padding);
        contents.setPadding(padding, padding, padding, padding);
    }

    private void setSeparatorParam(int rule) {
        RelativeLayout.LayoutParams separatorParams = new RelativeLayout.LayoutParams(
                (int) mStrokeWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        separatorParams.addRule(rule, mRoundableImageView.getId());
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

    public void setCustomParser(Parser parser) {
        mParser = parser;
    }

    public void loadFrom(@Nullable final String url) {
        if (TextUtils.isEmpty(url) || mSeparator == null || !URLUtil.isNetworkUrl(url)
                || url.equals("http://") || url.equals("https://")) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        setImage(null);
        mUri = Uri.parse(url);
        mUrl = url;
        mSeparator.setVisibility(GONE);
        OGData ogData = mOGCache.get(url);
        if (ogData == null) {
            if (mOnLoadListener != null) {
                mOnLoadListener.onLoadStart();
            }
            mTaskManager.execute(new OGDataTask(new OGDataCallable(url, mParser), new BaseTask.OnLoadListener<OGData>() {
                @Override
                public void onLoadSuccess(OGData ogData) {
                    mOGCache.add(url, ogData);
                    loadImage(ogData.getImage());
                    loadFavicon(mUrl);
                    setOpenGraphData(ogData);
                    if (mOnLoadListener != null) {
                        mOnLoadListener.onLoadFinish();
                    }
                }

                @Override
                public void onLoadError(Throwable e) {
                    if (mOnLoadListener != null) {
                        mOnLoadListener.onLoadError();
                    }
                }
            }));
        } else {
            loadImage(ogData.getImage());
            loadFavicon(mUrl);
            setOpenGraphData(ogData);
        }
    }

    public void loadFrom(Uri uri) {
        if (uri == null) {
            return;
        }
        mUri = uri;
        mUrl = uri.getPath();
        loadFrom(mUrl);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(mStrokeRect, mCornerRadius, mCornerRadius, mStroke);
        canvas.drawRoundRect(mFillRect, mCornerRadius, mCornerRadius, mFill);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        if (mStrokeRect.width() == 0 && mStrokeRect.height() == 0) {
            float padding = mStrokeWidth / 2;
            mStrokeRect.set(padding, padding, mViewWidth - padding, mViewHeight - padding);
        }
    }

    private void loadImage(final String url) {
        Bitmap bitmap = mOGCache.getImage(url);
        if (bitmap == null) {
            mTaskManager.execute(new ImageTask(new ImageCallable(url), new BaseTask.OnLoadListener<Bitmap>() {
                @Override
                public void onLoadSuccess(Bitmap bitmap) {
                    mOGCache.addImage(url, bitmap);
                    setImage(bitmap);
                }

                @Override
                public void onLoadError(Throwable e) {
                    setImage(null);
                }
            }));
        } else {
            setImage(bitmap);
        }
    }

    private void setImage(@Nullable Bitmap bitmap) {
        mRoundableImageView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        mRoundableImageView.setVisibility(bitmap == null ? GONE : VISIBLE);
        mSeparator.setVisibility(bitmap == null ? GONE : VISIBLE);
        mRoundableImageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            ImageAnimator.alphaAnimation(mRoundableImageView);
        }
        if (mSeparate) {
            mSeparator.setVisibility(VISIBLE);
        }
    }

    private void loadFavicon(String url) {
        Uri uri = Uri.parse(url);
        final String host = uri.getHost().startsWith("www.") ? uri.getHost().substring(4) : uri.getHost();
        Bitmap favicon = mOGCache.getImage(host);
        if (favicon == null) {
            mTaskManager.execute(new FaviconTask(new FaviconCallable(host), new BaseTask.OnLoadListener<Bitmap>() {
                @Override
                public void onLoadSuccess(Bitmap bitmap) {
                    mOGCache.addImage(host, bitmap);
                    setFavicon(bitmap);
                }

                @Override
                public void onLoadError(Throwable e) {
                    setFavicon(null);
                }
            }));
        } else {
            setFavicon(favicon);
        }
    }

    private void setFavicon(@Nullable Bitmap bitmap) {
        mFavicon.setVisibility(bitmap == null ? GONE : VISIBLE);
        mFavicon.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        mFavicon.setImageBitmap(bitmap);
        if (bitmap != null) {
            ImageAnimator.alphaAnimation(mFavicon);
        }
    }

    private void setOpenGraphData(OGData data) {
        TextView url = (TextView) findViewById(R.id.og_url);
        TextView title = (TextView) findViewById(R.id.og_title);
        TextView description = (TextView) findViewById(R.id.og_description);
        if (data == null) {
            title.setText(mUrl);
            url.setText("");
            description.setText("");
            mRoundableImageView.setVisibility(GONE);
            return;
        }

        boolean isImageEmpty = TextUtils.isEmpty(data.getImage());
        mRoundableImageView.setVisibility(isImageEmpty ? GONE : VISIBLE);
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

    public Uri getUri() {
        return mUri;
    }

    public void clear() {
        mRoundableImageView.setImageResource(0);
        ((TextView) findViewById(R.id.og_url)).setText("");
        ((TextView) findViewById(R.id.og_title)).setText("");
        ((TextView) findViewById(R.id.og_description)).setText("");
        ((ImageView) findViewById(R.id.favicon)).setImageResource(0);
    }
}
