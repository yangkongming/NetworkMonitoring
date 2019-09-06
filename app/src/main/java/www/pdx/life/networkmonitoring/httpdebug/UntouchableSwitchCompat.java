package www.pdx.life.networkmonitoring.httpdebug;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;

/**
 * 此类可以屏蔽SwitchCompat的点击和拖拽行为，避免操作时自动切换状态。
 * 改变此View的状态，可以通过调用{@link #toggle()}/{@link #setChecked(boolean)}方法完成，
 * onClickListener监听点击事件依然有效。
 */
public class UntouchableSwitchCompat extends SwitchCompat {

    private boolean mTouchable = false;
    private int mTouchSlop;
    private OnClickListener mOnClickListener;

    public UntouchableSwitchCompat(Context context) {
        super(context);
        init(context);
    }

    public UntouchableSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UntouchableSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    public void setTouchable(boolean mTouchable) {
        this.mTouchable = mTouchable;
    }

    public boolean isTouchable() {
        return mTouchable;
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    @Override
    public boolean performClick() {
        if (isTouchable()) {
            return super.performClick();
        }
        final boolean result;
        if (isClickable() && mOnClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            mOnClickListener.onClick(this);
            result = true;
        } else {
            result = false;
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isTouchable()) {
            return super.onTouchEvent(ev);
        } else {
            if (isClickable()) {
                final float x = ev.getX();
                final float y = ev.getY();
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (isPressed()) {
                            performClick();
                            setPressed(false);
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        setPressed(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        setPressed(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!pointInView(x, y, mTouchSlop)) {
                            setPressed(false);
                        }
                        break;
                }
                return true;
            }
            return false;
        }
    }


    public boolean pointInView(float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < ((getRight() - getLeft()) + slop) &&
                localY < ((getBottom() - getTop()) + slop);
    }
}
