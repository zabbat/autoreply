package net.wandroid.answer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class AnimatorRelativeLayout extends RelativeLayout {
    public AnimatorRelativeLayout(Context context) {
        super(context);
    }

    public AnimatorRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatorRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float getXFraction() {
        return getWidth() == 0 ? 0 : getX() / getWidth();
    }

    public void setXFraction(float x) {
        final int width = getWidth();
        setX((width > 0) ? (x * width) : 0);
    }
}
