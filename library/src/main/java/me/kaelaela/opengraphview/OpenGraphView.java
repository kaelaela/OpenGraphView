package me.kaelaela.opengraphview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.kaelaela.opengraphview.network.model.OGData;
import me.kaelaela.opengraphview.network.tasks.LoadFaviconTask;
import me.kaelaela.opengraphview.network.tasks.LoadImageTask;
import me.kaelaela.opengraphview.network.tasks.LoadOGDataTask;

public class OpenGraphView extends RelativeLayout {

    public static final String TAG = "OpenGraphView";

    private ImageView imageView;
    private String mUrl;
    private OnLoadListener mOnLoadListener;

    public OpenGraphView(Context context) {
        this(context, null);
    }

    public OpenGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OpenGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.view_open_graph, this);
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
        LoadOGDataTask task = new LoadOGDataTask(new LoadOGDataTask.OnLoadListener() {
            @Override
            public void onLoadStart() {
                super.onLoadStart();
                if (mOnLoadListener != null) {
                    mOnLoadListener.onLoadStart();
                }
                loadFavicon(mUrl);
                Log.d(TAG, "start loading");
            }

            @Override
            public void onLoadSuccess(OGData ogData) {
                super.onLoadSuccess(ogData);
                Log.d(TAG, "success loading" + ogData.toString());
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
                Log.d(TAG, "error loading");
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

    private void loadImage(String url) {
        LoadImageTask task = new LoadImageTask(new LoadImageTask.OnLoadListener() {
            @Override
            public void onLoadStart() {
                super.onLoadStart();
                Log.d(TAG, "start image loading");
            }

            @Override
            public void onLoadSuccess(Bitmap bitmap) {
                super.onLoadSuccess(bitmap);
                Log.d(TAG, "success image loading");
                imageView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                imageView.setImageBitmap(bitmap);
                ImageAnimator.alphaAnimation(imageView);
            }

            @Override
            public void onLoadError() {
                super.onLoadError();
                Log.d(TAG, "error image loading");
            }
        });
        task.execute(url);
        if (imageView != null) {
            imageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_gray));
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
                Log.d(TAG, "start favicon loading");
            }

            @Override
            public void onLoadSuccess(Bitmap bitmap) {
                super.onLoadSuccess(bitmap);
                Log.d(TAG, "success favicon loading");
                favicon.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
                favicon.setImageBitmap(bitmap);
                ImageAnimator.alphaAnimation(favicon);
            }

            @Override
            public void onLoadError() {
                super.onLoadError();
                Log.d(TAG, "error favicon loading");
                favicon.setVisibility(GONE);
            }
        });
        task.execute(host);
    }

    private void setOpenGraphData(OGData data) {
        imageView = (ImageView) findViewById(R.id.og_image);
        TextView url = (TextView) findViewById(R.id.og_url);
        TextView title = (TextView) findViewById(R.id.og_title);
        TextView description = (TextView) findViewById(R.id.og_description);
        if (data == null) {
            title.setText(mUrl);
            url.setText("");
            description.setText("");
            imageView.setVisibility(VISIBLE);
            return;
        }

        boolean isImageEmpty = TextUtils.isEmpty(data.getImage());
        if (!isImageEmpty) {
            imageView.setImageURI(Uri.parse(data.getImage()));
        }
        imageView.setVisibility(isImageEmpty ? GONE : VISIBLE);
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
        imageView.setImageDrawable(null);
        ((TextView) findViewById(R.id.og_url)).setText("");
        ((TextView) findViewById(R.id.og_title)).setText("");
        ((TextView) findViewById(R.id.og_description)).setText("");
        ((ImageView) findViewById(R.id.favicon)).setImageURI(Uri.parse(""));
    }
}
