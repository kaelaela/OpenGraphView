package me.kaelaela.opengraphview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RoundableImageView extends android.support.v7.widget.AppCompatImageView {

    private int mSide;
    private int mMargin;
    private float mCornerRadius;
    private final RectF mRect = new RectF();
    private final Paint mPaint = new Paint();
    private OpenGraphView.IMAGE_POSITION mPosition = OpenGraphView.IMAGE_POSITION.LEFT;

    public RoundableImageView(Context context) {
        this(context, null);
    }

    public RoundableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setAntiAlias(true);
    }

    public void setMargin(int viewSize, int margin) {
        mMargin = margin;
        mSide = viewSize;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.setMargins(margin, margin, margin, margin);
        params.width = mSide;
        params.height = mSide;
        setLayoutParams(params);
        invalidate();
    }

    public void setRadius(float radius) {
        if (mCornerRadius == radius) {
            return;
        }
        mCornerRadius = radius;
        invalidate();
    }

    public void setPosition(OpenGraphView.IMAGE_POSITION position) {
        mPosition = position;
        setImageParam(mPosition == OpenGraphView.IMAGE_POSITION.LEFT ?
                RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);
        invalidate();
    }

    private void setImageParam(int rule) {
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(mSide, mSide);
        imageParams.addRule(rule);
        imageParams.topMargin = mMargin;
        imageParams.bottomMargin = mMargin;
        setLayoutParams(imageParams);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bm == null) {
            mPaint.reset();
            mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            invalidate();
            return;
        } else {
            mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }

        Bitmap centerCroppedBitmap = ThumbnailUtils.extractThumbnail(bm, mSide, mSide);
        BitmapShader shader = new BitmapShader(centerCroppedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPosition == OpenGraphView.IMAGE_POSITION.LEFT) {
            mRect.set(0, 0, mSide, mSide);
            canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
            mRect.set(mSide - mCornerRadius, 0, mSide, mSide);
            canvas.drawRect(mRect, mPaint);
        } else {
            mRect.set(0, 0, mSide, mSide);
            canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
            mRect.set(0, 0, mCornerRadius, mSide);
            canvas.drawRect(mRect, mPaint);
        }
    }
}
