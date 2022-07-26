package com.bayzid.qrbarcodescanner2022.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

public class Statusbar extends View {
    private int mStatusBarHeight;

    public Statusbar(Context context) {
        this(context, null);
    }

    public Statusbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBarHeight = dpToPx();
            return insets.consumeSystemWindowInsets();
        }
        return insets;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mStatusBarHeight);
    }

    private int dpToPx() {
        float density = Resources.getSystem().getDisplayMetrics().density;
        int i =0;
        if (density>2){
            i = Math.round((float) 38.0 * density);
        }else if(density<=2){
            i = Math.round((float) 24.0 * density);
        }
        Log.d("StatusDesn", String.valueOf(i));
        return i;
    }
}

