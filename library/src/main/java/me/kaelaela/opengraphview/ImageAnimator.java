package me.kaelaela.opengraphview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

public class ImageAnimator {

    private static final int DEFAULT_DURATION = 755;

    public static void alphaAnimation(final View view) {
        alphaAnimation(view, DEFAULT_DURATION);
    }

    public static void alphaAnimation(final View view, int duration) {
        final ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0f, 1f);
        alphaAnimator.setInterpolator(new FastOutSlowInInterpolator());
        alphaAnimator.setDuration(duration);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                if (view != null) {
                    view.setAlpha(alpha);
                }
            }
        });
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimator.start();
    }
}
