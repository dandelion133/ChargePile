package com.kunpeng.ev.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by QHF on 2016/6/28.
 */
public class FlexibleScrollView extends ScrollView {
    private int mMaxOverDistance = 200;

    public FlexibleScrollView(Context context) {
        super(context);
    }

    public FlexibleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlexibleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxOverDistance, isTouchEvent);
    }
}
