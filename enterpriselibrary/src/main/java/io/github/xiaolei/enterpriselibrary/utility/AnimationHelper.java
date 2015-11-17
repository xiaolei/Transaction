package io.github.xiaolei.enterpriselibrary.utility;

import android.text.TextUtils;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.math.BigDecimal;

/**
 * TODO: add comment
 */
public class AnimationHelper {
    public static void displayPrice(final TextView textView, final BigDecimal price, final String currencyCode) {
        if (textView == null || price == null || TextUtils.isEmpty(currencyCode)) {
            return;
        }

        if (price.equals(BigDecimal.ZERO)) {
            textView.setText(CurrencyHelper.formatCurrency(currencyCode, price));
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, CurrencyHelper.castToInteger(price));
        valueAnimator.setDuration(500);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = Integer.parseInt(animation.getAnimatedValue().toString());
                String text = CurrencyHelper.formatCurrency(currencyCode, value);
                textView.setText(text);
            }
        });
        valueAnimator.start();
    }
}
