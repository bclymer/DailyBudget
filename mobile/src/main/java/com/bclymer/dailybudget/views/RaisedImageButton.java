package com.bclymer.dailybudget.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.bclymer.dailybudget.utilities.DisplayUtility;

/**
 * Created by bclymer on 10/19/2014.
 */
public class RaisedImageButton extends ImageButton {

    public RaisedImageButton(Context context) {
        super(context);
    }

    public RaisedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RaisedImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RaisedImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private float firstY = Integer.MIN_VALUE;
    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        if (firstY == Integer.MIN_VALUE) {
            firstY = getY();
        }
        setY(firstY + (pressed ? DisplayUtility.dpToPixels(2) : 0));
    }

}
